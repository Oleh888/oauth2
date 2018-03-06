package ua.yaroslav.auth2.store.iface;

import ua.yaroslav.auth2.entity.AuthCode;

import java.util.List;

public interface CodeStore {
    void saveCode(AuthCode code);
    List<AuthCode> getCodes();
}