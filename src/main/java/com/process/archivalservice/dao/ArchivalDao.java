package com.process.archivalservice.dao;

import com.process.archivalservice.model.Column;
import com.process.archivalservice.model.ConfigType;
import com.process.archivalservice.model.Configuration;
import com.process.archivalservice.model.Row;
import com.process.archivalservice.util.ArchiveUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class ArchivalDao {

    @Autowired
    @Qualifier("databaseTemplate")
    NamedParameterJdbcTemplate databaseTemplate;

    @Value("${batch.size}")
    int batchSize;

    private final String getConfigQuery = "SELECT * FROM core.CONFIGURATION WHERE CONFIGURATION_TYPE=:type";

    private final String getEligibleDataQuery = "SELECT * FROM %s.%s WHERE UPDATED<=:timestamp";

    private final String getGeoLocationsQuery = "SELECT DISTINCT LOCATION FROM core.GEOLOCATION";

    private final String archiveQueryTemplate = "INSERT INTO %s.%s (%s) VALUES(%s)";

    private final String deleteQueryTemplate = "DELETE FROM %s.%s WHERE %s";

    /***
     * fetches all the policies configured in teh system for a given configuration
     * @param type tells about the type of policy we are looking. Can we either of ARCHIVAL/DELETION
     * @return retuns the list of all the policies configured for a given type.
     */
    public List<Configuration> getAllConfiguration(ConfigType type) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("type", type.name());
        return databaseTemplate.query(getConfigQuery, params, (rs, rowNum) -> Configuration.builder()
                .id(rs.getInt("ID"))
                .tableName(rs.getString("TABLE_NAME"))
                .configurationType(rs.getString("CONFIGURATION_TYPE"))
                .years(rs.getInt("YEARS"))
                .months(rs.getInt("MONTHS"))
                .weeks(rs.getInt("WEEKS"))
                .days(rs.getInt("DAYS"))
                .hours(rs.getInt("HOURS"))
                .minutes(rs.getInt("MINUTES"))
                .created(rs.getTimestamp("CREATED"))
                .updated(rs.getTimestamp("UPDATED"))
                .build());
    }

    /***
     * fetches the data for a given table which is eligible for the archival.
     * @param tableName table eligible for the archival
     * @param location geo location at which data needs to be evaluated.
     * @param maxAllowedTimestamp parameter which tells what's the max duration we can keep the data.
     * @return fetches the rows which are older then maxAllowedTimestamp
     */
    public List<Row> getEligibleRows(String tableName, String location, Timestamp maxAllowedTimestamp) {
        List<Row> rows = new ArrayList<>();
        String eligibleQuery = String.format(getEligibleDataQuery, location, tableName);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("timestamp", maxAllowedTimestamp);
        databaseTemplate.query(eligibleQuery, params, rs -> {
            rows.addAll(ArchiveUtils.parse(rs));
        });
        return rows;
    }

    /***
     * responsible for fetching different geo locations configured.
     * @return the different geo location configured for the archival.
     */
    public List<String> getGeoLocations() {
        return databaseTemplate.queryForList(getGeoLocationsQuery, Collections.emptyMap(), String.class);
    }

    /***
     * responsible for archiving the data from a given table into the given geo location.
     * @param rows actual data which needs to be archived.
     * @param tableName table name at which the data needs to be inserted.
     * @param geoLocation geo location at which the database resides.
     */
    public void archiveData(List<Row> rows, String tableName, String geoLocation) {
        List<String> columns = rows.get(0).getColumns().stream().map(Column::getColumnName).toList();
        String archiveQuery = getArchiveQuery(columns, tableName, geoLocation);
        dbUpdate(archiveQuery, rows);
    }

    /***
     * generates the formatted archive query based on the args provided.
     * @param columns list of columns to be injected into the query.
     * @param table table name to be injected into the query.
     * @param schema schema name to be injected into the query.
     * @return returns the formatted query consist of column names and placeholder ofr the values.
     */
    private String getArchiveQuery(List<String> columns, String table, String schema) {
        String columnNames = String.join(",", columns);
        String placeholders = columns.stream().map(c -> ":"+c).collect(Collectors.joining(","));
        return String.format(archiveQueryTemplate, schema, table, columnNames, placeholders);
    }

    /***
     * Responsible for deleting the data from the given table for a given schema.
     * @param rows carries actual arguments which needs to be deleted.
     * @param tableName table name on which deletion query needs to be run.
     * @param geoLocation location at which query needs to be run.
     */
    public void deleteData(List<Row> rows, String tableName, String geoLocation) {
        List<String> columns = rows.get(0).getColumns().stream().map(Column::getColumnName).toList();
        String deleteQuery = getDeleteQuery(columns, tableName, geoLocation);
        dbUpdate(deleteQuery, rows);
    }

    /***
     * Generates the formatted delete query based on the args provided.
     * @param columns list of columns for which the query needs to be formatted.
     * @param table table name to be injected into the query.
     * @param schema schema name to be injected into the query,
     * @return formated query consist of appropriate placeholders with column names.
     */
    private String getDeleteQuery(List<String> columns, String table, String schema) {
        String columnValue = columns.stream().map(c -> c+"=:"+c).collect(Collectors.joining(" AND "));
        return String.format(deleteQueryTemplate, schema, table, columnValue);
    }

    /***
     * responsible for actual database operation.
     * @param query This is the formatted query templated consist of placeholders.
     * @param rows This has actual arguments which needs to be injected into the query.
     */
    private void dbUpdate(String query, List<Row> rows) {
        List<MapSqlParameterSource> arguments = new ArrayList<>();
        int counter = 0;
        for(int i=0; i<rows.size(); i++, counter++) {
            Row row = rows.get(i);
            MapSqlParameterSource params = new MapSqlParameterSource();
            for(int j=0; j<row.getColumns().size(); j++) {
                Column col = row.getColumns().get(j);
                params.addValue(col.getColumnName(), col.getValue());
            }
            arguments.add(params);
            if(arguments.size() == batchSize) {
                databaseTemplate.batchUpdate(query, arguments.toArray(new MapSqlParameterSource[0]));
                arguments.clear();
            }
        }
        if(!arguments.isEmpty()) databaseTemplate.batchUpdate(query, arguments.toArray(new MapSqlParameterSource[0]));
    }
}
