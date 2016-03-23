package com.chenyp.collaboration.task;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.model.Gallery;
import com.chenyp.collaboration.task.cursorLoader.PhotosLoader;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;

/**
 * Created by change on 2015/10/19.
 */
public class PhotosLoaderManager implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context context;
    private OnPhotosLoadFinished onPhotosLoadFinished;

    public final static int INDEX_ALL_PHOTOS = 0;

    public PhotosLoaderManager(Context context, OnPhotosLoadFinished onPhotosLoadFinished) {
        this.context = context;
        this.onPhotosLoadFinished = onPhotosLoadFinished;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new PhotosLoader(context);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) return;

        List<Gallery> galleries = new ArrayList<>();
        Gallery galleryAll = new Gallery();
        galleryAll.setName(context.getString(R.string.all_image));
        galleryAll.setId("All");

        if(data.moveToFirst()){
            do {
                int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
                String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
                String name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
                String path = data.getString(data.getColumnIndexOrThrow(DATA));

                Gallery gallery = new Gallery();
                gallery.setId(bucketId);
                gallery.setName(name);

                //判断是否存在gallery
                if (!galleries.contains(gallery)) {
                    gallery.setCoverPath(path);
                    gallery.addPhoto(imageId, path);
                    gallery.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
                    galleries.add(gallery);
                } else {
                    galleries.get(galleries.indexOf(gallery)).addPhoto(imageId, path);
                }

                galleryAll.addPhoto(imageId, path);

            }while (data.moveToNext());
        }

        if (galleryAll.getPhotoPaths().size() > 0) {
            galleryAll.setCoverPath(galleryAll.getPhotoPaths().get(0));
        }
        
        galleries.add(INDEX_ALL_PHOTOS, galleryAll);

        if (onPhotosLoadFinished != null) {
            onPhotosLoadFinished.onLoadFinished(galleries);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface OnPhotosLoadFinished {
        void onLoadFinished(List<Gallery> galleries);
    }
}
