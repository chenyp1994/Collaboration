package com.chenyp.collaboration.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.layoutManager.FullyGridLayoutManager;
import com.chenyp.collaboration.adapter.viewholder.BaseViewHolder;
import com.chenyp.collaboration.model.Detail;
import com.chenyp.collaboration.model.TreeRecord;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.util.GlideCircleTransform;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.ValidateUtil;

import java.sql.SQLPermission;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/10/26.
 */
public class AchievementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<TreeRecord> treeRecords;
    private boolean modifyMode;
    private LayoutInflater inflater;

    public AchievementAdapter(Context context, List<TreeRecord> treeRecords) {
        this.context = context;
        this.treeRecords = treeRecords;
        this.inflater = LayoutInflater.from(context);
        this.modifyMode = false;
    }

    public AchievementAdapter(Context context, List<TreeRecord> treeRecords, boolean modifyMode) {
        this.context = context;
        this.treeRecords = treeRecords;
        this.inflater = LayoutInflater.from(context);
        this.modifyMode = modifyMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AchievementViewHolder(inflater.inflate(R.layout.rv_achievement_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        AchievementViewHolder holder = (AchievementViewHolder) viewHolder;

        final TreeRecord treeRecord = treeRecords.get(position);

        //显示头像
        Glide.with(context)
                .load(treeRecord.getPhoto())
                .centerCrop()
                .thumbnail(0.1f)
                .transform(new GlideCircleTransform(context))
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.photo);

        holder.publisher.setText(treeRecord.getPublisher());
        holder.title.setText(treeRecord.getText());
        holder.uploadDate.setText(DateUtils.formatDateTime(context,
                treeRecord.getPdate(), DateUtils.FORMAT_SHOW_YEAR));
        holder.summary.setText(treeRecord.getSummary());
        holder.count.setText(String.valueOf(treeRecord.getCount()));

        List<String> urls = new ArrayList<>();
        if (ValidateUtil.isValid(treeRecord.getDetails())) {
            Detail detail = treeRecord.getDetails().get(treeRecord.getDetails().size() - 1);
            if (detail.getImage1() != null) {
                urls.add(detail.getImage1());
            }
            if (detail.getImage2() != null) {
                urls.add(detail.getImage2());
            }
            if (detail.getImage3() != null) {
                urls.add(detail.getImage3());
            }
        }
        holder.showPhotos.setLayoutManager(new FullyGridLayoutManager(context, 3));
        holder.showPhotos.setAdapter(new ShowPhotosAdapter(context, urls));

        //Item点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (modifyMode) {
                    //((MainActivity) context).navigateToAchievementShowPage(String.valueOf(achievement.getId()), modifyMode);
                    ((MainActivity) context).navigateToAchievementShowPage(GsonUtil.toJson(treeRecord), modifyMode);
                } else {
                    //((MainActivity) context).navigateToAchievementShowPage(String.valueOf(achievement.getId()));
                    ((MainActivity) context).navigateToAchievementShowPage(GsonUtil.toJson(treeRecord));
                }
            }

        });
    }

    public List<TreeRecord> getTreeRecords() {
        return treeRecords;
    }

    public void setTreeRecords(List<TreeRecord> treeRecords) {
        this.treeRecords = treeRecords;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return treeRecords.size();
    }

    public class AchievementViewHolder extends BaseViewHolder {

        @Bind(R.id.id_tv_publisher)
        public TextView publisher;
        @Bind(R.id.id_tv_title)
        public TextView title;
        @Bind(R.id.id_tv_upload_date)
        public TextView uploadDate;
        @Bind(R.id.id_tv_summary)
        public TextView summary;
        @Bind(R.id.id_iv_photo)
        public ImageView photo;
        @Bind(R.id.id_tv_read_count)
        public TextView count;
        @Bind(R.id.id_recycler_view)
        public RecyclerView showPhotos;

        public AchievementViewHolder(View itemView) {
            super(itemView);
        }

    }

}
