package com.chenyp.collaboration.model.json;

import com.chenyp.collaboration.model.DocInfo;

import java.util.List;

/**
 * Created by change on 2015/11/27.
 */
public class DiscussFileJsonData {

    private boolean success;

    private String message;

    private List<DocInfo> docList;

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

    public List<DocInfo> getDocList() {
        return docList;
    }

    public void setDocList(List<DocInfo> docList) {
        this.docList = docList;
    }
}
