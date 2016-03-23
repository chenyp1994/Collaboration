package com.chenyp.collaboration.ui.fragment.tab;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.CommunicationAdapter;
import com.chenyp.collaboration.adapter.decorator.DividerItemDecoration;
import com.chenyp.collaboration.model.TreeRecord;
import com.chenyp.collaboration.model.json.TreeRecordJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.ui.fragment.TipsLoginFragment;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ValidateUtil;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


/**
 * Created by change on 2015/10/23.
 */
public class CommunicationTabFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.id_recycler_view)
    public RecyclerView rv;
    @Bind(R.id.id_fab)
    public FloatingActionButton addActionButton;
    @Bind(R.id.id_swipe_refresh)
    public SwipeRefreshLayout refreshLayout;

    public CommunicationHandler handler = new CommunicationHandler(this);

    private CommunicationAdapter communicationAdapter;

    private Context mContext;

    /**
     * 日志的TAG
     */
    public final String TAG = getClass().getSimpleName();

    public static CommunicationTabFragment newInstance(Bundle bundle) {
        CommunicationTabFragment fragment = new CommunicationTabFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_communication;
    }

    @Override
    protected void initView() {
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
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
        updateCommunication();
    }

    @Override
    protected void initData() {
        if ("沟通项目".equals(getArguments().getString(TRANSACTION_TAG_TYPE))) {
            addActionButton.setVisibility(View.VISIBLE);
            addActionButton.attachToRecyclerView(rv);
            addActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOnNavigationUIListener().navigateToCommunicationAddPage();
                }
            });
        }
    }

    @Override
    public String getTransactionTag() {
        return getArguments().getString(TRANSACTION_TAG_TYPE);
    }

    public void updateCommunication() {
        Bundle bundle = getArguments();
        final String type = bundle.getString(URL_TYPE);
        Log.i(TAG, type);
        refreshLayout.setRefreshing(true);
        HttpPostUtil postUtil = new HttpPostUtil();
        postUtil.addTextParameter("type", type);
        postUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {

                    @Override
                    public void onSuccess(byte[] outputStream) {
                        String json = new String(outputStream);
                        Log.i(TAG, json);
                        TreeRecordJsonData jsonData = GsonUtil.fromJson(json, TreeRecordJsonData.class);
                        if (jsonData.isSuccess()) {
                            TreeRecord record = jsonData.getChildren().get(0);
                            List<TreeRecord> records = ValidateUtil.isValid(record.getChildren()) ?
                                    record.getChildren() : new ArrayList<TreeRecord>();

                            communicationAdapter = new CommunicationAdapter(mContext, records);
                            //更新UI
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    rv.setAdapter(communicationAdapter);
                                    refreshLayout.setRefreshing(false);
                                }

                            });
                            communicationAdapter.setOnCommunicationClickListener(new CommunicationAdapter.onCommunicationClickListener() {

                                @Override
                                public void onCommunicationClick(String json) {
                                    if ("沟通项目".equals(getArguments().getString(TRANSACTION_TAG_TYPE))) {
                                        getOnNavigationUIListener().navigateToCommunicationShowPage(json, true);
                                    } else {
                                        getOnNavigationUIListener().navigateToCommunicationShowPage(json);
                                    }
                                }
                            });
                            getChildFragmentManager().popBackStack();
                        } else {
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    refreshLayout.setRefreshing(false);
                                    if (!"沟通项目".equals(getArguments().getString(TRANSACTION_TAG_TYPE))) {
                                        replaceChildFragmentAndAddBackStack(R.id.id_fl_tab_communication, TipsLoginFragment.newInstance());
                                    }
                                }

                            });
                        }

                    }

                    @Override
                    public void onError(String error) {
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                refreshLayout.setRefreshing(false);
                                replaceChildFragmentAndAddBackStack(R.id.id_fl_tab_communication, TipsLoginFragment.newInstance());
                            }

                        });
                    }

                }

        );
    }

    @Override
    public void onAttach(Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        updateCommunication();
    }

    public static class CommunicationHandler extends Handler {

        private CommunicationTabFragment communicationFragment;

        public CommunicationHandler(CommunicationTabFragment communicationFragment) {
            this.communicationFragment = communicationFragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

    }
}
