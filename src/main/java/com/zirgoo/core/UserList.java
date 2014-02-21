package com.zirgoo.core;

import java.util.List;

/**
 * Created by kosztope on 10/02/14.
 */
public class UserList {
    private List<User> userList;

    public UserList() {
    }

    public UserList(List<User> userList) {
        this.userList = userList;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
