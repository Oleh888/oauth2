package ua.yaroslav.auth2.store;

import org.springframework.stereotype.Service;
import ua.yaroslav.auth2.entity.Client;
import ua.yaroslav.auth2.store.iface.ClientRepository;
import ua.yaroslav.auth2.store.iface.ClientStore;

import java.util.List;

@Service
public class PostgreClientStore implements ClientStore {
    private final ClientRepository repository;

    public PostgreClientStore(ClientRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveClient(Client client) {
        this.repository.save(client);
    }

    @Override
    public boolean checkClient(String clientID) {
        return this.repository.existsByName(clientID);
    }

    @Override
    public boolean checkClient(String clientID, String clientSecret) {
        return this.repository.existsByName(clientID) && this.repository.existsBySecret(clientSecret);
    }

    @Override
    public List<Client> getClients() {
        return (List<Client>) this.repository.findAll();
    }
}