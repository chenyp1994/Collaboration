package com.chenyp.collaboration.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by change on 2015/10/19.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private String[] tabs;
    private Context context;

    public TabPagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] tabs, Context context) {
        super(fm);
        this.fragments = fragments;
        this.tabs = tabs;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    /*@Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }*/

    public List<Fragment> getList() {
        return fragments;
    }

    public void setList(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public String[] getTabs() {
        return tabs;
    }

    public void setTabs(String[] tabs) {
        this.tabs = tabs;
    }
}
