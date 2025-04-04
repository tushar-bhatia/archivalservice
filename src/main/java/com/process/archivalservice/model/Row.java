package com.process.archivalservice.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public final class Row {
    List<Column> columns;
}
