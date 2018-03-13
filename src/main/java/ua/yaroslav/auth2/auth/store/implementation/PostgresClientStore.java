package ua.yaroslav.auth2.auth.store.implementation;

import org.springframework.stereotype.Service;
import ua.yaroslav.auth2.auth.entity.Client;
import ua.yaroslav.auth2.auth.store.ClientRepository;
import ua.yaroslav.auth2.auth.store.ClientStore;

import java.util.List;

@Service
public class PostgresClientStore implements ClientStore {
    private final ClientRepository repository;


    public PostgresClientStore(ClientRepository repository) {
        this.repository = repository;
    }


    @Override
    public void saveClient(Client client) {
        this.repository.save(client);
    }

    @Override
    public Client findByClientID(String cid) {
        return this.repository.findByName(cid);
    }

    @Override
    public boolean checkClient(String clientID) {
        return this.repository.existsByName(clientID);
    }

    @Override
    public boolean checkClient(String clientID, String clientSecret) {
        Client client = repository.findByName(clientID);
        return client.getSecret().equals(clientSecret);
    }

    @Override
    public List<Client> getClients() {
        return (List<Client>) this.repository.findAll();
    }
}