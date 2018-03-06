package ua.yaroslav.auth2.store.iface;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.yaroslav.auth2.entity.Client;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
    boolean existsByName(String name);
    boolean existsBySecret(String secret);
}