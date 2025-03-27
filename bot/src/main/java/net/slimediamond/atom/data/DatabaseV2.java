package net.slimediamond.atom.data;

import com.google.inject.Inject;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.data.dao.DAO;
import net.slimediamond.atom.data.dao.DAOManager;
import net.slimediamond.atom.discord.entities.AtomGuild;
import net.slimediamond.atom.discord.entities.Guild;
import net.slimediamond.atom.reference.DBReference;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service(value = "database2", priority = 99999)
public class DatabaseV2 {
    @Inject
    private Logger logger;

    private Connection conn;

    private List<DAOManager<? extends DAO>> managers;

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

        this.managers = new ArrayList<>();
        managers.add(new DAOManager<AtomGuild>());
    }

    public Connection getConn() {
        return conn;
    }

    @SuppressWarnings("unchecked")
    public <T extends DAO> Optional<DAOManager<T>> getDAOManager(Class<T> type) {
        return (Optional<DAOManager<T>>) (Optional<?>)managers.stream()
                .filter(manager -> manager.getClass().isAssignableFrom(type))
                .findAny();
    }

    public Optional<? extends Guild> getGuild(long id) {
        return getDAOManager(AtomGuild.class).orElseThrow().getAllManaged()
                .stream().filter(g -> g.getDiscordId() == id).findAny();
    }

    public Optional<? extends Guild> getGuild(net.dv8tion.jda.api.entities.Guild jdaGuild) {
        return getGuild(jdaGuild.getIdLong());
    }
}
