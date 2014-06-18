package io.ringring.server.persistence.repositories;

import io.ringring.core.User;

import java.util.List;

/**
 * Created by kosztope on 23/01/14.
 */
public interface UserRepository {

    void update(User user);
    void dropUsers();
    void dropInvites();
    public String getActivationCode(String email) throws Exception;

    public User getUser(String email, boolean onlyActivatedUsers) throws Exception; // WS
    public List<User> getUsers(List<String> emailList, boolean onlyActivatedUsers) throws Exception; //WS
    public User register(String email) throws Exception; // WS
    public void activate(User user) throws Exception; // WS
    public void renewActivationCode(String email) throws Exception; // WS
    public void invite(String fromEmail, String toEmail) throws Exception; // WS
}