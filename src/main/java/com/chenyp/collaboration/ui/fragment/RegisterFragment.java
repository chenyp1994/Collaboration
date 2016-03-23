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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chenyp.collaboration.R;
import com.chenyp.collaboration.model.json.UserJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.selectPhoto.SelectPhotoFragment;
import com.chenyp.collaboration.util.GlideCircleTransform;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ImageUtils;
import com.chenyp.collaboration.util.MD5Util;
import com.chenyp.collaboration.util.ValidateUtil;

import java.io.File;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/10/30.
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.id_toolbar)
    public Toolbar tb;
    @Bind(R.id.id_iv_photo)
    public ImageView photo;
    @Bind(R.id.id_et_name)
    public EditText name;
    @Bind(R.id.id_et_password)
    public EditText password;
    @Bind(R.id.id_et_confirmPassword)
    public EditText confirmPassword;
    @Bind(R.id.id_et_phone)
    public EditText phone;
    @Bind(R.id.id_et_email)
    public EditText email;
    @Bind(R.id.id_et_qq)
    public EditText qq;
    @Bind(R.id.id_et_weChat)
    public EditText weChat;
    @Bind(R.id.id_btn_register)
    public Button register;
    @Bind(R.id.id_btn_cancel)
    public Button cancel;

    private Context mContext;

    private String path = "";

    private RegisterHandler handler = new RegisterHandler(this);

    public final String TAG = getClass().getSimpleName();

    public static RegisterFragment newInstance() {
        Bundle bundle = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_register;
    }

    @Override
    protected void initView() {
        ((MainActivity) getActivity()).setSupportActionBar(tb);
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setShowHideAnimationEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(25);
            }
        }
        tb.setTitle("注册");

        photo.setOnClickListener(this);
        register.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (ValidateUtil.isValid(path)) {
            Glide.with(mContext)
                    .load(path)
                    .thumbnail(0.1f)
                    .centerCrop()
                    .transform(new GlideCircleTransform(mContext))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(photo);
        } else {
            Glide.with(mContext)
                    .load(android.R.drawable.ic_menu_gallery)
                    .transform(new GlideCircleTransform(mContext))
                    .thumbnail(0.1f)
                    .centerCrop()
                    .into(photo);
        }
    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_register, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_login:
                getOnNavigationUIListener().goBackFromCurrentPage();
                getOnNavigationUIListener().navigateToLoginPage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_btn_register:
                HttpPostUtil postUtil = new HttpPostUtil();
                postUtil.addTextParameter("type", "3");
                postUtil.addTextParameter("name", name.getText().toString());
                postUtil.addTextParameter("password", MD5Util.getMD5ofStr(password.getText().toString()));
                //postUtil.addTextParameter("password", password.getText().toString());
                postUtil.addTextParameter("confirmPassword", MD5Util.getMD5ofStr(confirmPassword.getText().toString()));
                postUtil.addTextParameter("mobile", phone.getText().toString());
                postUtil.addTextParameter("email", email.getText().toString());
                postUtil.addTextParameter("qq", qq.getText().toString());
                postUtil.addTextParameter("wechat", weChat.getText().toString());
                if (ValidateUtil.isValid(path)) {
                    postUtil.addFileParameter("photo", ImageUtils.compress(path));
                }
                postUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {
                    @Override
                    public void onSuccess(byte[] outputStream) {
                        String json = new String(outputStream);
                        final UserJsonData jsonData = GsonUtil.fromJson(json, UserJsonData.class);
                        if (jsonData.isSuccess()) {
                            getOnNavigationUIListener().goBackFromCurrentPage();
                            Snackbar.make(getLayoutView(), "这是你的用户id : " + String.valueOf(jsonData.getUser().getId()),
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    Snackbar.make(getLayoutView(), jsonData.getMessage(),
                                            Snackbar.LENGTH_LONG).show();
                                }

                            });
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
                break;
            case R.id.id_btn_cancel:
                getOnNavigationUIListener().goBackFromCurrentPage();
                break;
            case R.id.id_iv_photo:
                getOnNavigationUIListener().navigateToSelectPhotoPage(1, true, new SelectPhotoFragment.OnSelectPhotoFinishListener() {
                    @Override
                    public void onFinish(List<String> urls) {
                        if (ValidateUtil.isValid(urls)) {
                            path = urls.get(0);
                            Log.i(TAG, path);
                            //显示图片
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public static class RegisterHandler extends Handler {

        private RegisterFragment fragment;

        public RegisterHandler(RegisterFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
