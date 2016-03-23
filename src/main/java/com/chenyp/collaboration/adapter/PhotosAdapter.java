package com.chenyp.collaboration.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.viewholder.BaseViewHolder;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.selectPhoto.BrowsePhotoFragment;
import com.chenyp.collaboration.util.ValidateUtil;
import com.rey.material.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/10/16.
 */
public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements BrowsePhotoFragment.OnModifyFinishListener {

    public enum ITEM_TYPE {
        PHOTO,  //图片ITEM
        CAMERA, //拍照ITEM
    }

    private OnCameraClickListener onCameraClickListener = null;

    private OnPhotoSelectListener onPhotoSelectListener = null;

    public void setOnCameraClickListener(OnCameraClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }

    public void setOnPhotoSelectListener(OnPhotoSelectListener onPhotoSelectListener) {
        this.onPhotoSelectListener = onPhotoSelectListener;
    }

    private boolean showCamera;

    private int maxCount;

    private LayoutInflater inflater;

    private Context context;

    private List<String> photos;

    private List<String> selectPhotos;

    public PhotosAdapter(Context context, List<String> photos) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        if (photos != null) {
            this.photos = photos;
        } else {
            this.photos = new ArrayList<>();
        }
        selectPhotos = new ArrayList<>();
    }

    public PhotosAdapter(boolean showCamera, int maxCount, Context context, List<String> photos) {
        this.showCamera = showCamera;
        this.maxCount = maxCount;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        if (ValidateUtil.isValid(photos)) {
            this.photos = photos;
        } else {
            this.photos = new ArrayList<>();
        }
        if (!ValidateUtil.isValid(selectPhotos)) {
            selectPhotos = new ArrayList<>();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.CAMERA.ordinal()) {
            return new CameraViewHolder(inflater.inflate(R.layout.rv_photos_item_camera, parent, false));
        } else {
            return new PhotoViewHolder(inflater.inflate(R.layout.rv_photos_item_photo, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof CameraViewHolder) {
            final CameraViewHolder holder = (CameraViewHolder) viewHolder;
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onCameraClickListener.onCameraClick();
                }

            });
        } else if (viewHolder instanceof PhotoViewHolder) {
            final PhotoViewHolder holder = (PhotoViewHolder) viewHolder;
            final String path;
            if (isShowCamera()) {
                path = photos.get(position - 1);
            } else {
                path = photos.get(position);
            }

            final boolean isChecked = selectPhotos.contains(path);
            float alpha = isChecked ? 0.5f : 1.0f;
            holder.cb.setChecked(isChecked);
            holder.iv.setSelected(isChecked);
            holder.iv.setAlpha(alpha);

            //显示图片
            Glide.with(context)
                    .load(path)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(holder.iv);

            holder.v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean isSelected = selectPhotos.contains(path);
                    if (!isSelected) {
                        if (selectPhotos.size() >= maxCount) {
                            Toast.makeText(context, context.getString(R.string.select_max_count, maxCount)
                                    , Toast.LENGTH_SHORT).show();
                            return;
                        }
                        addSelectPhoto(path);
                        Log.i(getClass().getName(),
                                "addSelectPhoto(path:" + path + ",position:" + position + ")");
                    } else {
                        removeSelectPhoto(path);
                        Log.i(getClass().getName(),
                                "removeSelectPhoto(path:" + path + ",position:" + position + ")");
                    }
                    if (onPhotoSelectListener != null) {
                        onPhotoSelectListener.onSelected(selectPhotos.size(), maxCount);
                    }
                    holder.cb.setChecked(!isChecked);
                    notifyItemChanged(position);
                }

            });

            holder.iv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((MainActivity) context).navigateToBrowsePhotoPage(
                            photos, selectPhotos, position - 1,
                            BrowsePhotoFragment.BROWSE_MODIFY, PhotosAdapter.this);
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        int position = photos == null ? 0 : photos.size();
        if (isShowCamera()) {
            return position + 1;
        }
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return (isShowCamera() && position == 0) ? ITEM_TYPE.CAMERA.ordinal() : ITEM_TYPE.PHOTO.ordinal();
    }

    public class PhotoViewHolder extends BaseViewHolder {

        @Bind(R.id.id_iv_photo)
        public ImageView iv;
        @Bind(R.id.id_cb_select)
        public CheckBox cb;
        @Bind(R.id.id_v_click)
        public View v;

        public PhotoViewHolder(View itemView) {
            super(itemView);
        }

    }

    public class CameraViewHolder extends BaseViewHolder {

        public CameraViewHolder(View itemView) {
            super(itemView);
        }

    }

    public interface OnCameraClickListener {

        void onCameraClick();

    }

    public interface OnPhotoSelectListener {
        void onSelected(int selectCount, int maxCount);
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    public void addSelectPhoto(String path) {
        selectPhotos.add(path);
    }

    public void removeSelectPhoto(String path) {
        selectPhotos.remove(path);
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public List<String> getSelectPhotos() {
        return selectPhotos;
    }

    @Override
    public void onFinish(List<String> selectPhotos) {
        this.selectPhotos = selectPhotos;
        notifyDataSetChanged();
        onPhotoSelectListener.onSelected(selectPhotos.size(), maxCount);
    }
}
