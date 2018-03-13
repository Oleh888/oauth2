package ua.yaroslav.auth2.auth.store.iface;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.yaroslav.auth2.auth.entity.Client;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
    boolean existsByName(String name);
    Client findByName(String clientID);
}