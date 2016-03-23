package com.chenyp.collaboration.ui.fragment.tab;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.TabPagerAdapter;
import com.chenyp.collaboration.model.json.UserJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.ui.fragment.TipsLoginFragment;
import com.chenyp.collaboration.ui.view.HackyViewPager;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ValidateUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/10/23.
 */
public class MyPostProjectTabFragment extends BaseFragment {

    public final String TAG = getClass().getSimpleName();

    @Bind(R.id.id_fragment_tabLy)
    public TabLayout tbl;
    @Bind(R.id.id_fragment_vp)
    public HackyViewPager vp;
    /*@Bind(R.id.id_fab)
    public FloatingActionButton addActionButton;*/

    /**
     * tab的名字
     */
    public String[] tabs;

    /**
     * tab的标题头
     */
    public String[] tabTitles;

    private TabPagerAdapter tabPagerAdapter;

    private List<Fragment> fragments;

    private AchievementTabFragment myAchievementFragment;

    private CommunicationTabFragment myCommunicationFragment;

    private MyPostProjectTabFragmentHandler handler = new MyPostProjectTabFragmentHandler(this);
    private ActionBar actionBar;

    public static MyPostProjectTabFragment newInstance(Bundle bundle) {
        MyPostProjectTabFragment fragment = new MyPostProjectTabFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_my;
    }

    @Override
    protected void initView() {
        setTabMyFragments();
        tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager(), fragments, tabs, getContext());
        vp.setAdapter(tabPagerAdapter);
        vp.setOffscreenPageLimit(tabs.length);
        vp.setCanScroll(false);
        /*vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int lastPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setTitle(tabTitles[position]);
                if (lastPosition == position) {
                    fragments.get(position).setMenuVisibility(true);
                } else {
                    fragments.get(position).setMenuVisibility(true);
                    fragments.get(lastPosition).setMenuVisibility(false);
                }
                Log.i(TAG, "lastPosition : " + lastPosition + "\nposition : " + position);
                lastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });*/

        tbl.setupWithViewPager(vp);
        tbl.setTabsFromPagerAdapter(tabPagerAdapter);

        tbl.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
                //fragments.get(vp.getCurrentItem()).setMenuVisibility(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //fragments.get(vp.getCurrentItem()).setMenuVisibility(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
    }

    @Override
    protected void initData() {
        HttpPostUtil postUtil = new HttpPostUtil();
        postUtil.addTextParameter("type", "5");
        postUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {
            @Override
            public void onSuccess(byte[] outputStream) {
                String json = new String(outputStream);
                Log.i(TAG, json);
                UserJsonData jsonData = GsonUtil.fromJson(json, UserJsonData.class);
                Log.i(TAG, jsonData.toString());
                if (jsonData.isSuccess()) {
                    getChildFragmentManager().popBackStack();
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            replaceChildFragmentAndAddBackStack(R.id.id_fl_tab_my, TipsLoginFragment.newInstance());
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        replaceChildFragment(R.id.id_fl_tab_my, TipsLoginFragment.newInstance());
                    }
                });
            }
        });
    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    private void setTabMyFragments() {
        fragments = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putString(URL_TYPE, "24");
        bundle.putString(TRANSACTION_TAG_TYPE, tabs[0]);
        myAchievementFragment = AchievementTabFragment.newInstance(bundle);

        bundle = new Bundle();
        bundle.putString(URL_TYPE, "25");
        bundle.putString(TRANSACTION_TAG_TYPE, tabs[1]);
        myCommunicationFragment = CommunicationTabFragment.newInstance(bundle);

        myAchievementFragment.onAttach(getContext());
        myCommunicationFragment.onAttach(getContext());

        fragments.add(myAchievementFragment);
        fragments.add(myCommunicationFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Resources resources = context.getResources();
        tabs = resources.getStringArray(R.array.activity_main_my_project_tab_name);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (ValidateUtil.isValid(fragments)) {
            fragments.get(vp.getCurrentItem()).setMenuVisibility(menuVisible);
        }
    }

    public static class MyPostProjectTabFragmentHandler extends Handler {

        private MyPostProjectTabFragment fragment;

        public MyPostProjectTabFragmentHandler(MyPostProjectTabFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
