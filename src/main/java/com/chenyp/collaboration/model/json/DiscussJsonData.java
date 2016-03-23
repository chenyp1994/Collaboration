package com.chenyp.collaboration.model.json;

import com.chenyp.collaboration.model.Discuss;

/**
 * Created by change on 2015/11/29.
 */
public class DiscussJsonData {

    private boolean success;

    private String message;

    private Discuss discuss;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Discuss getDiscuss() {
        return discuss;
    }

    public void setDiscuss(Discuss discuss) {
        this.discuss = discuss;
    }
}
