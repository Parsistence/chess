package dataaccess;

import model.UserData;

public interface DataAccess {
    void createUser(UserData userData);

    UserData getUser(String username);
}
