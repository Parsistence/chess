package dataaccess;

import datamodel.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void saveUser(UserData userData) {
        users.put(userData.username(), userData);
    }

    @Override
    public void getUser(String username) {
        users.get(username);
    }
}
