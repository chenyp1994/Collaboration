package com.chenyp.collaboration.ui.fragment.communication;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.CommunicationTopicAdapter;
import com.chenyp.collaboration.adapter.layoutManager.FullyLinearLayoutManager;
import com.chenyp.collaboration.model.CommunicationTag;
import com.chenyp.collaboration.model.Discuss;
import com.chenyp.collaboration.model.TreeRecord;
import com.chenyp.collaboration.model.json.DiscussJsonData;
import com.chenyp.collaboration.model.json.TreeRecordJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.ui.fragment.communication.topic.CommunicationTopicAddFragment;
import com.chenyp.collaboration.ui.fragment.communication.topic.CommunicationTopicShowFragment;
import com.chenyp.collaboration.util.GlideCircleTransform;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ValidateUtil;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/10/23.
 */
public class CommunicationShowFragment extends BaseFragment {

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
    @Bind(R.id.id_tv_read_count)
    public TextView count;
    @Bind(R.id.id_tv_topics)
    public TextView topics;
    @Bind(R.id.id_recycler_view)
    public RecyclerView rv;
    @Bind(R.id.id_fab)
    public FloatingActionButton discussActionButton;
    @Bind(R.id.id_scroll_view)
    public ObservableScrollView scrollView;

    public final String TAG = getClass().getSimpleName();

    private String discuss;

    public final static String MODIFY_MODE = "modify_mode";

    private CommunicationShowHandler handler = new CommunicationShowHandler(this);

    /**
     * @param json 开发沟通数据
     * @return fragment CommunicationShowFragment的实例
     */
    public static CommunicationShowFragment newInstance(String json) {
        Bundle bundle = new Bundle();
        bundle.putString(JSON_DATA, json);
        bundle.putBoolean(MODIFY_MODE, false);
        CommunicationShowFragment fragment = new CommunicationShowFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * @param json       开发沟通数据
     * @param modifyMode 编辑模式状态
     * @return fragment CommunicationShowFragment的实例
     */
    public static CommunicationShowFragment newInstance(String json, boolean modifyMode) {
        Bundle bundle = new Bundle();
        bundle.putString(JSON_DATA, json);
        bundle.putBoolean(MODIFY_MODE, modifyMode);
        CommunicationShowFragment fragment = new CommunicationShowFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_communication_show;
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
        //新建第一级主题
        discussActionButton.attachToScrollView(scrollView);
        discussActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnNavigationUIListener().navigateToCommunicationTopicAddPage(null);
            }
        });
    }

    @Override
    protected void initData() {
        String json = getArguments().getString(JSON_DATA);
        TreeRecord record = GsonUtil.fromJson(json, TreeRecord.class);
        discuss = String.valueOf(record.getId());

        CommunicationTopicAddFragment.setDiscuss(discuss);
        tb.setTitle(record.getText());
        summary.setText(record.getSummary());
        publisher.setText(record.getPublisher());
        uploadDate.setText(DateUtils.formatDateTime(getContext(),
                record.getPdate(), DateUtils.FORMAT_SHOW_YEAR));
        count.setText(String.valueOf(record.getCount()));
        //显示头像
        Glide.with(getContext())
                .load(record.getPhoto())
                .centerCrop()
                .thumbnail(0.1f)
                .transform(new GlideCircleTransform(getContext()))
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(photo);
        getDiscussData();
        updateTopicDetail();
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
                DiscussJsonData jsonData = GsonUtil.fromJson(json, DiscussJsonData.class);
                final Discuss discuss = jsonData.getDiscuss();
                if (jsonData.isSuccess()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tb.setTitle(discuss.getTitle());
                            summary.setText(discuss.getSummary());
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {

            }

        });
    }


    private void updateTopicDetail() {
        String type = "6";
        HttpPostUtil postUtil = new HttpPostUtil();
        postUtil.addTextParameter("type", type);
        postUtil.addTextParameter("discuss", discuss);
        postUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {

            @Override
            public void onSuccess(byte[] outputStream) {
                String json = new String(outputStream);
                Log.i(TAG, json);
                TreeRecordJsonData jsonData = GsonUtil.fromJson(json, TreeRecordJsonData.class);
                if (jsonData.isSuccess()) {
                    //数据填充
                    final List<TreeRecord> records = jsonData.getChildren();
                    //final TreeRecord record = records.get(0);

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            /*tb.setTitle(record.getText());
                            publisher.setText(record.getPublisher());
                            uploadDate.setText(DateUtils.formatDateTime(getContext(),
                                    record.getPdate(), DateUtils.FORMAT_SHOW_YEAR));
                            summary.setText(record.getSummary());
                            count.setText(String.valueOf(record.getCount()));
                            //显示头像
                            Glide.with(getContext())
                                    .load(record.getPhoto())
                                    .centerCrop()
                                    .thumbnail(0.1f)
                                    .placeholder(R.drawable.photo_placeholder)
                                    .error(R.drawable.photo_error)
                                    .into(photo);*/

                            if (ValidateUtil.isValid(records)) {
                                topics.setVisibility(View.VISIBLE);
                                CommunicationTopicAdapter adapter = new CommunicationTopicAdapter(getContext(), records);
                                adapter.setOnTopicsItemClickListener(new CommunicationTopicAdapter.OnTopicsItemClickListener() {

                                    @Override
                                    public void OnTopicsItemClick(TreeRecord treeRecord) {
                                        List<CommunicationTag> tags = new ArrayList<>();
                                        CommunicationTag tag = new CommunicationTag();
                                        tag.setTag(getTransactionTag());
                                        tag.setTitle(tb.getTitle().toString());
                                        tags.add(tag);
                                        tag = new CommunicationTag();
                                        tag.setTitle(treeRecord.getText());
                                        tag.setTag(CommunicationTopicShowFragment.TAG + treeRecord.getId());
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setMenuVisibility(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_communcation_show, menu);
        if (getArguments().getBoolean(MODIFY_MODE)) {
            menu.findItem(R.id.action_edit).setVisible(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_file_list:
                getOnNavigationUIListener().navigateToCommunicationFilePage(getArguments());
                break;
            case R.id.action_edit:
                getOnNavigationUIListener().navigateToCommunicationModifyPage(getArguments());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class CommunicationShowHandler extends Handler {

        private CommunicationShowFragment fragment;

        public CommunicationShowHandler(CommunicationShowFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

}
