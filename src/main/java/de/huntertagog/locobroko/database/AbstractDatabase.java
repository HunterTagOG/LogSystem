package de.huntertagog.locobroko.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The AbstractDatabase class provides an abstract interface for managing island data in a database.
 * <p>
 * This class defines the essential methods required for saving, loading, and removing island data,
 * as well as managing database connections and setup.
 */
public abstract class AbstractDatabase {

    /**
     * Closes any active database connections.
     * <p>
     * This method is responsible for closing any open connections to the database,
     * ensuring that resources are properly released. It should be called when the
     * database operations are complete and the application is shutting down.
     */
    public abstract void close();

    /**
     * Obtains a connection to the database.
     * <p>
     * This private method is responsible for establishing a connection to the database.
     * Implementations should override this method to provide the necessary logic for
     * connecting to the specific database being used.
     *
     * @return a Connection object representing the database connection.
     */
    public Connection getConnection() throws SQLException {
        return null;
    }
}
