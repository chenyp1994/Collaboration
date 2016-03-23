package com.chenyp.collaboration.ui.fragment.communication.topic;


import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.model.TreeRecord;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ValidateUtil;

import butterknife.Bind;

/**
 * Created by change on 2015/10/23.
 */
public class CommunicationTopicAddFragment extends BaseFragment /*implements View.OnClickListener*/ {

    @Bind(R.id.id_toolbar)
    public Toolbar tb;
    @Bind(R.id.id_et_communication_topic_parent_title)
    public EditText parentTitle;
    @Bind(R.id.id_ll_topic_parent_title)
    public LinearLayout titleLayout;
    @Bind(R.id.id_et_communication_topic_child_title)
    public EditText title;
    @Bind(R.id.id_et_communication_topic_child_summary)
    public EditText summary;
    /*@Bind(R.id.id_btn_submit)
    public Button submit;
    @Bind(R.id.id_btn_cancel)
    public Button cancel;*/

    public final String TAG = getClass().getSimpleName();

    protected static String discuss;

    public static void setDiscuss(String discuss) {
        CommunicationTopicAddFragment.discuss = discuss;
    }

    private String topicParentId;

    public static CommunicationTopicAddFragment newInstance(String parentJson) {
        Bundle bundle = new Bundle();
        CommunicationTopicAddFragment fragment = new CommunicationTopicAddFragment();
        bundle.putString(JSON_DATA, parentJson);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_communication_topic_edit;
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
        String json = getArguments().getString(JSON_DATA);
        if (ValidateUtil.isValid(json)) {
            TreeRecord record = GsonUtil.fromJson(json, TreeRecord.class);
            topicParentId = String.valueOf(record.getId());
            parentTitle.setText(record.getText());
            titleLayout.setVisibility(View.VISIBLE);
        } else {
            titleLayout.setVisibility(View.GONE);
        }
        tb.setTitle("新建主题");

        /*submit.setOnClickListener(this);
        cancel.setOnClickListener(this);*/
    }

    @Override
    protected void initData() {

    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    /*@Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_btn_submit:
                submitCommunicationTopic();
                break;
            case R.id.id_btn_cancel:
                getOnNavigationUIListener().goBackFromCurrentPage();
                break;
        }
    }*/

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
            /*if (validate()) {
                String summaryText = summary.getText().toString();
                String titleText = title.getText().toString();
                submitCommunication(summaryText, titleText);
            }*/
            submitCommunicationTopic();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitCommunicationTopic() {
        String type = "11";
        String summaryText = summary.getText().toString();
        String titleText = title.getText().toString();
        if (!ValidateUtil.isValid(titleText)) {
            Toast.makeText(getContext(), "主题名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ValidateUtil.isValid(summaryText)) {
            Toast.makeText(getContext(), "主题简介不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        HttpPostUtil postUtil = new HttpPostUtil();
        postUtil.addTextParameter("type", type);
        postUtil.addTextParameter("discuss", discuss);
        if (ValidateUtil.isValid(topicParentId)) {
            postUtil.addTextParameter("topic", topicParentId);
        }
        postUtil.addTextParameter("title", titleText);
        postUtil.addTextParameter("summary", summaryText);
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
}
