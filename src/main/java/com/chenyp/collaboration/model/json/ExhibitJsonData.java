package com.chenyp.collaboration.model.json;

import com.chenyp.collaboration.model.Exhibit;

/**
 * Created by change on 2015/11/24.
 */
public class ExhibitJsonData {

    private boolean success;

    private String message;

    private Exhibit exhibit;

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

    public Exhibit getExhibit() {
        return exhibit;
    }

    public void setExhibit(Exhibit exhibit) {
        this.exhibit = exhibit;
    }

}
