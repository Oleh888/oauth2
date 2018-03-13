package ua.yaroslav.auth2.auth.store;

import ua.yaroslav.auth2.auth.entity.Client;

import java.util.List;

public interface ClientStore {
    void saveClient(Client client);
    Client findByClientID(String cid);
    boolean checkClient(String cid);
    boolean checkClient(String cid, String clientSecret);
    List<Client> getClients();
}