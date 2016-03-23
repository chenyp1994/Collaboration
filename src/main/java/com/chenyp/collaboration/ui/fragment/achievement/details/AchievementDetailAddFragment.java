package com.chenyp.collaboration.ui.fragment.achievement.details;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.decorator.DividerItemDecoration;
import com.chenyp.collaboration.adapter.layoutManager.FullyGridLayoutManager;
import com.chenyp.collaboration.adapter.ShowPhotosAdapter;
import com.chenyp.collaboration.model.Detail;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.ValidateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/11/4.
 */
public class AchievementDetailAddFragment extends BaseFragment /*implements View.OnClickListener*/ {

    @Bind(R.id.id_toolbar)
    public Toolbar tb;
    @Bind(R.id.id_et_project_detail)
    public EditText pjDetail;
    @Bind(R.id.id_recycler_view)
    public RecyclerView rv;
    /*@Bind(R.id.id_btn_submit)
    public Button submit;
    @Bind(R.id.id_btn_cancel)
    public Button cancel;*/

    public final String TAG = getClass().getSimpleName();

    public final static String EXHIBIT_ID = "exhibit_id";

    private ShowPhotosAdapter showPhotosAdapter;

    private OnDetailAddListener onDetailAddListener;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public interface OnDetailAddListener {
        void onAdd(Detail detail);
    }

    public void setOnDetailSubmitListener(OnDetailAddListener onDetailAddListener) {
        this.onDetailAddListener = onDetailAddListener;
    }

    public static AchievementDetailAddFragment newInstance() {
        Bundle bundle = new Bundle();
        AchievementDetailAddFragment fragment = new AchievementDetailAddFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static AchievementDetailAddFragment newInstance(String exhibit) {
        Bundle bundle = new Bundle();
        bundle.putString(EXHIBIT_ID, exhibit);
        AchievementDetailAddFragment fragment = new AchievementDetailAddFragment();
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
        tb.setTitle("新建项目成果详细信息");

        if (showPhotosAdapter == null) {
            showPhotosAdapter = new ShowPhotosAdapter(getContext(), new ArrayList<String>(), true);
        }
        rv.setLayoutManager(new FullyGridLayoutManager(getContext(), 3));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        rv.setAdapter(showPhotosAdapter);
        /*submit.setOnClickListener(this);
        cancel.setOnClickListener(this);*/
    }

    @Override
    protected void initData() {

    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    /*@Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_btn_submit:
                submitDetail();
                break;
            case R.id.id_btn_cancel:
                getOnNavigationUIListener().goBackFromCurrentPage();
                break;
        }
    }*/

    private void submitDetail() {
        List<String> urls = showPhotosAdapter.getUrls();
        String detailText = pjDetail.getText().toString();
        if (!ValidateUtil.isValid(detailText)) {
            Toast.makeText(getContext(), "详细内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (onDetailAddListener != null) {
            Detail detail = new Detail();
            detail.setDetail(detailText);
            for (int i = 0; i < urls.size(); i++) {
                if (ValidateUtil.isValid(urls.get(i))) {
                    switch (i) {
                        case 0:
                            detail.setImage1(urls.get(i));
                            break;
                        case 1:
                            detail.setImage2(urls.get(i));
                            break;
                        case 2:
                            detail.setImage3(urls.get(i));
                            break;
                    }
                }
            }
            //detail.setUrls(urls);
            onDetailAddListener.onAdd(detail);
            getOnNavigationUIListener().goBackFromCurrentPage();
        } else {
            String type = "18";
            String exhibit = getArguments().getString(EXHIBIT_ID);
            HttpPostUtil httpPostUtil = new HttpPostUtil();
            httpPostUtil.addTextParameter("type", type);
            httpPostUtil.addTextParameter("exhibit", exhibit);
            httpPostUtil.addTextParameter("detail", detailText);
            for (int i = 0; i < urls.size(); i++) {
                String image = "image" + (i + 1);
                httpPostUtil.addFileParameter(image, new File(urls.get(i)));
            }
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
