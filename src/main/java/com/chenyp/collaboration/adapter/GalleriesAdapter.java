package com.chenyp.collaboration.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.viewholder.BaseViewHolder;
import com.chenyp.collaboration.model.Gallery;
import com.chenyp.collaboration.util.ValidateUtil;

import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/10/17.
 */
public class GalleriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private Gallery selectGallery;

    private List<Gallery> galleries;

    private LayoutInflater inflater;

    private OnGallerySelectListener onGallerySelectListener;

    public interface OnGallerySelectListener {
        void onSelected(Gallery gallery);
    }

    public void setOnGallerySelectListener(OnGallerySelectListener onGallerySelectListener) {
        this.onGallerySelectListener = onGallerySelectListener;
    }

    public GalleriesAdapter(Context context, List<Gallery> galleries) {
        this.context = context;
        this.galleries = galleries;
        this.inflater = LayoutInflater.from(context);
        selectGallery = galleries.get(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GalleryViewHolder(inflater.inflate(R.layout.rv_galleries_item_gallery, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        GalleryViewHolder holder = (GalleryViewHolder) viewHolder;

        final Gallery gallery = galleries.get(position);

        if (ValidateUtil.isValid(gallery.getPhotoPaths())) {
            String path = gallery.getPhotoPaths().get(0);
            //显示图片
            Glide.with(context)
                    .load(path)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(holder.iv);
            holder.name.setText(gallery.getName());
            holder.count.setText(context.getString(
                    R.string.tv_text_gallery_photo_count_format, gallery.getPhotoPaths().size()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectGallery = gallery;
                    if (onGallerySelectListener != null) {
                        onGallerySelectListener.onSelected(gallery);
                    }
                }

            });
        }

    }

    @Override
    public int getItemCount() {
        return galleries == null ? 0 : galleries.size();
    }

    public class GalleryViewHolder extends BaseViewHolder {

        @Bind(R.id.id_iv_photo)
        public ImageView iv;
        @Bind(R.id.id_tv_gallery_name)
        public TextView name;
        @Bind(R.id.id_gallery_photo_count)
        public TextView count;

        public GalleryViewHolder(View itemView) {
            super(itemView);
        }

    }
}
