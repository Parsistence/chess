package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void insertUser(UserData userData) throws DataAccessException {
        String username = userData.username();
        var result = users.put(username, userData);
        
        if (result != null) {
            throw new DataAccessException(
                    "Tried to register a new user with username " + username + ", but there is already a user registered with that name"
            );
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData userData = users.get(username);

        if (userData == null) {
            throw new DataAccessException(
                    "Tried to get an existing user with username " + username + ", but there is no user registered with that name"
            );
        }

        return userData;
    }
}
