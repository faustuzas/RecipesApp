package com.faustas.dbms.framework.repositories;

import java.util.Map;

public class ProcessedQuery {

    private final String preparedQuery;

    private final Map<Integer, Object> positionalParams;

    public ProcessedQuery(String preparedQuery, Map<Integer, Object> positionalParams) {
        this.preparedQuery = preparedQuery;
        this.positionalParams = positionalParams;
    }

    public String getPreparedQuery() {
        return preparedQuery;
    }

    public Map<Integer, Object> getPositionalParams() {
        return positionalParams;
    }
}
