package com.chenyp.collaboration.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.model.User;
import com.chenyp.collaboration.model.json.UserJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.MD5Util;

import butterknife.Bind;

/**
 * Created by change on 2015/10/30.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.id_et_account)
    public EditText account;
    @Bind(R.id.id_et_password)
    public EditText password;
    @Bind(R.id.id_btn_login)
    public Button login;
    @Bind(R.id.id_btn_cancel)
    public Button cancel;
    @Bind(R.id.id_toolbar)
    public Toolbar tb;

    private LoginFragmentHandler handler = new LoginFragmentHandler(this);

    public final String TAG = getClass().getSimpleName();

    public static LoginFragment newInstance() {
        Bundle bundle = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void initView() {
        ((MainActivity) getActivity()).setSupportActionBar(tb);
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setShowHideAnimationEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(25);
            }
        }
        tb.setTitle("用户登陆");

        login.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_btn_login:
                HttpPostUtil httpPostUtil = new HttpPostUtil();
                httpPostUtil.addTextParameter("type", "4");
                httpPostUtil.addTextParameter("id", account.getText().toString());
                //MD5加密
                httpPostUtil.addTextParameter("password", MD5Util.getMD5ofStr(password.getText().toString()));
                //httpPostUtil.addTextParameter("password", password.getText().toString());
                httpPostUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {

                    @Override
                    public void onSuccess(byte[] outputStream) {
                        String json = new String(outputStream);
                        Log.i(TAG, json);
                        final UserJsonData jsonData = GsonUtil.fromJson(json, UserJsonData.class);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(getLayoutView(), jsonData.getMessage(),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        });
                        if (jsonData.isSuccess()) {
                            User user = jsonData.getUser();
                            System.out.println(user.getPhoto());
                            getOnNavigationUIListener().goBackFromCurrentPage();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        //Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }

                });
                break;
            case R.id.id_btn_cancel:

                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_login, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_register:
                getOnNavigationUIListener().goBackFromCurrentPage();
                getOnNavigationUIListener().navigateToRegisterPage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class LoginFragmentHandler extends Handler {

        private LoginFragment fragment;

        public LoginFragmentHandler(LoginFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

}
