package net.slimediamond.atom.data.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOManager<T extends DAO> {
    private final List<T> managed = new ArrayList<>();

    public Optional<T> findByPrimaryKey(int id) {
        return managed.stream().filter(dao -> dao.getPrimaryKey() == id).findAny();
    }

    public List<T> getAllManaged() {
        return managed;
    }
}
