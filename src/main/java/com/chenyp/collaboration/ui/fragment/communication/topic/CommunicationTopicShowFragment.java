package com.chenyp.collaboration.ui.fragment.communication.topic;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.CommunicationTopicAdapter;
import com.chenyp.collaboration.adapter.CommunicationTopicsTextAdapter;
import com.chenyp.collaboration.adapter.layoutManager.FullyLinearLayoutManager;
import com.chenyp.collaboration.model.CommunicationTag;
import com.chenyp.collaboration.model.TreeRecord;
import com.chenyp.collaboration.model.json.TreeRecordJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ValidateUtil;
import com.google.gson.reflect.TypeToken;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/11/9.
 */
public class CommunicationTopicShowFragment extends BaseFragment {

    @Bind(R.id.id_toolbar)
    public Toolbar tb;
    @Bind(R.id.id_tv_publisher)
    public TextView publisher;
    @Bind(R.id.id_tv_upload_date)
    public TextView uploadDate;
    @Bind(R.id.id_tv_summary)
    public TextView summary;
    @Bind(R.id.id_iv_photo)
    public ImageView photo;
    @Bind(R.id.id_tv_child_topics)
    public TextView childTopics;
    @Bind(R.id.id_recycler_view)
    public RecyclerView rv;
    @Bind(R.id.id_recycler_view_topics)
    public RecyclerView rvTopics;
    @Bind(R.id.id_fab)
    public FloatingActionButton topicActionButton;
    @Bind(R.id.id_scroll_view)
    public ObservableScrollView scrollView;

    private CommunicationTopicDetailHandler handler = new CommunicationTopicDetailHandler(this);

    public final static String TAG = "CommunicationTopicShowFragment";

    public static final String PARENT_LIST_JSON = "parent_json";

    private String topic;

    /**
     * @param parentJson 父主题的数据
     * @return
     */
    public static CommunicationTopicShowFragment newInstance(String parentJson, String parentListJson) {
        Bundle bundle = new Bundle();
        bundle.putString(JSON_DATA, parentJson);
        bundle.putString(PARENT_LIST_JSON, parentListJson);
        CommunicationTopicShowFragment fragment = new CommunicationTopicShowFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_communication_topic_show;
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

        rv.setLayoutManager(new FullyLinearLayoutManager(getContext()
                , LinearLayoutManager.VERTICAL, false));
        rvTopics.setLayoutManager(new FullyLinearLayoutManager(getContext()
                , LinearLayoutManager.HORIZONTAL, false));
        String parentListJson = getArguments().getString(PARENT_LIST_JSON);
        List<CommunicationTag> tags = GsonUtil.fromJson(parentListJson, new TypeToken<List<CommunicationTag>>() {
        });
        CommunicationTopicsTextAdapter adapter = new CommunicationTopicsTextAdapter(getContext(), tags);
        adapter.setOnTopicsTextItemClickListener(new CommunicationTopicsTextAdapter.OnTopicsTextItemClickListener() {

            @Override
            public void onTopicsTextItemClick(String tag) {
                if (tag != null) {
                    getOnNavigationUIListener().goBackCommunicationTopicShowPage(tag);
                } else {
                    updateTopicDetail();
                }
            }

        });
        RecyclerView.RecycledViewPool pool = new RecyclerView.RecycledViewPool();
        pool.setMaxRecycledViews(0, 10);
        rvTopics.setRecycledViewPool(pool);
        rvTopics.setAdapter(adapter);
        rvTopics.scrollToPosition(adapter.getItemCount() - 1);
        //漂浮按钮
        topicActionButton.attachToScrollView(scrollView);

    }

    @Override
    protected void initData() {
        final String parentJson = getArguments().getString(JSON_DATA);
        TreeRecord parentRecord = GsonUtil.fromJson(parentJson, TreeRecord.class);
        topic = String.valueOf(parentRecord.getId());
        tb.setTitle(parentRecord.getText());
        publisher.setText(parentRecord.getPublisher());
        uploadDate.setText(DateUtils.formatDateTime(getContext(),
                parentRecord.getPdate(), DateUtils.FORMAT_SHOW_YEAR));
        summary.setText(parentRecord.getSummary());
        //显示头像
        Glide.with(getContext())
                .load(parentRecord.getPhoto())
                .centerCrop()
                .thumbnail(0.1f)
                .placeholder(R.drawable.photo_placeholder)
                .error(R.drawable.photo_error)
                .into(photo);
        topicActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnNavigationUIListener().navigateToCommunicationTopicAddPage(parentJson);
            }
        });
        updateTopicDetail();
    }

    private void updateTopicDetail() {
        String type = "7";
        HttpPostUtil postUtil = new HttpPostUtil();
        postUtil.addTextParameter("type", type);
        postUtil.addTextParameter("topic", topic);
        postUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {

            @Override
            public void onSuccess(byte[] outputStream) {
                String json = new String(outputStream);
                Log.i(TAG, json);
                TreeRecordJsonData jsonData = GsonUtil.fromJson(json, TreeRecordJsonData.class);
                if (jsonData.isSuccess()) {
                    //数据填充
                    final List<TreeRecord> records = jsonData.getChildren();
                    //final TreeRecord childRecord = records.get(0);

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (ValidateUtil.isValid(records)) {
                                childTopics.setVisibility(View.VISIBLE);
                                CommunicationTopicAdapter adapter = new CommunicationTopicAdapter(getContext(), records);
                                adapter.setOnTopicsItemClickListener(new CommunicationTopicAdapter.OnTopicsItemClickListener() {

                                    @Override
                                    public void OnTopicsItemClick(TreeRecord treeRecord) {
                                        String parentJson = getArguments().getString(PARENT_LIST_JSON);
                                        List<CommunicationTag> tags = GsonUtil.fromJson(parentJson, new TypeToken<List<CommunicationTag>>() {
                                        });
                                        CommunicationTag tag = new CommunicationTag();
                                        tag.setTitle(treeRecord.getText());
                                        tag.setTag(TAG + treeRecord.getId());
                                        tags.add(tag);
                                        getOnNavigationUIListener().navigateToCommunicationTopicShowPage(
                                                GsonUtil.toJson(treeRecord), GsonUtil.toJson(tags));
                                    }

                                });
                                rv.setAdapter(adapter);
                            }
                        }

                    });

                } else {
                    if (jsonData.getMessage().equals("请登陆后再执行操作")) {
                        /*replaceChildFragmentAndAddBackStack(R.id.id_fl_tab_achievement,
                                TipsLoginFragment.newInstance());*/
                    }
                }
            }

            @Override
            public void onError(String error) {

            }

        });
    }

    @Override
    public String getTransactionTag() {
        String json = getArguments().getString(JSON_DATA);
        TreeRecord record = GsonUtil.fromJson(json, TreeRecord.class);
        return TAG + record.getId();
    }

    public static class CommunicationTopicDetailHandler extends Handler {

        private CommunicationTopicShowFragment communicationTopicShowFragment;

        public CommunicationTopicDetailHandler(CommunicationTopicShowFragment communicationTopicShowFragment) {
            this.communicationTopicShowFragment = communicationTopicShowFragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

    }

}
