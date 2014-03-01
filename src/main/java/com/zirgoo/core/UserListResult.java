package com.zirgoo.core;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by kosztope on 11/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserListResult {
    private UserList userList;
    private Status status;

    public UserListResult() {
    }

    public UserListResult(UserList userList, Status status) {
        this.userList = userList;
        this.status = status;
    }

    public UserList getUserList() {
        return this.userList;
    }
    public void setUserList(UserList userList) {
        this.userList = userList;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return Status.OKAY.equals(this.getStatus());
    }

    @Override
    public String toString() {
        String statusString =  "Status {" + '\n'
                + " status=" + this.status + '\n'
                + ",isSuccess=" + this.isSuccess() + '\n'
                + "}";

        String userListString = null;
        for (User user : getUserList().getUserList()) {
            if (userListString == null) {
                userListString = user.toString();
            }
            else {
                userListString = userListString + user.toString();
            }
        }

        return statusString + '\n'
                + "Result {" + '\n'
                + userListString + '\n'
                + "}";
    }

}
