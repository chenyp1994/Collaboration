package com.chenyp.collaboration.ui.fragment.achievement.details;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.layoutManager.FullyGridLayoutManager;
import com.chenyp.collaboration.adapter.ShowPhotosAdapter;
import com.chenyp.collaboration.model.Detail;
import com.chenyp.collaboration.model.json.ExhibitDetailJsonData;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.util.GsonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/11/11.
 */
public class AchievementDetailShowFragment extends BaseFragment
        implements AchievementDetailModifyFragment.OnDetailModifyListener {

    @Bind(R.id.id_toolbar)
    public Toolbar tb;
    @Bind(R.id.id_iv_photo)
    public ImageView photo;
    @Bind(R.id.id_tv_publisher)
    public TextView publisher;
    @Bind(R.id.id_tv_upload_date)
    public TextView uploadDate;
    @Bind(R.id.id_tv_read_count)
    public TextView count;
    @Bind(R.id.id_tv_detail_text)
    public TextView detailText;
    @Bind(R.id.id_recycler_view)
    public RecyclerView rv;

    public final static String POSITION = "position";

    public final static String MODIFY_MODE = "modify_mode";

    public final String TAG = getClass().getSimpleName();

    private ShowPhotosAdapter showPhotosAdapter;

    public static AchievementDetailShowFragment newInstance(String json, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(JSON_DATA, json);
        bundle.putInt(POSITION, position);
        bundle.putBoolean(MODIFY_MODE, false);
        AchievementDetailShowFragment fragment = new AchievementDetailShowFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static AchievementDetailShowFragment newInstance(String json, int position, boolean modifyMode) {
        Bundle bundle = new Bundle();
        bundle.putString(JSON_DATA, json);
        bundle.putInt(POSITION, position);
        bundle.putBoolean(MODIFY_MODE, modifyMode);
        AchievementDetailShowFragment fragment = new AchievementDetailShowFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_achievement_details_show;
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
        int position = getArguments().getInt(POSITION);
        ExhibitDetailJsonData jsonData = GsonUtil.fromJson(getArguments().getString(JSON_DATA), ExhibitDetailJsonData.class);
        Detail detail = jsonData.getDetails().get(position);
        //显示头像
        Glide.with(getContext())
                .load(jsonData.getPhoto())
                .centerCrop()
                .thumbnail(0.1f)
                .placeholder(R.drawable.photo_placeholder)
                .error(R.drawable.photo_error)
                .into(photo);
        tb.setTitle(jsonData.getTitle());
        publisher.setText(jsonData.getPublisher());
        uploadDate.setText(DateUtils.formatDateTime(getContext(),
                jsonData.getPdate(), DateUtils.FORMAT_SHOW_YEAR));
        count.setText(String.valueOf(jsonData.getCount()));
        detailText.setText(getContext().getString(R.string.text_summary_format, detail.getDetail()));
        if (showPhotosAdapter == null) {
            List<String> urls = new ArrayList<>();
            if (detail.getImage1() != null) {
                urls.add(detail.getImage1());
            }
            if (detail.getImage2() != null) {
                urls.add(detail.getImage2());
            }
            if (detail.getImage3() != null) {
                urls.add(detail.getImage3());
            }
            showPhotosAdapter = new ShowPhotosAdapter(getContext(), urls);
        }
        rv.setLayoutManager(new FullyGridLayoutManager(getContext(), 3));
        //rv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        rv.setAdapter(showPhotosAdapter);
    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments().getBoolean(MODIFY_MODE)) {
            setMenuVisibility(true);
        } else {
            setMenuVisibility(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            int position = getArguments().getInt(POSITION);
            ExhibitDetailJsonData jsonData = GsonUtil.fromJson(getArguments().getString(JSON_DATA), ExhibitDetailJsonData.class);
            Detail detail = jsonData.getDetails().get(position);
            getOnNavigationUIListener().navigateToAchievementDetailModifyPage(GsonUtil.toJson(detail), position, null);
            //AchievementDetailModifyFragment.setExhibit(String.valueOf(jsonData.getExhibit()));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onModify(int position, Detail detail) {
        //修改本地数据
        detailText.setText(getContext().getString(R.string.text_summary_format, detail.getDetail()));
        List<String> urls = new ArrayList<>();
        if (detail.getImage1() != null) {
            urls.add(detail.getImage1());
        }
        if (detail.getImage2() != null) {
            urls.add(detail.getImage2());
        }
        if (detail.getImage3() != null) {
            urls.add(detail.getImage3());
        }
        rv.setAdapter(new ShowPhotosAdapter(getContext(), urls));
    }
}
