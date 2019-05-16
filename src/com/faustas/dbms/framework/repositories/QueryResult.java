package com.faustas.dbms.framework.repositories;

import java.sql.Statement;

public class QueryResult {

    private final Statement statement;

    private final Object result;

    public QueryResult(Statement statement, Object result) {
        this.statement = statement;
        this.result = result;
    }

    public Statement getStatement() {
        return statement;
    }

    public Object getResult() {
        return result;
    }
}
