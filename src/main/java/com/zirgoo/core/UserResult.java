package com.zirgoo.core;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by kosztope on 11/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserResult {
    private User result;
    private Status status;

    public UserResult() {
    }

    public UserResult(User result, Status status) {
        this.result = result;
        this.status = status;
    }

    public User getResult() {
        return this.result;
    }

    public Status getStatus() {
        return this.status;
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
        if(getResult() != null) {
            resultString = getResult().toString();
        }

        return statusString + '\n'
                + "Result {" + '\n'
                + resultString + '\n'
                + "}";
    }
}
