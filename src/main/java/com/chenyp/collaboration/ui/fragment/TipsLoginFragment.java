package com.chenyp.collaboration.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.ui.activity.MainActivity;

import butterknife.Bind;

/**
 * Created by change on 2015/11/1.
 */
public class TipsLoginFragment extends BaseFragment {

    @Bind(R.id.id_btn_navigate_to_login)
    public Button btn;

    public final String TAG = getClass().getSimpleName();

    public static TipsLoginFragment newInstance() {
        Bundle bundle = new Bundle();
        TipsLoginFragment fragment = new TipsLoginFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tips_login;
    }

    @Override
    protected void initView() {
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getOnNavigationUIListener().navigateToLoginPage();
            }

        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }
}
