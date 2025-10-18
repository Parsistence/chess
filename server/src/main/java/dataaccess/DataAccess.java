package dataaccess;

import datamodel.UserData;

public interface DataAccess {
    void saveUser(UserData userData);

    void getUser(String username);
}
