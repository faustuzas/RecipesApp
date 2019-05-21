package com.faustas.dbms.framework.interfaces;

public interface Transactional {

    void startTransaction();

    void commit();

    void rollback();
}
