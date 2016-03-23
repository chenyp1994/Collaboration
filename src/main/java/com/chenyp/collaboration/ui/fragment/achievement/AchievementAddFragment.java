package com.chenyp.collaboration.ui.fragment.achievement;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.DetailsAdapter;
import com.chenyp.collaboration.adapter.decorator.DividerItemDecoration;
import com.chenyp.collaboration.adapter.layoutManager.FullyLinearLayoutManager;
import com.chenyp.collaboration.model.Detail;
import com.chenyp.collaboration.model.Exhibit;
import com.chenyp.collaboration.model.json.ExhibitDetailJsonData;
import com.chenyp.collaboration.model.json.ExhibitJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.util.AuthorizeWatcher;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ImageUtils;
import com.chenyp.collaboration.util.ValidateUtil;

import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/11/4.
 */
public class AchievementAddFragment extends BaseFragment /*implements View.OnClickListener*/ {

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

    private DetailsAdapter detailsAdapter;

    public final String TAG = getClass().getSimpleName();

    public static AchievementAddFragment newInstance() {
        Bundle bundle = new Bundle();
        AchievementAddFragment fragment = new AchievementAddFragment();
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
        tb.setTitle("新建项目成果展示");

        if (detailsAdapter == null) {
            detailsAdapter = new DetailsAdapter(getContext(), true);
        }
        rv.setLayoutManager(new FullyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        //rv.setLayoutManager(new FullyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        rv.setAdapter(detailsAdapter);
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

    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    private void submitAchievement(String titleText, String summaryText) {
        String type = "17";
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("正在上传...")
                .show();
        String authorizeText = authorize.getVisibility() == View.GONE ? "all" : authorize.getText().toString();
        HttpPostUtil postUtil = new HttpPostUtil();
        postUtil.addTextParameter("type", type);
        postUtil.addTextParameter("title", titleText);
        postUtil.addTextParameter("summary", summaryText);
        postUtil.addTextParameter("authorize", authorizeText);
        postUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {

            @Override
            public void onSuccess(byte[] outputStream) {
                String json = new String(outputStream);
                Log.i(TAG, json);
                ExhibitJsonData jsonData = GsonUtil.fromJson(json, ExhibitJsonData.class);
                if (jsonData.isSuccess()) {
                    Exhibit exhibit = jsonData.getExhibit();
                    submitDetail(exhibit.getId());
                } else {
                    if (jsonData.getMessage().equals("请登陆后再执行操作")) {
                        Log.i(TAG, jsonData.getMessage());
                    }
                }
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.i(TAG, error);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                    }
                });
            }

        });
    }

    private void submitDetail(Long exhibit) {
        List<Detail> details = detailsAdapter.getDetails();
        for (Detail detail : details) {
            String type = "18";
            HttpPostUtil httpPostUtil = new HttpPostUtil();
            httpPostUtil.addTextParameter("type", type);
            httpPostUtil.addTextParameter("exhibit", String.valueOf(exhibit));
            httpPostUtil.addTextParameter("detail", detail.getDetail());
            //修改这里
            if (ValidateUtil.isValid(detail.getImage1())) {
                httpPostUtil.addFileParameter("image1", ImageUtils.compress(detail.getImage1()));
            }
            if (ValidateUtil.isValid(detail.getImage2())) {
                httpPostUtil.addFileParameter("image2", ImageUtils.compress(detail.getImage2()));
            }
            if (ValidateUtil.isValid(detail.getImage3())) {
                httpPostUtil.addFileParameter("image3", ImageUtils.compress(detail.getImage3()));
            }
            httpPostUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {

                @Override
                public void onSuccess(byte[] outputStream) {
                    String json = new String(outputStream);
                    Log.i(TAG, json);
                    ExhibitJsonData jsonData = GsonUtil.fromJson(json, ExhibitJsonData.class);
                    if (jsonData.isSuccess()) {
                        getOnNavigationUIListener().goBackFromCurrentPage();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.i(TAG, error);
                }

            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_submit, menu);
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
}
