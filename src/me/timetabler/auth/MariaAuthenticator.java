package me.timetabler.auth;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.Map;

/**
 * {@inheritDoc}
 * This implantation authenticates with a MariaDB data source.
 */
public class MariaAuthenticator implements Authenticator {
    /**
     * The 'data_source' configuration map. It must contain at least the key/value combinations 'addr', 'port' and
     * 'database'.
     */
    private Map<String, String> config;

    /**
     * Initialises the authenticator. The given configuration map should be the 'data_source' configuration map,
     * containing the key/value combinations 'addr', 'port' and 'database'.
     * @param config The 'data_source' configuration map.
     */
    public MariaAuthenticator(Map<String, String> config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean authenticate(String username, char[] password) {
        MariaDbDataSource source = null;
        try {
            source = new MariaDbDataSource(config.get("addr"), Integer.parseInt(config.get("port")), config.get("database"));
            source.setUser(username);
            source.setPassword(String.valueOf(password));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            source.getConnection();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
