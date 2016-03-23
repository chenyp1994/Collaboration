package com.chenyp.collaboration.model.json;

import com.chenyp.collaboration.model.TreeRecord;

import java.util.List;

/**
 * Created by change on 2015/10/28.
 */
public class TreeRecordJsonData {

    private boolean success;

    private String message;

    private List<TreeRecord> children;

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

    public List<TreeRecord> getChildren() {
        return children;
    }

    public void setChildren(List<TreeRecord> children) {
        this.children = children;
    }

}

