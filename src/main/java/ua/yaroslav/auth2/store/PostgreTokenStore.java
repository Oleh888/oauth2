package ua.yaroslav.auth2.store;

import org.springframework.stereotype.Service;
import ua.yaroslav.auth2.entity.AccessToken;
import ua.yaroslav.auth2.store.iface.TokenRepository;
import ua.yaroslav.auth2.store.iface.TokenStore;

import java.util.List;

@Service
public class PostgreTokenStore implements TokenStore {
    private final TokenRepository repository;

    public PostgreTokenStore(TokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveToken(AccessToken accessToken) {
        repository.save(accessToken);
    }

    @Override
    public List<AccessToken> getTokens() {
        return (List<AccessToken>) repository.findAll();
    }
}