package net.slimediamond.atom.data;

import com.google.inject.Inject;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.reference.DBReference;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service(value = "database2", priority = 99999)
public class DatabaseV2 {
    @Inject
    private Logger logger;

    private Connection conn;

    @Service.Start
    public void open() throws SQLException {
        logger.info("Opening connection");

        conn = DriverManager.getConnection(
                "jdbc:mariadb://" +
                        DBReference.host +
                        ":" +
                        DBReference.port +
                        "/" +
                        DBReference.database +
                        "?user=" +
                        DBReference.user +
                        "&password=" +
                        DBReference.password +
                        "&autoReconnect=true&allowPublicKeyRetrieval=true");
    }

    public Connection getConn() {
        return conn;
    }
}
