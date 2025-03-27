package net.slimediamond.atom.data.dao;

import java.sql.SQLException;

/**
 * A DAO for the Atom database.
 */
public interface DAO {
    /**
     * Save the DAO to the database
     */
    void save() throws SQLException;

    /**
     * Get the primary key for the DAO
     *
     * @return DAO primary key
     */
    int getPrimaryKey();
}
