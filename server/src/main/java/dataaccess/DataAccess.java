package dataaccess;

import model.UserData;

public interface DataAccess {
    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
}
