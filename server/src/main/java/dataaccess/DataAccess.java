package dataaccess;

import datamodel.UserData;

public interface DataAccess {
    void saveUser(UserData userData);

    UserData getUser(String username);
}
