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
import com.chenyp.collaboration.adapter.viewholder.BaseViewHolder;
import com.chenyp.collaboration.model.TreeRecord;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.util.GlideCircleTransform;
import com.chenyp.collaboration.util.GsonUtil;

import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/10/30.
 */
public class CommunicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<TreeRecord> treeRecords;
    private LayoutInflater inflater;

    private onCommunicationClickListener onCommunicationClickListener;

    public void setOnCommunicationClickListener(CommunicationAdapter.onCommunicationClickListener onCommunicationClickListener) {
        this.onCommunicationClickListener = onCommunicationClickListener;
    }

    public interface onCommunicationClickListener {
        void onCommunicationClick(String json);
    }

    public CommunicationAdapter(Context context, List<TreeRecord> treeRecords) {
        this.context = context;
        this.treeRecords = treeRecords;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommunicationViewHolder(inflater.inflate(R.layout.rv_communication_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        CommunicationViewHolder holder = (CommunicationViewHolder) viewHolder;
        final TreeRecord record = treeRecords.get(position);

        //显示头像
        Glide.with(context)
                .load(record.getPhoto())
                .centerCrop()
                .thumbnail(0.1f)
                .transform(new GlideCircleTransform(context))
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.photo);
        holder.publisher.setText(record.getPublisher());
        holder.title.setText(record.getText());
        holder.uploadDate.setText(DateUtils.formatDateTime(context,
                record.getPdate(), DateUtils.FORMAT_SHOW_YEAR));
        holder.summary.setText(context.getString(R.string.text_summary_format, record.getSummary()));
        holder.count.setText(String.valueOf(record.getCount()));

        //Item点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onCommunicationClickListener != null) {
                    onCommunicationClickListener.onCommunicationClick(GsonUtil.toJson(record));
                }
                //((MainActivity) context).navigateToCommunicationShowPage(GsonUtil.toJson(record));
            }

        });
    }

    @Override
    public int getItemCount() {
        return treeRecords.size();
    }

    public List<TreeRecord> getTreeRecords() {
        return treeRecords;
    }

    public void setTreeRecords(List<TreeRecord> treeRecords) {
        this.treeRecords = treeRecords;
        notifyDataSetChanged();
    }

    public class CommunicationViewHolder extends BaseViewHolder {

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

        public CommunicationViewHolder(View itemView) {
            super(itemView);
        }

    }

}
