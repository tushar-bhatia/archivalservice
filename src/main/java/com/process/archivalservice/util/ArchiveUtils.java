package com.process.archivalservice.util;

import com.process.archivalservice.model.Column;
import com.process.archivalservice.model.Configuration;
import com.process.archivalservice.model.Row;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArchiveUtils {

    private ArchiveUtils(){}

    public static Timestamp getLastTimestamp(Configuration configuration) {
        return Timestamp.valueOf(LocalDateTime.now()
                .minusYears(configuration.getYears())
                .minusMonths(configuration.getMonths())
                .minusWeeks(configuration.getWeeks())
                .minusDays(configuration.getDays())
                .minusHours(configuration.getHours())
                .minusMinutes(configuration.getMinutes()));
    }

    public static List<Row> parse(ResultSet rs) throws SQLException {
        List<Row> rows = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        List<Column> columns = new ArrayList<>();
        int columnCount = metaData.getColumnCount();
        for(int i=1; i<=columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            Object value = rs.getObject(i);
            columns.add(new Column(columnName, value));
        }
        rows.add(new Row(columns));
        return rows;
    }
}
