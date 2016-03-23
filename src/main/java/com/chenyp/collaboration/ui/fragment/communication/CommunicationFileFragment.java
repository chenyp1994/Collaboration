package com.chenyp.collaboration.ui.fragment.communication;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.CommunicationFileAdapter;
import com.chenyp.collaboration.adapter.decorator.DividerItemDecoration;
import com.chenyp.collaboration.adapter.layoutManager.FullyLinearLayoutManager;
import com.chenyp.collaboration.model.Discuss;
import com.chenyp.collaboration.model.DocInfo;
import com.chenyp.collaboration.model.TreeRecord;
import com.chenyp.collaboration.model.json.DiscussFileJsonData;
import com.chenyp.collaboration.model.json.DiscussJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ValidateUtil;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/11/26.
 */
public class CommunicationFileFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.id_recycler_view)
    public RecyclerView rv;
    @Bind(R.id.id_toolbar)
    public Toolbar tb;
    @Bind(R.id.id_fab)
    public FloatingActionButton addFileFloatingActionButton;
    @Bind(R.id.id_swipe_refresh)
    public SwipeRefreshLayout refreshLayout;

    private String discuss;

    private CommunicationFileHandler handler = new CommunicationFileHandler(this);

    public final String TAG = getClass().getSimpleName();

    public static CommunicationFileFragment newInstance(Bundle bundle) {
        CommunicationFileFragment fragment = new CommunicationFileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_communication_file;
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
        tb.setTitle("文档列表");
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        rv.setLayoutManager(new FullyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        if (getArguments().getBoolean(CommunicationShowFragment.MODIFY_MODE)) {
            //新建第一级主题
            addFileFloatingActionButton.setVisibility(View.VISIBLE);
            addFileFloatingActionButton.attachToRecyclerView(rv);
            addFileFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //getOnNavigationUIListener().navigateToCommunicationTopicAddPage(null);
                }
            });
        }
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3,
                R.color.color4);
        //避免上拉刷新事件冲突
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                refreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

        });
    }

    @Override
    protected void initData() {
        getDocumentList();
    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    private void getDocumentList() {
        TreeRecord record = GsonUtil.fromJson(getArguments().getString(JSON_DATA), TreeRecord.class);
        discuss = String.valueOf(record.getId());
        refreshLayout.setRefreshing(true);
        HttpPostUtil postUtil = new HttpPostUtil();
        postUtil.addTextParameter("type", "9");
        postUtil.addTextParameter("discuss", discuss);
        postUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {
            @Override
            public void onSuccess(byte[] outputStream) {
                String json = new String(outputStream);
                Log.i(TAG, json);
                if (!ValidateUtil.isValid(json)) {
                    return;
                }
                DiscussFileJsonData jsonData = GsonUtil.fromJson(json, DiscussFileJsonData.class);
                if (jsonData.isSuccess()) {
                    final List<DocInfo> docList = jsonData.getDocList();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            rv.setAdapter(new CommunicationFileAdapter(getContext(), docList));
                        }
                    });

                } else {

                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onRefresh() {

    }

    public static class CommunicationFileHandler extends Handler {

        private CommunicationFileFragment fragment;

        public CommunicationFileHandler(CommunicationFileFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

}
