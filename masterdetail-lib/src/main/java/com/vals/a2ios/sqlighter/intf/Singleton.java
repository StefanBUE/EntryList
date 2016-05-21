package com.vals.a2ios.sqlighter.intf;

import com.vals.a2ios.sqlighter.intf.SQLighterDb;

/**
 * Singleton class ot initialize SQLite
 * and interfaces that give access to it.
 *
 * This class is j2objc'd into objective c
 * and you should see its counterpart in j2objc
 * demo prj.
 */
public class Singleton {
    private static Singleton singleton;
    
    private Singleton() {}
    
    public static synchronized Singleton getInstance() {
        if(singleton == null) {
            singleton = new Singleton();
        }
        return singleton;
    }
    private SQLighterDb sqLighterDb;

    public SQLighterDb getSqLighterDb() {
        return sqLighterDb;
    }

    public void setSqLighterDb(SQLighterDb sqLighterDb) {
        this.sqLighterDb = sqLighterDb;
    }
}
