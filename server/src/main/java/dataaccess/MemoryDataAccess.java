package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        users.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }
}
