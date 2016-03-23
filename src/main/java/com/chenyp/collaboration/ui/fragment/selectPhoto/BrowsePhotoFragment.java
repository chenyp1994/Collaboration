package com.chenyp.collaboration.ui.fragment.selectPhoto;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.BrowsePhotoAdapter;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.ui.view.HackyViewPager;
import com.chenyp.collaboration.util.ValidateUtil;
import com.rey.material.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/11/3.
 */
public class BrowsePhotoFragment extends BaseFragment {

    @Bind(R.id.id_toolbar)
    public Toolbar tb;

    @Bind(R.id.id_vp_browse_photo)
    public HackyViewPager vp;

    public final String TAG = getClass().getSimpleName();

    public final static String BROWSE_PHOTOS = "browse_photos";

    public final static String BROWSE_SELECT_PHOTOS = "browse_select_photos";

    public final static String BROWSE_POSITION = "browse_position";

    public final static String BROWSE_MODE = "browse_mode";

    public final static int BROWSE_NORMAL = 100;

    public final static int BROWSE_DELETE = 101;

    public final static int BROWSE_MODIFY = 102;

    private BrowsePhotoAdapter browsePhotoAdapter;

    private Snackbar deleteSnackbar;

    private MenuItem modifyItem;

    private OnModifyFinishListener onModifyFinishListener;

    public interface OnModifyFinishListener {
        void onFinish(List<String> selectPhotos);
    }

    public void setOnModifyFinishListener(OnModifyFinishListener onModifyFinishListener) {
        this.onModifyFinishListener = onModifyFinishListener;
    }

    @Deprecated
    public static BrowsePhotoFragment newInstance(List<String> photos, int position) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(BROWSE_PHOTOS, (ArrayList<String>) photos);
        bundle.putInt(BROWSE_POSITION, position);
        BrowsePhotoFragment fragment = new BrowsePhotoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static BrowsePhotoFragment newInstance(List<String> photos, List<String> selectPhotos, int position, int modeCode) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(BROWSE_PHOTOS, (ArrayList<String>) photos);
        if (modeCode == BROWSE_MODIFY) {
            bundle.putStringArrayList(BROWSE_SELECT_PHOTOS, (ArrayList<String>) selectPhotos);
        }
        bundle.putInt(BROWSE_POSITION, position);
        bundle.putInt(BROWSE_MODE, modeCode);
        BrowsePhotoFragment fragment = new BrowsePhotoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_browse_photo;
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

        Bundle bundle = getArguments();
        List<String> urls = bundle.getStringArrayList(BROWSE_PHOTOS);
        urls = ValidateUtil.isValid(urls) ? urls : new ArrayList<String>();
        int position = bundle.getInt(BROWSE_POSITION);
        browsePhotoAdapter = new BrowsePhotoAdapter(getChildFragmentManager(), urls);
        vp.setAdapter(browsePhotoAdapter);
        vp.setCurrentItem(position);
        tb.setTitle(getContext().getString(R.string.text_browse_photo_format,
                position + 1, urls.size()));
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateToolbar(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    @SuppressWarnings("ConstantConditions")
    private void updateToolbar(int position) {
        Bundle bundle = getArguments();
        List<String> urls = bundle.getStringArrayList(BROWSE_PHOTOS);
        urls = ValidateUtil.isValid(urls) ? urls : new ArrayList<String>();
        tb.setTitle(getContext().getString(R.string.text_browse_photo_format,
                position + 1, urls.size()));
        if (bundle.getInt(BROWSE_MODE) == BROWSE_MODIFY) {
            List<String> selectUrls = bundle.getStringArrayList(BROWSE_SELECT_PHOTOS);
            selectUrls = ValidateUtil.isValid(urls) ? selectUrls : new ArrayList<String>();
            if (selectUrls.contains(urls.get(position))) {
                ((CheckBox) modifyItem.getActionView().
                        findViewById(R.id.id_cb_select)).setChecked(true);
            } else {
                ((CheckBox) modifyItem.getActionView().
                        findViewById(R.id.id_cb_select)).setChecked(false);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMenuVisibility(true);
        int mode = getArguments().getInt(BROWSE_MODE, BROWSE_NORMAL);
        switch (mode) {
            case BROWSE_DELETE:
                setHasOptionsMenu(true);
                break;
            case BROWSE_MODIFY:
                setHasOptionsMenu(true);
                break;
            case BROWSE_NORMAL:
                setHasOptionsMenu(false);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int mode = getArguments().getInt(BROWSE_MODE, BROWSE_NORMAL);
        switch (mode) {
            case BROWSE_DELETE:
                inflater.inflate(R.menu.menu_browse_photo, menu);
                menu.findItem(R.id.action_delete).setVisible(true);
                modifyItem = menu.findItem(R.id.action_select).setVisible(false);
                break;
            case BROWSE_MODIFY:
                inflater.inflate(R.menu.menu_browse_photo, menu);
                menu.findItem(R.id.action_delete).setVisible(false);
                modifyItem = menu.findItem(R.id.action_select).setVisible(true);
                modifyItem.setActionView(R.layout.menu_check_box);
                modifyItem.getActionView().findViewById(R.id.id_v_click)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CheckBox cb = (CheckBox) modifyItem.getActionView().findViewById(R.id.id_cb_select);
                                Bundle bundle = getArguments();
                                List<String> photos = bundle.getStringArrayList(BROWSE_PHOTOS);
                                List<String> selectPhotos = bundle.getStringArrayList(BROWSE_SELECT_PHOTOS);
                                photos = ValidateUtil.isValid(photos) ? photos : new ArrayList<String>();
                                selectPhotos = ValidateUtil.isValid(photos) ? selectPhotos : new ArrayList<String>();
                                int position = vp.getCurrentItem();
                                String path = photos.get(position);
                                //noinspection ConstantConditions
                                boolean isSelected = selectPhotos.contains(path);
                                if (!isSelected) {
                                    //noinspection ConstantConditions
                                    if (selectPhotos.size() >= 3) {
                                        Toast.makeText(getContext(), getContext().
                                                getString(R.string.select_max_count, 3), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    selectPhotos.add(path);
                                    Log.i(getClass().getName(),
                                            "addSelectPhoto(path:" + path + ",position:" + position + ")");
                                } else {
                                    //noinspection ConstantConditions
                                    selectPhotos.remove(photos.get(position));
                                    Log.i(getClass().getName(),
                                            "removeSelectPhoto(path:" + path + ",position:" + position + ")");
                                }
                                cb.setChecked(!isSelected);
                                bundle.putStringArrayList(BROWSE_SELECT_PHOTOS,
                                        (ArrayList<String>) selectPhotos);
                            }
                        });
                updateToolbar(getArguments().getInt(BROWSE_POSITION));
                break;
            case BROWSE_NORMAL:
                break;
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            final int position = vp.getCurrentItem();
            final List<String> urls = browsePhotoAdapter.getUrls();
            final String deletedPath = urls.get(position);

            deleteSnackbar = Snackbar.make(getLayoutView(), R.string.deleted_a_photo,
                    Snackbar.LENGTH_LONG);

            if (urls.size() <= 1) {

                // show confirm dialog
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.confirm_to_delete)
                        .setPositiveButton(R.string.btn_text_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                urls.remove(position);
                                getOnNavigationUIListener().goBackFromCurrentPage();
                            }
                        })
                        .setNegativeButton(R.string.btn_text_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

            } else {

                deleteSnackbar.show();

                urls.remove(position);

                browsePhotoAdapter.notifyDataSetChanged();
            }

            deleteSnackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == 3) {
                        vp.setCurrentItem(position - 1, true);
                    } else {
                        vp.setCurrentItem(position + 1, true);
                    }
                    if (urls.size() > 0) {
                        urls.add(position, deletedPath);
                    } else {
                        urls.add(deletedPath);
                    }
                    tb.setTitle(getContext().getString(R.string.text_browse_photo_format,
                            vp.getCurrentItem() + 1, urls.size()));
                    browsePhotoAdapter.notifyDataSetChanged();
                }
            });
            tb.setTitle(getContext().getString(R.string.text_browse_photo_format,
                    vp.getCurrentItem() + 1, urls.size()));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        if (deleteSnackbar != null) {
            deleteSnackbar.dismiss();
        }
        if (onModifyFinishListener != null) {
            onModifyFinishListener.onFinish(getArguments()
                    .getStringArrayList(BROWSE_SELECT_PHOTOS));
        }
        super.onDestroy();
    }

}
