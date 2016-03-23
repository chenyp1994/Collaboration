package com.chenyp.collaboration.ui.fragment.achievement.details;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.layoutManager.FullyGridLayoutManager;
import com.chenyp.collaboration.adapter.ShowPhotosAdapter;
import com.chenyp.collaboration.model.Detail;
import com.chenyp.collaboration.model.json.ExhibitDetailJsonData;
import com.chenyp.collaboration.model.json.ExhibitJsonData;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ImageUtils;
import com.chenyp.collaboration.util.ValidateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;

/**
 * Created by change on 2015/11/4.
 */
public class AchievementDetailModifyFragment extends BaseFragment {

    @Bind(R.id.id_toolbar)
    public Toolbar tb;
    @Bind(R.id.id_et_project_detail)
    public EditText pjDetail;
    @Bind(R.id.id_recycler_view)
    public RecyclerView rv;

    public static final String POSITION = "position";

    public final String TAG = getClass().getSimpleName();

    private ShowPhotosAdapter showPhotosAdapter;

    private OnDetailModifyListener onDetailModifyListener;

    //protected static String exhibit;

    private String detailId;

    /*public static void setExhibit(String exhibit) {
        AchievementDetailModifyFragment.exhibit = exhibit;
    }*/

    public interface OnDetailModifyListener {
        void onModify(int position, Detail detail);
    }

    public void setOnDetailSubmitListener(OnDetailModifyListener onDetailModifyListener) {
        this.onDetailModifyListener = onDetailModifyListener;
    }

    public static AchievementDetailModifyFragment newInstance(String json, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(JSON_DATA, json);
        bundle.putInt(POSITION, position);
        AchievementDetailModifyFragment fragment = new AchievementDetailModifyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_achievement_detail_edit;
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
        tb.setTitle("更新项目成果详细信息");

        String json = getArguments().getString(JSON_DATA);
        Detail detail = GsonUtil.fromJson(json, Detail.class);
        detailId = String.valueOf(detail.getId());
        pjDetail.setText(detail.getDetail());
        if (showPhotosAdapter == null) {
            List<String> urls = new ArrayList<>();
            if (ValidateUtil.isValid(detail.getImage1())) {
                urls.add(detail.getImage1());
            }
            if (ValidateUtil.isValid(detail.getImage2())) {
                urls.add(detail.getImage2());
            }
            if (ValidateUtil.isValid(detail.getImage3())) {
                urls.add(detail.getImage3());
            }
            showPhotosAdapter = new ShowPhotosAdapter(getContext(), urls, true);
        }
        rv.setLayoutManager(new FullyGridLayoutManager(getContext(), 3));
        //rv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        rv.setAdapter(showPhotosAdapter);
    }

    @Override
    protected void initData() {

    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    private void submitDetail() {
        final List<String> temp = new ArrayList<>();
        temp.addAll(showPhotosAdapter.getUrls());
        String detailText = pjDetail.getText().toString();
        if (!ValidateUtil.isValid(detailText)) {
            Toast.makeText(getContext(), "详细内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (onDetailModifyListener != null) {
            Detail detail = new Detail();
            detail.setDetail(detailText);
            for (int i = 0; i < temp.size(); i++) {
                if (ValidateUtil.isValid(temp.get(i))) {
                    switch (i) {
                        case 0:
                            detail.setImage1(temp.get(i));
                            break;
                        case 1:
                            detail.setImage2(temp.get(i));
                            break;
                        case 2:
                            detail.setImage3(temp.get(i));
                            break;
                    }
                }
            }
            int position = getArguments().getInt(POSITION);
            onDetailModifyListener.onModify(position, detail);
            getOnNavigationUIListener().goBackFromCurrentPage();
        } else {
            List<String> temps = new ArrayList<>();
            temps.addAll(temp);

            for (int i = temps.size(); i < 3; i++) {
                temps.add("");
            }
            Map<String, String> urls = new HashMap<>();
            for (String url : temps) {
                File file = new File(url);
                if (ValidateUtil.isValid(url) && !file.isFile()) {
                    int index = Integer.valueOf(url.substring(url.length() - 1, url.length()));
                    urls.put("image" + index, "");
                } else {
                    for (int i = 0; i < temps.size(); i++) {
                        if (!urls.containsKey("image" + (i + 1))) {
                            urls.put("image" + (i + 1), url);
                            break;
                        }
                    }
                }
            }
            if (temp.size() >= 3) {
                Set<String> keySet = urls.keySet();
                for (String name : keySet) {
                    String value = urls.get(name);
                    if (ValidateUtil.isValid(value)) {
                        HttpPostUtil urlPost = new HttpPostUtil();
                        urlPost.addTextParameter("type", "21");
                        urlPost.addTextParameter("exhibitDetail", detailId);
                        urlPost.addTextParameter("image", name.substring(name.length() - 1, name.length()));
                        urlPost.addFileParameter(name, ImageUtils.compress(value));
                        urlPost.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {
                            @Override
                            public void onSuccess(byte[] outputStream) {
                                String json = new String(outputStream);
                                Log.i(TAG, json);
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                    }
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    int index = i + 1;
                    String name = "image" + index;
                    //if (temp.size() <= i + 1) {
                    String value = urls.get(name);
                    if (ValidateUtil.isValid(value)) {
                        HttpPostUtil urlPost = new HttpPostUtil();
                        urlPost.addTextParameter("type", "21");
                        urlPost.addTextParameter("exhibitDetail", detailId);
                        urlPost.addTextParameter("image", name.substring(name.length() - 1, name.length()));
                        urlPost.addFileParameter(name, ImageUtils.compress(value));
                        urlPost.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {
                            @Override
                            public void onSuccess(byte[] outputStream) {
                                String json = new String(outputStream);
                                Log.i(TAG, json);
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                    } else {
                        HttpPostUtil urlPost = new HttpPostUtil();
                        urlPost.addTextParameter("type", "21");
                        urlPost.addTextParameter("exhibitDetail", detailId);
                        urlPost.addTextParameter("image", name.substring(name.length() - 1, name.length()));
                        urlPost.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {
                            @Override
                            public void onSuccess(byte[] outputStream) {
                                String json = new String(outputStream);
                                Log.i(TAG, json);
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                    }
                }
            }
            String type = "20";
            HttpPostUtil httpPostUtil = new HttpPostUtil();
            httpPostUtil.addTextParameter("type", type);
            httpPostUtil.addTextParameter("exhibitDetail", detailId);
            httpPostUtil.addTextParameter("detail", detailText);
            httpPostUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {

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
                submitDetail();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validate() {
        String detailText = pjDetail.getText().toString();
        if (!ValidateUtil.isValid(detailText)) {
            Toast.makeText(getContext(), "详细内容不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
