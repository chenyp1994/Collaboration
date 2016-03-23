package com.chenyp.collaboration.ui.fragment.selectPhoto;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.chenyp.collaboration.R;
import com.chenyp.collaboration.ui.fragment.BaseFragment;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by change on 2015/10/17.
 */
public class PhotoFragment extends BaseFragment {

    @Bind(R.id.id_iv_photo)
    public PhotoView iv;

    public final String TAG = getClass().getSimpleName();

    public static final String IMAGE_URL = "image_url";

    public static PhotoFragment newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(IMAGE_URL, url);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.browse_photo_item;
    }

    @Override
    protected void initView() {
        //显示图片
        Glide.with(getContext())
                .load(getArguments().getString(IMAGE_URL))
                .placeholder(R.drawable.photo_placeholder)
                .error(R.drawable.photo_error)
                .thumbnail(0.1f)
                .fitCenter()
                .into(iv);
    }

    @Override
    protected void initData() {

    }

    @Override
    public String getTransactionTag() {
        return TAG;
    }

}
