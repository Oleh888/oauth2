package ua.yaroslav.auth2.store.iface;

import ua.yaroslav.auth2.entity.AccessToken;

import java.util.List;

public interface TokenStore {
    void saveToken(AccessToken accessToken);
    List<AccessToken> getTokens();
}