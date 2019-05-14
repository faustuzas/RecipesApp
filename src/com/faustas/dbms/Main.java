package com.faustas.dbms;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.interfaces.Bootable;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext(Main.class);
        context.getBean(Bootable.class).boot();
    }
}
