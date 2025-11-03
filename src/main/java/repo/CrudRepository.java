package repo;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    T save(T t);                     // create
    T update(T t);                   // update
    void deleteById(ID id);
    Optional<T> findById(ID id);
    List<T> findAll();
}

