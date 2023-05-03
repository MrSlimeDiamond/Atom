package net.zenoc.atom.services;

public interface Service {
    /**
     * Start the service
     */
    void startService() throws Exception;
    default void reloadService() throws Exception {}
    default void shutdownService() throws Exception {}
}
