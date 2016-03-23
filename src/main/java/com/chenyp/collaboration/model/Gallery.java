package com.chenyp.collaboration.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by change on 2015/10/18.
 */
public class Gallery extends BaseModel{

    private String id;
    private String folder;
    private String name;
    private long dateAdded;
    private List<Photo> photos = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gallery)) return false;

        Gallery gallery = (Gallery) o;

        if (!id.equals(gallery.id)) return false;
        return name.equals(gallery.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverPath() {
        return folder;
    }

    public void setCoverPath(String folder) {
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public List<String> getPhotoPaths() {
        List<String> paths = new ArrayList<>(photos.size());
        for (Photo photo : photos) {
            paths.add(photo.getPath());
        }
        return paths;
    }

    public void addPhoto(int id, String path) {
        photos.add(new Photo(id, path));
    }
}
