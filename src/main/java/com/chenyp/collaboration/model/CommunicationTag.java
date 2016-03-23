package com.chenyp.collaboration.model;

/**
 * Created by change on 2015/11/16.
 * 包括Communication 和 CommunicationTopic, 辨别目录
 */
public class CommunicationTag extends BaseModel {

    public Long id;

    public String tag;

    public String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
