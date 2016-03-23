package com.chenyp.collaboration.ui.fragment.selectPhoto;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.GalleriesAdapter;
import com.chenyp.collaboration.adapter.layoutManager.FullyLinearLayoutManager;
import com.chenyp.collaboration.model.Gallery;
import com.chenyp.collaboration.ui.fragment.BaseFragment;

import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/10/16.
 */
public class GalleriesFragment extends BaseFragment {

    @Bind(R.id.id_recycler_view)
    public RecyclerView rv;

    public final String TAG = getClass().getSimpleName();

    private GalleriesAdapter galleriesAdapter;

    private Context context;

    public static GalleriesFragment newInstance() {
        Bundle bundle = new Bundle();
        GalleriesFragment fragment = new GalleriesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_galleries;
    }

    @Override
    protected void initView() {
        rv.setLayoutManager(new FullyLinearLayoutManager(context));
        rv.setAdapter(galleriesAdapter);
    }

    @Override
    protected void initData() {

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

    public void setGalleries(List<Gallery> galleries, GalleriesAdapter.OnGallerySelectListener listener) {
        galleriesAdapter = new GalleriesAdapter(context, galleries);
        galleriesAdapter.setOnGallerySelectListener(listener);
        if (rv != null) {
            rv.setAdapter(galleriesAdapter);
        }
    }

}
