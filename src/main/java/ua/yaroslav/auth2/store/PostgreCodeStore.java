package ua.yaroslav.auth2.store;

import org.springframework.stereotype.Service;
import ua.yaroslav.auth2.entity.AuthCode;
import ua.yaroslav.auth2.store.iface.CodeRepository;
import ua.yaroslav.auth2.store.iface.CodeStore;

import java.util.List;

@Service
public class PostgreCodeStore implements CodeStore {
    private final CodeRepository repository;

    public PostgreCodeStore(CodeRepository repository) {
        this.repository = repository;
    }


    @Override
    public void saveCode(AuthCode code) {
        repository.save(code);
    }

    @Override
    public List<AuthCode> getCodes() {
        return (List<AuthCode>) repository.findAll();
    }
}