package sarisaristore;

import java.util.List;

/** INTERFACE: the four database operations every table class must offer. */
public interface Crud<T> {
    List<T> findAll();
    boolean add(T item);
    boolean update(T item);
    boolean delete(int id);
}
