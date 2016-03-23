package com.chenyp.collaboration.model;

/**
 * Created by change on 2015/11/29.
 */
public class Discuss {

    private Long id;

    private String title;

    private String summary;

    private Long pdate;

    private Long count;

    private String authorize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAuthorize() {
        return authorize;
    }

    public void setAuthorize(String authorize) {
        this.authorize = authorize;
    }
}
