package com.chenyp.collaboration.model;


/**
 * Created by change on 2015/10/18.
 */
public class Photo extends BaseModel{

    /**
     * 图片id
     */
    private int id;

    /**
     * 图片地址
     */
    private String path;

    public Photo(int id, String path) {
        this.id = id;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;

        Photo photo = (Photo) o;

        return id == photo.id;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
