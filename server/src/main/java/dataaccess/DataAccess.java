package dataaccess;

import datamodel.User;

public interface DataAccess {
    public void saveUser(User user);

    public void getUser(String username);
}
