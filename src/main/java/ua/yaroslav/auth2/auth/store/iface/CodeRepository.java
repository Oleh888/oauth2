package ua.yaroslav.auth2.auth.store.iface;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.yaroslav.auth2.auth.entity.AuthCode;

@Repository
public interface CodeRepository extends CrudRepository<AuthCode, Long> {
}