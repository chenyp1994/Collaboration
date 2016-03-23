package com.chenyp.collaboration.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenyp.collaboration.R;
import com.chenyp.collaboration.adapter.viewholder.BaseViewHolder;
import com.chenyp.collaboration.model.DocInfo;
import com.chenyp.collaboration.ui.activity.BaseActivity;
import com.chenyp.collaboration.util.GlideCircleTransform;
import com.chenyp.collaboration.util.HttpPostUtil;
import com.chenyp.collaboration.util.SDcardUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.Bind;

/**
 * Created by change on 2015/11/27.
 */
public class CommunicationFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private List<DocInfo> docList;

    private LayoutInflater inflater;

    public CommunicationFileAdapter(Context context, List<DocInfo> docList) {
        this.context = context;
        this.docList = docList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileViewHolder(inflater.inflate(R.layout.rv_communication_file_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        FileViewHolder holder = (FileViewHolder) viewHolder;
        final DocInfo docInfo = docList.get(position);
        holder.publisher.setText(docInfo.getPublisher());
        holder.title.setText(docInfo.getTitle());
        holder.count.setText(String.valueOf(docInfo.getCount()));
        holder.date.setText(DateUtils.formatDateTime(context,
                docInfo.getPdate(), DateUtils.FORMAT_SHOW_YEAR));
        //显示头像
        Glide.with(context)
                .load(docInfo.getPhoto())
                .centerCrop()
                .thumbnail(0.1f)
                .transform(new GlideCircleTransform(context))
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.iv);
        holder.summary.setText(docInfo.getSummary());
        holder.download.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //下载文件操作
                HttpPostUtil postUtil = new HttpPostUtil();
                postUtil.addTextParameter("type", "10");
                postUtil.addTextParameter("doc", String.valueOf(docInfo.getId()));
                postUtil.sendHttpRequest(BaseActivity.URL, new HttpPostUtil.HttpCallbackListener() {

                    @Override
                    public void onSuccess(byte[] outputStream) {
                        //文件流
                        if (!SDcardUtil.hasExternalStorage()) {
                            Log.i(this.getClass().getName(), "SD card is not avaiable/writeable right now.");
                            return;
                        }
                        String docPath = Environment.getExternalStorageDirectory() + "/collaboration/download/doc/"
                                + docInfo.getTitle() + "." + docInfo.getType();
                        File file = new File(docPath);
                        // 文件夹不存在则创建
                        if (!file.exists()) {
                            //noinspection ResultOfMethodCallIgnored
                            file.getParentFile().mkdirs();
                        }
                        try {
                            FileOutputStream fos = new FileOutputStream(docPath);
                            fos.write(outputStream);
                            fos.flush();
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }

        });
    }

    @Override
    public int getItemCount() {
        return docList.size();
    }

    public class FileViewHolder extends BaseViewHolder {

        @Bind(R.id.id_tv_publisher)
        public TextView publisher;
        @Bind(R.id.id_tv_title)
        public TextView title;
        @Bind(R.id.id_tv_upload_count)
        public TextView count;
        @Bind(R.id.id_tv_upload_date)
        public TextView date;
        @Bind(R.id.id_tv_summary)
        public TextView summary;
        @Bind(R.id.id_btn_download)
        public Button download;
        @Bind(R.id.id_iv_photo)
        public ImageView iv;

        public FileViewHolder(View itemView) {
            super(itemView);
        }

    }
}
