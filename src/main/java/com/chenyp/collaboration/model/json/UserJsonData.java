package com.chenyp.collaboration.model.json;

import com.chenyp.collaboration.model.User;

/**
 * Created by change on 2015/11/2.
 */
public class UserJsonData {

    private boolean success;

    private String message;

    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
