package com.chenyp.collaboration.ui.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.TabPagerAdapter;
import com.chenyp.collaboration.model.json.UserJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.tab.AchievementTabFragment;
import com.chenyp.collaboration.ui.fragment.tab.CommunicationTabFragment;
import com.chenyp.collaboration.ui.fragment.tab.MyPostProjectTabFragment;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ValidateUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/10/23.
 */
public class MainFragment extends BaseFragment {

    @Bind(R.id.id_toolbar)
    public Toolbar tb;
    @Bind(R.id.id_fragment_tabLy)
    public TabLayout tbl;
    @Bind(R.id.id_fragment_vp)
    public ViewPager vp;
    /*@Bind(R.id.id_nav_view)
    public NavigationView navigationView;*/
    @Bind(R.id.id_drawer_layout)
    public DrawerLayout mDrawerLayout;

    public final String TAG = getClass().getSimpleName();

    /**
     * tab的名字
     */
    public String[] tabs;
    /**
     * tab的标题头
     */
    public String[] tabTitles;

    private MenuItem toLogin, toLogout, toRegister;

    private MainHandler handler = new MainHandler(this);

    private TabPagerAdapter tabPagerAdapter;

    private List<Fragment> fragments;

    private AchievementTabFragment achievementFragment;

    private CommunicationTabFragment communicationFragment;

    private MyPostProjectTabFragment myPostProjectFragment;

    private ActionBar actionBar;

    public static MainFragment newInstance(Bundle bundle) {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView() {
        ((MainActivity) getActivity()).setSupportActionBar(tb);
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            //actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setShowHideAnimationEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(25);
            }
        }
        /*navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        mDrawerLayout.closeDrawers();
                        Log.i(TAG, "menuItem click");
                        menuItem.setChecked(true);
                        return true;
                    }
                });*/

        assert actionBar != null;
        actionBar.setTitle(tabTitles[vp.getCurrentItem()]);
    }

    @Override
    protected void initData() {
        setTabFragments();
        tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager(), fragments, tabs, getContext());
        vp.setAdapter(tabPagerAdapter);
        vp.setOffscreenPageLimit(3);
        tbl.setupWithViewPager(vp);
        tbl.setTabsFromPagerAdapter(tabPagerAdapter);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int lastPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        actionBar.setTitle(tabTitles[position]);
                        break;
                    case 1:
                        actionBar.setTitle(tabTitles[position]);
                        break;
                    case 2:
                        /*if (myPostProjectFragment.vp != null) {
                            String title = myPostProjectFragment.tabTitles[myPostProjectFragment.vp.getCurrentItem()];
                            actionBar.setTitle(title);
                        } else {*/
                        actionBar.setTitle(tabTitles[position]);
                        //}
                        break;
                }
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


        });
        tbl.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
                fragments.get(vp.getCurrentItem()).setMenuVisibility(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                fragments.get(vp.getCurrentItem()).setMenuVisibility(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
        tabPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    /**
     * 初始化Fragment列
     */
    private void setTabFragments() {
        fragments = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putString(URL_TYPE, "1");
        bundle.putString(TRANSACTION_TAG_TYPE, tabs[0]);
        achievementFragment = AchievementTabFragment.newInstance(bundle);

        bundle = new Bundle();
        bundle.putString(URL_TYPE, "23");
        bundle.putString(TRANSACTION_TAG_TYPE, tabs[1]);
        communicationFragment = CommunicationTabFragment.newInstance(bundle);

        myPostProjectFragment = MyPostProjectTabFragment.newInstance(new Bundle());

        achievementFragment.onAttach(getContext());
        communicationFragment.onAttach(getContext());
        myPostProjectFragment.onAttach(getContext());

        fragments.add(achievementFragment);
        fragments.add(communicationFragment);
        fragments.add(myPostProjectFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setMenuVisibility(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        toLogin = menu.findItem(R.id.action_login);
        toLogout = menu.findItem(R.id.action_logout);
        toRegister = menu.findItem(R.id.action_register);
        if (ValidateUtil.isValid(BaseActivity.sessionID)) {
            toLogin.setVisible(false);
            toRegister.setVisible(false);
            toLogout.setVisible(true);
        } else {
            toLogin.setVisible(true);
            toRegister.setVisible(true);
            toLogout.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_register:
                getOnNavigationUIListener().navigateToRegisterPage();
                return true;
            case R.id.action_login:
                getOnNavigationUIListener().navigateToLoginPage();
                return true;
            case R.id.action_logout:
                HttpPostUtil postUtil = new HttpPostUtil();
                postUtil.addTextParameter("type", "22");
                postUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {
                    @Override
                    public void onSuccess(byte[] outputStream) {
                        String json = new String(outputStream);
                        Log.i(TAG, json);
                        //UserJsonData jsonData = GsonUtil.fromJson(json, UserJsonData.class);
                        BaseActivity.sessionID = "";
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                initData();
                                toLogin.setVisible(true);
                                toRegister.setVisible(true);
                                toLogout.setVisible(false);
                            }
                        });

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Resources resources = context.getResources();
        tabs = resources.getStringArray(R.array.activity_main_tab_name);
        tabTitles = resources.getStringArray(R.array.activity_main_tab_title_name);
    }


    public static class MainHandler extends Handler {

        private MainFragment fragment;

        public MainHandler(MainFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
