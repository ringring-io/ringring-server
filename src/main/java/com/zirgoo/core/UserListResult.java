package com.zirgoo.core;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by kosztope on 11/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserListResult {
    private UserList result;
    private Status status;

    public UserListResult() {
    }

    public UserListResult(UserList result, Status status) {
        this.result = result;
        this.status = status;
    }

    public UserList getResult() {
        return this.result;
    }
    public void setResult(UserList result) {
        this.result = result;
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

        String resultString = null;
        for (User user : getResult().getUserList()) {
            if (resultString == null) {
                resultString = user.toString();
            }
            else {
                resultString = resultString + user.toString();
            }
        }

        return statusString + '\n'
                + "Result {" + '\n'
                + resultString + '\n'
                + "}";
    }

}
