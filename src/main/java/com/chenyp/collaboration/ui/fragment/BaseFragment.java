package com.chenyp.collaboration.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.chenyp.collaboration.listener.OnNavigationUIListener;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

/**
 * Created by change on 2015/10/14.
 */
public abstract class BaseFragment extends Fragment /*implements View.OnTouchListener*/ {

    public final static String URL_TYPE = "type";

    public final static String TRANSACTION_TAG_TYPE = "transaction_tag_type";

    public final static String JSON_DATA = "json";

    protected OnNavigationUIListener onNavigationUIListener;

    private View layoutView;

    protected abstract int getLayoutResId();

    protected abstract void initView();

    protected abstract void initData();

    public abstract String getTransactionTag();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity activity = ((MainActivity) context);
        if (activity != null) {
            onNavigationUIListener = activity;
        }
    }

    protected OnNavigationUIListener getOnNavigationUIListener() {
        return onNavigationUIListener;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*view.setOnTouchListener(this);*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(getLayoutResId(), container, false);
        ButterKnife.bind(this, layoutView);
        initView();
        return layoutView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public View getLayoutView() {
        return layoutView;
    }

    /*@Override
    public boolean onTouch(View v, MotionEvent event) {
        //避免多个Fragment重叠，点击事件触发
        return true;
    }*/

    protected void addChildFragmentAndAddBackStack(int containerId, BaseFragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.add(containerId, fragment, fragment.getTransactionTag());
        ft.commitAllowingStateLoss();
    }

    protected void replaceChildFragment(int containerId, BaseFragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.replace(containerId, fragment, fragment.getTransactionTag());
        ft.commitAllowingStateLoss();
    }

    protected void replaceChildFragmentAndAddBackStack(int containerId, BaseFragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.replace(containerId, fragment, fragment.getTransactionTag());
        ft.addToBackStack(fragment.getTransactionTag());
        ft.commitAllowingStateLoss();
    }

    protected boolean isChildPopBackStack() {
        boolean isBackSuccess = false;
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            getChildFragmentManager().popBackStack();
            isBackSuccess = true;
        }
        return isBackSuccess;
    }

    protected void childPopBackStack() {
        getChildFragmentManager().popBackStack();
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}


