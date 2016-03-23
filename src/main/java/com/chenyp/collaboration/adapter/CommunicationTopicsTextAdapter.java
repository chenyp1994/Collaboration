package com.chenyp.collaboration.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.viewholder.BaseViewHolder;
import com.chenyp.collaboration.model.CommunicationTag;

import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/11/16.
 */
public class CommunicationTopicsTextAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CommunicationTag> tags;
    private Context context;
    private LayoutInflater inflater;

    public OnTopicsTextItemClickListener onTopicsTextItemClickListener;

    public void setOnTopicsTextItemClickListener(OnTopicsTextItemClickListener onTopicsTextItemClickListener) {
        this.onTopicsTextItemClickListener = onTopicsTextItemClickListener;
    }

    public interface OnTopicsTextItemClickListener {
        void onTopicsTextItemClick(String tag);
    }

    public CommunicationTopicsTextAdapter(Context context, @NonNull List<CommunicationTag> tags) {
        this.context = context;
        this.tags = tags;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TopicTextViewHolder(inflater.inflate(R.layout.rv_topics_text_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        TopicTextViewHolder holder = (TopicTextViewHolder) viewHolder;

        final CommunicationTag tag = tags.get(position);
        holder.tv.setText(tag.getTitle());
        if (position == tags.size() - 1) {
            holder.iv.setVisibility(View.GONE);
        } else {
            holder.iv.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (tags.size() > position + 1) {
                    onTopicsTextItemClickListener.onTopicsTextItemClick(tags.get(position + 1).getTag());
                } else {
                    onTopicsTextItemClickListener.onTopicsTextItemClick(null);
                }
            }

        });

    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class TopicTextViewHolder extends BaseViewHolder {

        @Bind(R.id.id_tv_topic_title)
        public TextView tv;
        @Bind(R.id.id_iv_topic_icon)
        public ImageView iv;

        public TopicTextViewHolder(View itemView) {
            super(itemView);
        }

    }
}
