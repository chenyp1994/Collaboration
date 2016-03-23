package com.chenyp.collaboration.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chenyp.collaboration.ui.fragment.selectPhoto.PhotoFragment;

import java.util.List;

/**
 * Created by change on 2015/11/3.
 */
public class BrowsePhotoAdapter extends FragmentPagerAdapter {

    private List<String> urls;

    public BrowsePhotoAdapter(FragmentManager fm, @NonNull List<String> urls) {
        super(fm);
        this.urls = urls;
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoFragment.newInstance(urls.get(position));
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public List<String> getUrls() {
        return urls;
    }

}
