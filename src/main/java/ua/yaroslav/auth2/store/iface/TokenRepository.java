package ua.yaroslav.auth2.store.iface;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.yaroslav.auth2.entity.AccessToken;

@Repository
public interface TokenRepository extends CrudRepository<AccessToken, Long> {
}