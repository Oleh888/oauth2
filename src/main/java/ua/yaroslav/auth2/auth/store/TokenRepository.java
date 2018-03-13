package ua.yaroslav.auth2.auth.store;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.yaroslav.auth2.auth.entity.AccessToken;

@Repository
public interface TokenRepository extends CrudRepository<AccessToken, Long> {
}