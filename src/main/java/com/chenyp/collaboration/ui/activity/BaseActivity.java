package com.chenyp.collaboration.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.util.ActivityCollector;


/**
 * Created by change on 2015/10/14.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public final static String URL = "http://127.0.0.1:8080/Collaboration/Broker";

    /**
     * 保存浏览器的sessionID
     */
    public static String sessionID = "";

    /**
     * 获取容器的ID
     *
     * @return 容器的ID
     */
    protected abstract int getContainerResId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    protected void addFragmentAndAddBackStack(BaseFragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        /*ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out,
                android.R.anim.slide_out_right, android.R.anim.fade_in);*/
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.add(getContainerResId(), fragment, fragment.getTransactionTag());
        ft.addToBackStack(fragment.getTransactionTag());
        ft.commitAllowingStateLoss();
    }

    protected void replaceFragment(BaseFragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        /*ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out,
                android.R.anim.slide_out_right, android.R.anim.fade_in);*/
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.replace(getContainerResId(), fragment, fragment.getTransactionTag());
        ft.commitAllowingStateLoss();
    }

    protected void replaceFragmentAndAddBackStack(BaseFragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        /*ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out,
                android.R.anim.slide_out_right, android.R.anim.fade_in);*/
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.replace(getContainerResId(), fragment, fragment.getTransactionTag());
        ft.addToBackStack(fragment.getTransactionTag());
        ft.commitAllowingStateLoss();
    }

    protected boolean isPopBackStack() {
        boolean isBackSuccess = false;
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            isBackSuccess = true;
        }
        return isBackSuccess;
    }

    protected void popBackStack() {
        getSupportFragmentManager().popBackStack();
    }

    protected void popBackStack(String tag) {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
