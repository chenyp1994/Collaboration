package com.chenyp.collaboration.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
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
 * Created by change on 2015/11/9.
 */
public class CommunicationTopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TreeRecord> recordList;
    private Context context;
    private LayoutInflater inflater;

    public OnTopicsItemClickListener onTopicsItemClickListener;

    public void setOnTopicsItemClickListener(OnTopicsItemClickListener onTopicsItemClickListener) {
        this.onTopicsItemClickListener = onTopicsItemClickListener;
    }

    public interface OnTopicsItemClickListener {
        void OnTopicsItemClick(TreeRecord record);
    }

    public CommunicationTopicAdapter(Context context, @NonNull List<TreeRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TopicViewHolder(inflater.inflate(R.layout.rv_communication_topic_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        TopicViewHolder holder = (TopicViewHolder) viewHolder;

        final TreeRecord record = recordList.get(position);

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //打开子主题内容 传递json
                if (onTopicsItemClickListener != null) {
                    onTopicsItemClickListener.OnTopicsItemClick(record);
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public class TopicViewHolder extends BaseViewHolder {

        @Bind(R.id.id_tv_publisher)
        public TextView publisher;
        @Bind(R.id.id_tv_topic_title)
        public TextView title;
        @Bind(R.id.id_tv_upload_date)
        public TextView uploadDate;
        @Bind(R.id.id_tv_summary)
        public TextView summary;
        @Bind(R.id.id_iv_photo)
        public ImageView photo;

        public TopicViewHolder(View itemView) {
            super(itemView);
        }
    }


}
