package com.chenyp.collaboration.ui.fragment.achievement;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.chenyp.collaboration.adapter.DetailsAdapter;
import com.chenyp.collaboration.adapter.layoutManager.FullyLinearLayoutManager;
import com.chenyp.collaboration.model.TreeRecord;
import com.chenyp.collaboration.model.json.ExhibitDetailJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.util.GlideCircleTransform;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ValidateUtil;

import butterknife.Bind;

/**
 * Created by change on 2015/11/2.
 */
public class AchievementShowFragment extends BaseFragment {

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
    @Bind(R.id.id_tv_details)
    public TextView detailList;
    @Bind(R.id.id_recycler_view)
    public RecyclerView rv;

    public final String TAG = getClass().getSimpleName();

    private Context mContext;

    public final static String EXHIBIT_ID = "exhibit";

    public final static String MODIFY_MODE = "modify_mode";

    private AchievementShowHandler handler = new AchievementShowHandler(this);

    public static AchievementShowFragment newInstance(String json) {
        Bundle bundle = new Bundle();
        bundle.putString(JSON_DATA, json);
        bundle.putString(EXHIBIT_ID, String.valueOf(GsonUtil.fromJson(json, TreeRecord.class).getId()));
        bundle.putBoolean(MODIFY_MODE, false);
        AchievementShowFragment fragment = new AchievementShowFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static AchievementShowFragment newInstance(String json, boolean modifyMode) {
        Bundle bundle = new Bundle();
        bundle.putString(JSON_DATA, json);
        bundle.putString(EXHIBIT_ID, String.valueOf(GsonUtil.fromJson(json, TreeRecord.class).getId()));
        bundle.putBoolean(MODIFY_MODE, modifyMode);
        AchievementShowFragment fragment = new AchievementShowFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_achievement_show;
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
    }

    @Override
    protected void initData() {
        updateAchievementUI(GsonUtil.fromJson(getArguments().getString(JSON_DATA), TreeRecord.class));
        updateAchievement();
    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setMenuVisibility(true);
    }

    @Override
    public void onAttach(Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getArguments().getBoolean(MODIFY_MODE)) {
            inflater.inflate(R.menu.menu_edit, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            getOnNavigationUIListener().navigateToAchievementModifyPage(getArguments());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateAchievement() {
        HttpPostUtil httpPostUtil = new HttpPostUtil();
        String type = "2";
        String exhibit_id = getArguments().getString(EXHIBIT_ID);
        Log.i(TAG, type);

        httpPostUtil.addTextParameter(URL_TYPE, type);
        httpPostUtil.addTextParameter(EXHIBIT_ID, exhibit_id);
        httpPostUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {

            @Override
            public void onSuccess(byte[] outputStream) {
                String json = new String(outputStream);
                Log.i(TAG, json);
                getArguments().putString(JSON_DATA, json);
                parseJsonData(json);
            }

            @Override
            public void onError(String error) {

            }

        });
    }

    public void parseJsonData(String json) {
        final ExhibitDetailJsonData jsonData = GsonUtil.fromJson(json, ExhibitDetailJsonData.class);
        if (jsonData.isSuccess()) {
            updateAchievementUI(jsonData);
        } else {
            //失败的操作
        }
    }

    private void updateAchievementUI(final ExhibitDetailJsonData jsonData) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //显示头像
                Glide.with(mContext)
                        .load(jsonData.getPhoto())
                        .centerCrop()
                        .thumbnail(0.1f)
                        .transform(new GlideCircleTransform(mContext))
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .into(photo);

                tb.setTitle(jsonData.getTitle());
                publisher.setText(jsonData.getPublisher());
                uploadDate.setText(DateUtils.formatDateTime(getContext(),
                        jsonData.getPdate(), DateUtils.FORMAT_SHOW_YEAR));
                summary.setText(getContext().getString(R.string.text_summary_format, jsonData.getSummary()));
                count.setText(String.valueOf(jsonData.getCount()));
                if (ValidateUtil.isValid(jsonData.getDetails())) {
                    rv.setLayoutManager(new FullyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    detailList.setVisibility(View.VISIBLE);
                    DetailsAdapter detailsAdapter = new DetailsAdapter(getContext(), jsonData);
                    if (getArguments().getBoolean(MODIFY_MODE)) {
                        detailsAdapter.setModifyMode(true);
                    }
                    rv.setAdapter(detailsAdapter);
                }
            }
        });
    }

    private void updateAchievementUI(final TreeRecord record) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //显示头像
                Glide.with(getContext())
                        .load(record.getPhoto())
                        .centerCrop()
                        .thumbnail(0.1f)
                        .transform(new GlideCircleTransform(mContext))
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .into(photo);

                tb.setTitle(record.getText());
                publisher.setText(record.getPublisher());
                uploadDate.setText(DateUtils.formatDateTime(getContext(),
                        record.getPdate(), DateUtils.FORMAT_SHOW_YEAR));
                summary.setText(getContext().getString(R.string.text_summary_format, record.getSummary()));
                count.setText(String.valueOf(record.getCount()));
                if (ValidateUtil.isValid(record.getDetails())) {
                    detailList.setVisibility(View.VISIBLE);
                    DetailsAdapter detailsAdapter = new DetailsAdapter(getContext(), record.getDetails());
                    if (getArguments().getBoolean(MODIFY_MODE)) {
                        detailsAdapter.setModifyMode(true);
                    }
                    rv.setLayoutManager(new FullyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    //rv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                    rv.setAdapter(detailsAdapter);
                }
            }
        });
    }


    public static class AchievementShowHandler extends Handler {

        private AchievementShowFragment showFragment;

        public AchievementShowHandler(AchievementShowFragment achievementShowFragment) {
            this.showFragment = achievementShowFragment;
        }

    }
}
