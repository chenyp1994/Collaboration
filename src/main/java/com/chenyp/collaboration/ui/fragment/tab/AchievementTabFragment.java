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
import com.chenyp.collaboration.adapter.AchievementAdapter;
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
public class AchievementTabFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.id_recycler_view)
    public RecyclerView rv;
    @Bind(R.id.id_fab)
    public FloatingActionButton addActionButton;
    @Bind(R.id.id_swipe_refresh)
    public SwipeRefreshLayout refreshLayout;

    /**
     * 日志的TAG
     */
    public final String TAG = getClass().getSimpleName();

    public AchievementAdapter achievementAdapter;

    private AchievementHandler handler = new AchievementHandler(this);

    public static AchievementTabFragment newInstance(Bundle bundle) {
        AchievementTabFragment fragment = new AchievementTabFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_achievement;
    }

    @Override
    protected void initView() {
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
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
        if ("展示项目".equals(getArguments().getString(TRANSACTION_TAG_TYPE))) {
            addActionButton.setVisibility(View.VISIBLE);
            addActionButton.attachToRecyclerView(rv);
            addActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOnNavigationUIListener().navigateToAchievementAddPage();
                }
            });
        }
        updateAchievement();
    }

    @Override
    public String getTransactionTag() {
        return getArguments().getString(TRANSACTION_TAG_TYPE);
    }

    /**
     * 请求项目成果展示列
     */
    public void updateAchievement() {
        getChildFragmentManager().popBackStack();
        Bundle bundle = getArguments();
        String type = bundle.getString(URL_TYPE);
        refreshLayout.setRefreshing(true);
        Log.i(TAG, type);

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
                    getArguments().putString(JSON_DATA, GsonUtil.toJson(jsonData));
                    if (!"展示项目".equals(getArguments().getString(TRANSACTION_TAG_TYPE))) {
                        achievementAdapter = new AchievementAdapter(getContext(), records, true);
                        //getChildFragmentManager().popBackStack();
                    } else {
                        achievementAdapter = new AchievementAdapter(getContext(), records);
                    }
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
                            rv.setAdapter(achievementAdapter);
                            refreshLayout.setRefreshing(false);
                        }

                    });
                } else {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                            if (!"展示项目".equals(getTransactionTag())) {
                                replaceChildFragmentAndAddBackStack(R.id.id_fl_tab_achievement, TipsLoginFragment.newInstance());
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
                        if (!"展示项目".equals(getTransactionTag())) {
                            replaceChildFragmentAndAddBackStack(R.id.id_fl_tab_achievement, TipsLoginFragment.newInstance());
                        }
                    }

                });
            }

        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setHasOptionsMenu(true);
        setMenuVisibility(false);
        //容易出现BUG位置   注意
        if ("成果展示".equals(getArguments().getString(TRANSACTION_TAG_TYPE))) {
            setMenuVisibility(true);
        } else {
            setMenuVisibility(false);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        updateAchievement();
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if ("展示项目".equals(getArguments().getString(TRANSACTION_TAG_TYPE))) {
            inflater.inflate(R.menu.menu_my_achievement, menu);
        } else {
            inflater.inflate(R.menu.menu_achievement, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            getOnNavigationUIListener().navigateToAchievementAddPage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public static class AchievementHandler extends Handler {

        private AchievementTabFragment achievementFragment;

        public AchievementHandler(AchievementTabFragment achievementFragment) {
            this.achievementFragment = achievementFragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

    }
}