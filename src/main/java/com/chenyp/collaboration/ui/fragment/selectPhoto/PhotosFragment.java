package com.chenyp.collaboration.ui.fragment.selectPhoto;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.PhotosAdapter;
import com.chenyp.collaboration.task.PhotosLoaderManager;
import com.chenyp.collaboration.ui.fragment.BaseFragment;

import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/10/16.
 */
public class PhotosFragment extends BaseFragment {

    public final String TAG = getClass().getSimpleName();

    @Bind(R.id.id_recycler_view)
    public RecyclerView rv;

    private PhotosAdapter photosAdapter;

    private Context context;

    public static PhotosFragment newInstance(Bundle bundle) {
        PhotosFragment fragment = new PhotosFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_photo;
    }

    @Override
    protected void initView() {
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(new GridLayoutManager(context, 3));
        rv.setAdapter(photosAdapter);
    }

    @Override
    protected void initData() {
        //数据加载
        getActivity().getSupportLoaderManager().
                initLoader(getId(), getArguments(), new PhotosLoaderManager(getContext()
                        , (SelectPhotoFragment) getParentFragment()));
    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void setPhotos(List<String> paths) {
        if (photosAdapter == null) {
            Bundle bundle = getArguments();
            photosAdapter = new PhotosAdapter(bundle.getBoolean(SelectPhotoFragment.EXTRA_SHOW_CAMERA),
                    bundle.getInt(SelectPhotoFragment.EXTRA_MAX_COUNT), context, paths);
            photosAdapter.setOnCameraClickListener((SelectPhotoFragment) getParentFragment());
            photosAdapter.setOnPhotoSelectListener((SelectPhotoFragment) getParentFragment());
        } else {
            photosAdapter.setPhotos(paths);
        }
        if (rv != null) {
            rv.setLayoutManager(new GridLayoutManager(context, 3));
            rv.setAdapter(photosAdapter);
        }
    }

    public List<String> getSelectPhotos() {
        return photosAdapter.getSelectPhotos();
    }

    public void addSelectPhoto(String path) {
        photosAdapter.addSelectPhoto(path);
    }
}
