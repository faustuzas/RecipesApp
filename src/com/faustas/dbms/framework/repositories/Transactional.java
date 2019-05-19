package com.faustas.dbms.framework.repositories;

public interface Transactional {

    void startTransaction();

    void commit();

    void rollback();
}
