package com.chenyp.collaboration.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.viewholder.BaseViewHolder;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.selectPhoto.BrowsePhotoFragment;
import com.chenyp.collaboration.ui.fragment.selectPhoto.SelectPhotoFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by change on 2015/10/26.
 */
public class ShowPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SelectPhotoFragment.OnSelectPhotoFinishListener {

    private List<String> urls;
    private Context context;
    private LayoutInflater inflater;
    private boolean addMode;

    public ShowPhotosAdapter(Context context, List<String> urls) {
        this.urls = urls;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        addMode = false;
    }

    public ShowPhotosAdapter(Context context, List<String> urls, boolean addMode) {
        this.urls = urls;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.addMode = addMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShowPhotoViewHolder(inflater.inflate(R.layout.rv_achievement_show_photos_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (position > 2) {
            viewHolder.itemView.setVisibility(View.GONE);
            viewHolder.itemView.setSystemUiVisibility(View.GONE);
            return;
        }
        ShowPhotoViewHolder holder = (ShowPhotoViewHolder) viewHolder;
        if (position == urls.size()) {
            holder.iv.setVisibility(View.GONE);
            holder.add.setVisibility(View.VISIBLE);
            holder.add.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int maxCount = 3 - urls.size();
                    ((MainActivity) context).navigateToSelectPhotoPage(maxCount, true, ShowPhotosAdapter.this);
                }

            });
            return;
        }

        holder.add.setVisibility(View.GONE);
        holder.iv.setVisibility(View.VISIBLE);
        String path = urls.get(position);
        //显示图片
        Glide.with(context)
                .load(path)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.iv);

        holder.iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int modeCode;
                if (addMode) {
                    modeCode = BrowsePhotoFragment.BROWSE_DELETE;
                } else {
                    modeCode = BrowsePhotoFragment.BROWSE_NORMAL;
                }
                ((MainActivity) context).navigateToBrowsePhotoPage(urls, null, position, modeCode, null);
            }

        });
    }

    @Override
    public int getItemCount() {
        if (addMode) {
            return urls.size() + 1;
        }
        return urls.size();
    }

    public List<String> getUrls() {
        return urls;
    }

    @Override
    public void onFinish(List<String> urls) {
        for (String url : urls) {
            this.urls.add(url);
        }
        notifyDataSetChanged();
    }

    public class ShowPhotoViewHolder extends BaseViewHolder {

        @Bind(R.id.id_iv_photo)
        public ImageView iv;
        @Bind(R.id.id_iv_add)
        public ImageView add;

        public ShowPhotoViewHolder(View itemView) {
            super(itemView);
        }
    }


}
