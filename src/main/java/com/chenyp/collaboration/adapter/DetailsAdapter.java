package com.chenyp.collaboration.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.layoutManager.FullyGridLayoutManager;
import com.chenyp.collaboration.adapter.viewholder.BaseViewHolder;
import com.chenyp.collaboration.model.Detail;
import com.chenyp.collaboration.model.json.ExhibitDetailJsonData;
import com.chenyp.collaboration.model.json.ExhibitJsonData;
import com.chenyp.collaboration.ui.activity.MainActivity;
import com.chenyp.collaboration.ui.fragment.achievement.details.AchievementDetailAddFragment;
import com.chenyp.collaboration.ui.fragment.achievement.details.AchievementDetailModifyFragment;
import com.chenyp.collaboration.util.GsonUtil;
import com.chenyp.collaboration.util.ValidateUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/10/26.
 */
public class DetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements AchievementDetailAddFragment.OnDetailAddListener, AchievementDetailModifyFragment.OnDetailModifyListener {

    public enum ITEM_TYPE {
        ADD,  //添加ITEM
        Detail, //详细信息ITEM
    }

    private Context context;
    private ExhibitDetailJsonData jsonData;
    private List<Detail> details;
    private LayoutInflater inflater;
    private boolean addMode;
    private boolean modifyMode;

    public DetailsAdapter(Context context, List<Detail> details) {
        this.context = context;
        this.details = details;
        this.inflater = LayoutInflater.from(context);
        this.addMode = false;
        this.modifyMode = false;
    }

    public DetailsAdapter(Context context, boolean addMode) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.details = new ArrayList<>();
        this.addMode = addMode;
        this.modifyMode = addMode;
    }

    public DetailsAdapter(Context context, ExhibitDetailJsonData jsonData) {
        this.context = context;
        this.jsonData = jsonData;
        this.inflater = LayoutInflater.from(context);
        if (!ValidateUtil.isValid(jsonData.getDetails())) {
            this.details = new ArrayList<>();
        } else {
            this.details = jsonData.getDetails();
        }
        this.addMode = false;
        this.modifyMode = false;
    }

    public DetailsAdapter(Context context, ExhibitDetailJsonData jsonData, boolean addMode) {
        this.context = context;
        this.jsonData = jsonData;
        this.inflater = LayoutInflater.from(context);
        if (!ValidateUtil.isValid(jsonData.getDetails())) {
            this.details = new ArrayList<>();
        } else {
            this.details = jsonData.getDetails();
        }
        this.addMode = addMode;
        this.modifyMode = addMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ADD.ordinal()) {
            return new AddDetailViewHolder(inflater.inflate(R.layout.rv_achievement_add_details_item, parent, false));
        } else {
            return new DetailViewHolder(inflater.inflate(R.layout.rv_achievement_details_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof AddDetailViewHolder) {
            AddDetailViewHolder holder = (AddDetailViewHolder) viewHolder;
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (jsonData == null) {
                        ((MainActivity) context).navigateToAchievementDetailAddPage(DetailsAdapter.this);
                    } else {
                        ((MainActivity) context).navigateToAchievementDetailAddPage(String.valueOf(jsonData.getExhibit()));
                    }
                }

            });
        } else if (viewHolder instanceof DetailViewHolder) {
            DetailViewHolder holder = (DetailViewHolder) viewHolder;

            final Detail detail = details.get(position);
            holder.detail.setText(context.getString(R.string.text_summary_format, detail.getDetail()));
            List<String> urls = new ArrayList<>();
            if (detail.getImage1() != null) {
                urls.add(detail.getImage1());
            }
            if (detail.getImage2() != null) {
                urls.add(detail.getImage2());
            }
            if (detail.getImage3() != null) {
                urls.add(detail.getImage3());
            }
            holder.rv.setLayoutManager(new FullyGridLayoutManager(context, 3));
            holder.rv.setAdapter(new ShowPhotosAdapter(context, urls));
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((MainActivity) context).
                            navigateToAchievementDetailShowPage(GsonUtil.toJson(jsonData), position, modifyMode);
                }

            });
        }

    }

    @Override
    public int getItemCount() {
        if (addMode) {
            return details.size() + 1;
        } else {
            return details.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (addMode && position == details.size()) {
            return ITEM_TYPE.ADD.ordinal();
        } else {
            return ITEM_TYPE.Detail.ordinal();
        }
    }

    public class DetailViewHolder extends BaseViewHolder {
        @Bind(R.id.id_tv_detail)
        public TextView detail;
        @Bind(R.id.id_recycler_view)
        public RecyclerView rv;

        public DetailViewHolder(View itemView) {
            super(itemView);
        }

    }

    public class AddDetailViewHolder extends BaseViewHolder {

        public AddDetailViewHolder(View itemView) {
            super(itemView);
        }

    }

    public void setModifyMode(boolean modifyMode) {
        this.modifyMode = modifyMode;
    }

    @Override
    public void onAdd(Detail detail) {
        details.add(detail);
        if (addMode) {
            notifyItemInserted(details.size());
        } else {
            notifyItemInserted(details.size() - 1);
        }
    }

    @Override
    public void onModify(int position, Detail detail) {
        details.set(position, detail);
        notifyItemChanged(position);
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
        notifyDataSetChanged();
    }

    public List<Detail> getDetails() {
        return details;
    }
}
