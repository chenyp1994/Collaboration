package com.chenyp.collaboration.model;

/**
 * Created by change on 2015/11/27.
 */
public class DocInfo extends BaseModel {

    private Long id;
    private String photo;
    private String publisher;
    private String title;
    private String summary;
    private Long pdate;
    private Long count;
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getPdate() {
        return pdate;
    }

    public void setPdate(Long pdate) {
        this.pdate = pdate;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
