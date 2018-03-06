package ua.yaroslav.auth2.store.iface;

import ua.yaroslav.auth2.entity.Client;

import java.util.List;

public interface ClientStore {
    void saveClient(Client client);
    boolean checkClient(String clientID);
    boolean checkClient(String clientID, String clientSecret);
    List<Client> getClients();
}