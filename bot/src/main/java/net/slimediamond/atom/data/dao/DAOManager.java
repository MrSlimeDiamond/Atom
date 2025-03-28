package net.slimediamond.atom.data.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOManager<T extends DAO> {
    private final Class<T> clazz;
    private final List<T> managed = new ArrayList<>();

    public DAOManager(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Optional<T> findByPrimaryKey(int id) {
        return managed.stream().filter(dao -> dao.getPrimaryKey() == id).findAny();
    }

    public List<T> getAllManaged() {
        return managed;
    }
}
