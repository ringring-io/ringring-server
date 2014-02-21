package com.zirgoo.server.persistence;

import java.sql.Connection;

/**
 * Created by kosztope on 23/01/14.
 */
public interface ConnectionManager {
    Connection getConnection();
}
