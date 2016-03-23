package com.chenyp.collaboration.ui.fragment.selectPhoto;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.GalleriesAdapter;
import com.chenyp.collaboration.adapter.PhotosAdapter;
import com.chenyp.collaboration.adapter.TabPagerAdapter;
import com.chenyp.collaboration.model.Gallery;
import com.chenyp.collaboration.task.PhotosLoaderManager;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.BaseFragment;
import com.chenyp.collaboration.util.SDcardUtil;
import com.chenyp.collaboration.util.ValidateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/10/15.
 */
public class SelectPhotoFragment extends BaseFragment
        implements PhotosLoaderManager.OnPhotosLoadFinished,
        GalleriesAdapter.OnGallerySelectListener,
        PhotosAdapter.OnCameraClickListener,
        PhotosAdapter.OnPhotoSelectListener {

    @Bind(R.id.id_toolbar)
    public Toolbar tb;
    @Bind(R.id.id_fragment_tabLy)
    public TabLayout tbl;
    @Bind(R.id.id_fragment_vp)
    public ViewPager vp;

    public final String TAG = getClass().getSimpleName();

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static int CAMERA = 100;

    private String takePicPath;

    private MenuItem done;

    private List<Fragment> fragments = new ArrayList<>();

    public String[] tabs;

    private PhotosFragment photosFragment;

    private GalleriesFragment galleriesFragment;

    private TabPagerAdapter tabPagerAdapter;

    private OnSelectPhotoFinishListener onSelectPhotoFinishListener;

    public interface OnSelectPhotoFinishListener {
        void onFinish(List<String> urls);
    }

    public void setOnSelectPhotoFinishListener(OnSelectPhotoFinishListener onSelectPhotoFinishListener) {
        this.onSelectPhotoFinishListener = onSelectPhotoFinishListener;
    }

    public static SelectPhotoFragment newInstance(int maxCount, boolean showCamera) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_MAX_COUNT, maxCount);
        bundle.putBoolean(EXTRA_SHOW_CAMERA, showCamera);
        SelectPhotoFragment fragment = new SelectPhotoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_photo_select;
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
        tb.setTitle("图片选择");

        if (!ValidateUtil.isValid(fragments)) {
            setTabFragment();
            tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager(), fragments, tabs, getContext());
        }

        vp.setAdapter(tabPagerAdapter);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        tbl.setupWithViewPager(vp);
        tbl.setTabsFromPagerAdapter(tabPagerAdapter);

        tbl.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
    }

    private void setTabFragment() {
        photosFragment = PhotosFragment.newInstance(getArguments());
        galleriesFragment = GalleriesFragment.newInstance();
        photosFragment.onAttach(getContext());
        galleriesFragment.onAttach(getContext());

        fragments.add(photosFragment);
        fragments.add(galleriesFragment);
    }

    @Override
    protected void initData() {

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
        super.onAttach(context);
        tabs = context.getResources().getStringArray(R.array.activity_select_photo_tab_name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_photos, menu);
        done = menu.findItem(R.id.action_done);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            if (onSelectPhotoFinishListener != null) {
                onSelectPhotoFinishListener.onFinish(photosFragment.getSelectPhotos());
            }
            getOnNavigationUIListener().goBackFromCurrentPage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadFinished(List<Gallery> galleries) {
        photosFragment.setPhotos(galleries.get(0).getPhotoPaths());
        galleriesFragment.setGalleries(galleries, this);
    }

    @Override
    public void onSelected(Gallery gallery) {
        vp.setCurrentItem(0);
        photosFragment.setPhotos(gallery.getPhotoPaths());
    }

    @Override
    public void onCameraClick() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (!SDcardUtil.hasExternalStorage()) {
            Log.i(this.getClass().getName(), "SD card is not avaiable/writeable right now.");
            return;
        }
        takePicPath = Environment.getExternalStorageDirectory() + "/collaboration/camera/"
                + DateUtils.formatDateTime(getContext(),
                System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_ALL) + ".jpg";
        File file = new File(takePicPath);
        // 文件夹不存在则创建
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
        }
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(i, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    //保存到数据库便于搜索出来
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, takePicPath);
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    photosFragment.addSelectPhoto(takePicPath);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    File file = new File(takePicPath);
                    if (file.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }
                    takePicPath = "";
                }
                break;
        }


    }

    @Override
    public void onSelected(int selectCount, int maxCount) {
        if (selectCount == 0) {
            done.setEnabled(false);
            done.setTitle(getContext().getString(R.string.action_done));
        } else {
            done.setEnabled(true);
            done.setTitle(getContext().getString(R.string.done_with_count_format, selectCount, maxCount));
        }
    }

}
