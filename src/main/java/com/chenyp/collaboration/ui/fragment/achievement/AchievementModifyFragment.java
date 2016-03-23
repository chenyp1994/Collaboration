package com.chenyp.collaboration.ui.fragment.achievement;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.chenyp.collaboration.adapter.DetailsAdapter;
import com.chenyp.collaboration.adapter.layoutManager.FullyLinearLayoutManager;
import com.chenyp.collaboration.model.Detail;
import com.chenyp.collaboration.model.json.ExhibitDetailJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.ui.fragment.achievement.details.AchievementDetailAddFragment;
import com.chenyp.collaboration.util.AuthorizeWatcher;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ValidateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/11/4.
 */
public class AchievementModifyFragment extends BaseFragment {

    @Bind(R.id.id_toolbar)
    public Toolbar tb;
    @Bind(R.id.id_et_project_title)
    public EditText title;
    @Bind(R.id.id_et_project_summary)
    public EditText summary;
    @Bind(R.id.id_et_authorize)
    public EditText authorize;
    @Bind(R.id.id_recycler_view)
    public RecyclerView rv;
    @Bind(R.id.id_rg_authorize)
    public RadioGroup rg;
    @Bind(R.id.id_rb_all_user)
    public RadioButton allUser;
    @Bind(R.id.id_rb_specific_user)
    public RadioButton specificUser;

    private AchievementModifyHandler handler = new AchievementModifyHandler(this);

    private DetailsAdapter detailsAdapter;

    public final String TAG = getClass().getSimpleName();

    public static AchievementModifyFragment newInstance(String exhibit) {
        Bundle bundle = new Bundle();
        bundle.putString(AchievementDetailAddFragment.EXHIBIT_ID, exhibit);
        AchievementModifyFragment fragment = new AchievementModifyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static AchievementModifyFragment newInstance(Bundle bundle) {
        AchievementModifyFragment fragment = new AchievementModifyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_achievement_edit;
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
        parseJsonData(getArguments().getString(JSON_DATA));
        updateAchievement(getArguments().getString(JSON_DATA));
    }

    private void updateAchievement(String json) {
        ExhibitDetailJsonData jsonData = GsonUtil.fromJson(json, ExhibitDetailJsonData.class);
        HttpPostUtil postUtil = new HttpPostUtil();
        postUtil.addTextParameter("type", "2");
        postUtil.addTextParameter("exhibit", String.valueOf(jsonData.getExhibit()));
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

    private void submitAchievement(String titleText, String summaryText) {
        String type = "19";
        String exhibit = getArguments().getString(AchievementShowFragment.EXHIBIT_ID);
        String authorizeText = authorize.getVisibility() == View.GONE ? "all" : authorize.getText().toString();

        HttpPostUtil postUtil = new HttpPostUtil();
        postUtil.addTextParameter("type", type);
        postUtil.addTextParameter("exhibit", exhibit);
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

    public void parseJsonData(String json) {
        ExhibitDetailJsonData jsonData = GsonUtil.fromJson(json, ExhibitDetailJsonData.class);
        if (jsonData.isSuccess()) {
            //更新UI
            updateAchievementUI(jsonData);
        } else {
            //失败的操作
        }
    }

    private void updateAchievementUI(final ExhibitDetailJsonData jsonData) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                tb.setTitle("更新项目成果展示");
                title.setText(jsonData.getTitle());
                summary.setText(jsonData.getSummary());
                if (jsonData.getAuthorize().equals("all")) {
                    allUser.setChecked(true);
                    specificUser.setChecked(false);
                } else {
                    allUser.setChecked(false);
                    specificUser.setChecked(true);
                    authorize.setText(jsonData.getAuthorize());
                }
                /*List<Detail> temp;
                if (detailsAdapter == null) {
                    temp = !ValidateUtil.isValid(jsonData.getDetails()) ?
                            new ArrayList<Detail>() : jsonData.getDetails();
                } else {
                    temp = detailsAdapter.getDetails();
                }
                jsonData.setDetails(temp);*/
                detailsAdapter = new DetailsAdapter(getContext(), jsonData, true);
                rv.setLayoutManager(new FullyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
                //rv.setLayoutManager(new FullyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                //rv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                rv.setAdapter(detailsAdapter);
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
                submitAchievement(title.getText().toString(), summary.getText().toString());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validate() {
        String titleText = title.getText().toString();
        String summaryText = summary.getText().toString();
        if (!ValidateUtil.isValid(titleText)) {
            Toast.makeText(getContext(), "主题名称不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!ValidateUtil.isValid(summaryText)) {
            Toast.makeText(getContext(), "主题简介不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static class AchievementModifyHandler extends Handler {

        private AchievementModifyFragment achievementModifyFragment;

        public AchievementModifyHandler(AchievementModifyFragment achievementModifyFragment) {
            this.achievementModifyFragment = achievementModifyFragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

}
