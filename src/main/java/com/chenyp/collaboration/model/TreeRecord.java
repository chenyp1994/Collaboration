package com.chenyp.collaboration.model;

import java.util.List;

/**
 * Created by change on 2015/10/5.
 */
public class TreeRecord extends BaseModel {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String text;

    /**
     * 发布日期
     */
    private String publisher;

    /**
     * 发布Long格式的时间
     */
    private Long pdate;

    /**
     * 项目简介
     */
    private String summary;

    /**
     * 头像的url
     */
    private String photo;

    /**
     * 浏览次数
     */
    private Long count;

    /**
     * 详细信息列
     */
    private List<Detail> details;

    private String catalog;

    private Boolean leaf;

    private Boolean expanded;

    private List<TreeRecord> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public List<TreeRecord> getChildren() {
        return children;
    }

    public void setChildren(List<TreeRecord> children) {
        this.children = children;
    }
}
