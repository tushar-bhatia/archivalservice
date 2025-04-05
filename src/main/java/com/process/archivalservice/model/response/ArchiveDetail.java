package com.process.archivalservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArchiveDetail {
    String tableName;
    List<Map<String, Object>> tableData;
}
