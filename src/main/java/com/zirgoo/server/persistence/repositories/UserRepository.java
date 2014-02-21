package com.zirgoo.server.persistence.repositories;

import com.zirgoo.core.User;
import com.google.inject.ImplementedBy;

import java.util.List;

/**
 * Created by kosztope on 23/01/14.
 */
public interface UserRepository {

    void update(User user);
    void dropUsers();
    public String getActivationCode(String email) throws Exception;

    public User getUser(String email, boolean onlyActivatedUsers) throws Exception; // WS
    public List<User> getUsers(List<String> emailList, boolean onlyActivatedUsers) throws Exception; //WS
    public User register(String email) throws Exception; // WS
    public void activate(User user) throws Exception; // WS
    public void renewActivationCode(String email) throws Exception; // WS
}