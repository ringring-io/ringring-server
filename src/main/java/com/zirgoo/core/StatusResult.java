package com.zirgoo.core;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by peterkosztolanyi on 26/02/2014.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class StatusResult {
    private Status status;

    public StatusResult() {
    }

    public StatusResult(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public boolean isSuccess() {
        return Status.OKAY.equals(this.getStatus());
    }

    @Override
    public String toString() {
        return  "Status {" + '\n'
                + " status=" + this.status + '\n'
                + ",isSuccess=" + this.isSuccess() + '\n'
                + "}";
    }
}
