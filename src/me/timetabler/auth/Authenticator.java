package me.timetabler.auth;

import java.util.Map;

/**
 * A way for the system to authenticate a username and password combination.
 */
public interface Authenticator {
    /**
     * Authenticates the username and password combination given. Returning true if the combination was valid, and false
     * otherwise.
     * @param username The username to authenticate.
     * @param password The password to authenticate.
     * @return Returns true if the username and password combination is valid.
     */
    boolean authenticate(String username, char[] password);

    /**
     * Gets the correct implementation of Authenticator, if it exists. If it does not exist, this will return null. The
     * given map must contain the entry 'type', which must represent an implementation, or this will return null.
     * @param config The configuration map for 'data_source'.
     * @return Returns the implmentation of Authenticator corrisponding to the entry 'type' in the map, or null if it
     * does not exist.
     */
    static Authenticator getAuthenticator(Map<String, String> config) {
        String type = config.get("type");
        switch (type) {
            case "MARIADB": return new MariaAuthenticator(config);
            default: return null;
        }
    }
}
