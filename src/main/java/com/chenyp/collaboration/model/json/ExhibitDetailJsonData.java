package com.chenyp.collaboration.model.json;

import com.chenyp.collaboration.model.Detail;

import java.util.List;

/**
 * Created by change on 2015/11/2.
 */
public class ExhibitDetailJsonData {

    private boolean success;

    private String message;

    private Long exhibit;

    private String title;

    private String publisher;

    private Long pdate;

    private String summary;

    private Long count;

    private String authorize;

    private String photo;

    private List<Detail> details;

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

    public Long getExhibit() {
        return exhibit;
    }

    public void setExhibit(Long exhibit) {
        this.exhibit = exhibit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Long getPdate() {
        return pdate;
    }

    public void setPdate(Long pdate) {
        this.pdate = pdate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getAuthorize() {
        return authorize;
    }

    public void setAuthorize(String authorize) {
        this.authorize = authorize;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
