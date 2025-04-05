package com.process.archivalservice.dao;

import com.process.archivalservice.model.*;
import com.process.archivalservice.util.ArchiveUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class ArchivalDao {

    @Autowired
    @Qualifier("databaseTemplate")
    NamedParameterJdbcTemplate databaseTemplate;

    @Autowired
    @Qualifier("configurationRepository")
    ConfigurationRepository configurationRepository;

    @Value("${batch.size}")
    int batchSize;

    private final String getEligibleArchiveDataQuery = "SELECT * FROM %s.%s WHERE ARCHIVED<=:timestamp";

    private final String getEligibleDataQuery = "SELECT * FROM %s.%s WHERE UPDATED<=:timestamp";

    private final String getGeoLocationsQuery = "SELECT DISTINCT LOCATION FROM core.GEOLOCATION";

    private final String archiveQueryTemplate = "INSERT INTO %s.%s (%s) VALUES(%s)";

    private final String deleteQueryTemplate = "DELETE FROM %s.%s WHERE %s";

    private final String getArchiveDataQuery = "SELECT * FROM %s.%s";

    private final String getUserRolesQuery = "select distinct p.ROLE_NAME from core.user u join core.permission p on u.NAME=p.USER_NAME where u.NAME=:user and u.password=:password";

    /***
     * fetches all the policies configured in teh system for a given configuration
     * @param type tells about the type of policy we are looking. Can we either of ARCHIVAL/DELETION
     * @return retuns the list of all the policies configured for a given type.
     */
    public List<Configuration> getAllConfiguration(ConfigType type) {
        return configurationRepository.findByConfigurationType(type.name());
    }

    /***
     * Fetches the data for a given table which is eligible for the archival.
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
     * Fetches the data for a given table which is eligible for deletion from archive table.
     * @param tableName table in which data needs to be evaluated.
     * @param location geo location at which table resides.
     * @param maxAllowedTimestamp parameter which tells what's the max duration we can keep the data.
     * @return fetches the rows which are older then maxAllowedTimestamp
     */
    public List<Row> getEligibleRowsForDeletion(String tableName, String location, Timestamp maxAllowedTimestamp) {
        List<Row> rows = new ArrayList<>();
        String eligibleQuery = String.format(getEligibleArchiveDataQuery, location, tableName);
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

    /***
     * Responsible to view the data from a given table from a given location
     * @param tableName table from which data needs to be fetched
     * @param location location at which the table resides
     * @return list of rows containing column and their values
     */
    public List<Row> getDataFromTable(String tableName, String location) {
        String query = String.format(getArchiveDataQuery, location, tableName);
        List<Row> rows = new ArrayList<>();
        databaseTemplate.query(query, new MapSqlParameterSource(), rs -> {
            rows.addAll(ArchiveUtils.parse(rs));
        });
        return rows;
    }


    /***
     * This method is use to fetch the configured roles for a given user. If the list is empty, then,
     * it typically implies that either user with the given name does not exist or the given password is incorrect.
     * @param user User for which roles needs to be fetched.
     * @param password Passowrd for teh given user.
     * @return Set of configured roles if both user and password matches.
     */
    public Set<String> getUserDetails(String user, String password) {
        Set<String> roles = new HashSet<>();
        databaseTemplate.query(getUserRolesQuery, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                roles.add(rs.getString("ROLE_NAME"));
            }
        });
        return roles;
    }
}
