package dataaccess;

import model.UserData;

public interface DataAccess {
    void insertUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
}
