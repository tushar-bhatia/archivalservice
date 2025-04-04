package com.process.archivalservice.dao;

import com.process.archivalservice.model.Column;
import com.process.archivalservice.model.ConfigType;
import com.process.archivalservice.model.Configuration;
import com.process.archivalservice.model.Row;
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

    @Value("${archival.batch.size}")
    int archivalBatchSize;

    private final String getConfigQuery = "SELECT * FROM core.CONFIGURATION WHERE CONFIGURATION_TYPE=:type";

    private final String getTableDataQuery = "SELECT * FROM main.%s WHERE UPDATED<=:timestamp";

    private final String getGeoLocationsQuery = "SELECT DISTINCT LOCATION FROM core.GEOLOCATION";

    private final String archiveQueryTemplate = "INSERT INTO %s.%s (%s) VALUES(%s)";

    private final String deleteQueryTemplate = "DELETE FROM %s.%s WHERE %s";

    public List<Configuration> getAllConfiguration(ConfigType type) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("type", type.name());
        return databaseTemplate.query(getConfigQuery, params, new RowMapper<Configuration>() {
            @Override
            public Configuration mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Configuration.builder()
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
                        .build();
            }
        });
    }


    public List<Row> getResults(String tableName, Timestamp maxAllowedTimestamp) {
        List<Row> rows = new ArrayList<>();
        String finalQueryToBeUsed = String.format(getTableDataQuery, tableName);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("timestamp", maxAllowedTimestamp);
        databaseTemplate.query(finalQueryToBeUsed, params, rs -> {
            ResultSetMetaData metaData = rs.getMetaData();
            List<Column> columns = new ArrayList<>();
            int columnCount = metaData.getColumnCount();
            for(int i=1; i<=columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = rs.getObject(i);
                columns.add(new Column(columnName, value));
            }
            rows.add(new Row(columns));
        });
        return rows;
    }

    public List<String> getGeoLocations() {
        return databaseTemplate.queryForList(getGeoLocationsQuery, Collections.emptyMap(), String.class);
    }

    public void archiveData(List<Row> rows, String tableName, String geoLocation) {
        List<String> columns = rows.get(0).getColumns().stream().map(Column::getColumnName).toList();
        String archiveQuery = getArchiveQuery(columns, tableName, geoLocation);
        dbUpdate(archiveQuery, rows);
    }

    private String getArchiveQuery(List<String> columns, String table, String schema) {
        String columnNames = String.join(",", columns);
        String placeholders = columns.stream().map(c -> ":"+c).collect(Collectors.joining(","));
        return String.format(archiveQueryTemplate, schema, table, columnNames, placeholders);
    }

    public void deleteData(List<Row> rows, String tableName, String geoLocation) {
        List<String> columns = rows.get(0).getColumns().stream().map(Column::getColumnName).toList();
        String deleteQuery = getDeleteQuery(columns, tableName, geoLocation);
        dbUpdate(deleteQuery, rows);
    }

    private String getDeleteQuery(List<String> columns, String table, String schema) {
        String columnValue = columns.stream().map(c -> c+"=:"+c).collect(Collectors.joining(" AND "));
        return String.format(deleteQueryTemplate, schema, table, columnValue);
    }

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
            if(arguments.size() == archivalBatchSize) {
                databaseTemplate.batchUpdate(query, arguments.toArray(new MapSqlParameterSource[0]));
                arguments.clear();
            }
        }
        if(!arguments.isEmpty()) databaseTemplate.batchUpdate(query, arguments.toArray(new MapSqlParameterSource[0]));
    }
}
