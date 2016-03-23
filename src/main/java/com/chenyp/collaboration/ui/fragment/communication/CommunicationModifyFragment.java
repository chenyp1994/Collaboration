package com.chenyp.collaboration.ui.fragment.communication;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.model.Discuss;
import com.chenyp.collaboration.model.TreeRecord;
import com.chenyp.collaboration.model.json.DiscussJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.util.AuthorizeWatcher;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ValidateUtil;

import butterknife.Bind;

/**
 * Created by change on 2015/11/4.
 */
public class CommunicationModifyFragment extends BaseFragment {

    @Bind(R.id.id_toolbar)
    public Toolbar tb;
    @Bind(R.id.id_et_communication_title)
    public EditText title;
    @Bind(R.id.id_et_communication_description)
    public EditText summary;
    @Bind(R.id.id_et_authorize)
    public EditText authorize;
    @Bind(R.id.id_rg_authorize)
    public RadioGroup rg;
    @Bind(R.id.id_rb_all_user)
    public RadioButton allUser;
    @Bind(R.id.id_rb_specific_user)
    public RadioButton specificUser;

    private CommunicationModifyHandler handler = new CommunicationModifyHandler(this);

    public final String TAG = getClass().getSimpleName();

    private String discuss;

    public static CommunicationModifyFragment newInstance(Bundle bundle) {
        CommunicationModifyFragment fragment = new CommunicationModifyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_communication_edit;
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
        tb.setTitle("更新项目开发沟通");

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.id_rb_all_user:
                        authorize.setVisibility(View.GONE);
                        break;
                    case R.id.id_rb_specific_user:
                        authorize.setVisibility(View.VISIBLE);
                        break;
                }
            }

        });

        authorize.addTextChangedListener(new AuthorizeWatcher(authorize));
    }

    @Override
    protected void initData() {
        getDiscussData();

    }

    private void getDiscussData() {
        TreeRecord record = GsonUtil.fromJson(getArguments().getString(JSON_DATA), TreeRecord.class);
        discuss = String.valueOf(record.getId());
        HttpPostUtil postUtil = new HttpPostUtil();
        postUtil.addTextParameter("type", "15");
        postUtil.addTextParameter("discuss", discuss);
        postUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {

            @Override
            public void onSuccess(byte[] outputStream) {
                String json = new String(outputStream);
                Log.i(TAG, json);
                parseJsonData(json);
            }

            @Override
            public void onError(String error) {

            }

        });
    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    private void parseJsonData(String json) {
        DiscussJsonData jsonData = GsonUtil.fromJson(json, DiscussJsonData.class);
        if (jsonData.isSuccess()) {
            updateCommunicationUI(jsonData.getDiscuss());
        }
    }

    private void updateCommunicationUI(final Discuss discuss) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                title.setText(discuss.getTitle());
                if (discuss.getAuthorize().equals("all")) {
                    allUser.setChecked(true);
                    specificUser.setChecked(false);
                } else {
                    allUser.setChecked(true);
                    specificUser.setChecked(false);
                    authorize.setText(discuss.getAuthorize());
                }
                summary.setText(discuss.getSummary());
            }
        });
    }

    private void submitCommunication(String titleText, String summaryText) {
        String type = "16";
        String authorizeText = authorize.getVisibility() == View.GONE ? "all" : authorize.getText().toString();

        HttpPostUtil postUtil = new HttpPostUtil();
        postUtil.addTextParameter("type", type);
        postUtil.addTextParameter("discuss", discuss);
        postUtil.addTextParameter("title", titleText);
        postUtil.addTextParameter("summary", summaryText);
        postUtil.addTextParameter("authorize", authorizeText);
        postUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {

            @Override
            public void onSuccess(byte[] outputStream) {
                String json = new String(outputStream);
                Log.i(TAG, json);
                getOnNavigationUIListener().goBackFromCurrentPage();
            }

            @Override
            public void onError(String error) {

            }

        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_submit, menu);
        //MenuItem submitItem = menu.findItem(R.id.action_submit);
        //submitItem.setEnabled(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_submit) {
            if (validate()) {
                String summaryText = summary.getText().toString();
                String titleText = title.getText().toString();
                submitCommunication(titleText, summaryText);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validate() {
        String summaryText = summary.getText().toString();
        String titleText = title.getText().toString();
        if (!ValidateUtil.isValid(titleText)) {
            Toast.makeText(getContext(), "沟通名称不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!ValidateUtil.isValid(summaryText)) {
            Toast.makeText(getContext(), "沟通描述不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static class CommunicationModifyHandler extends Handler {

        private CommunicationModifyFragment communicationModifyFragment;

        public CommunicationModifyHandler(CommunicationModifyFragment communicationModifyFragment) {
            this.communicationModifyFragment = communicationModifyFragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

}
