package com.example.qd.douyinwu.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qd.douyinwu.MainActivity;
import com.example.qd.douyinwu.R;

import java.util.List;

import cn.jzvd.JZVideoPlayerStandard;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private MainActivity mContext;
    private List<String> mDatas;

    //为RecyclerView的Item添加监听
    public interface OnItemClickListener {
        void onItemClick(int position, String type, View view, View view1, View view2);
    }

    public VideoAdapter.OnItemClickListener mOnItemClickListerer;

    public void setOnItemClickListerer(VideoAdapter.OnItemClickListener listerer) {
        this.mOnItemClickListerer = listerer;
    }

    public VideoAdapter(MainActivity context, List<String> datas) {
        mContext = context;
        mDatas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_play_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.jzVideo.setUp(String.valueOf(mDatas.get(position)), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
        //隐藏全屏按钮、返回按钮
        holder.jzVideo.fullscreenButton.setVisibility(View.GONE);
        holder.jzVideo.backButton.setVisibility(View.GONE);
        //返回
        holder.ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListerer.onItemClick(position, "back", view, view, view);
            }
        });
        //评论
        holder.iv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListerer.onItemClick(position, "commit", view, view, view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public JZVideoPlayerStandard jzVideo;
        LinearLayout ll_back;
        ImageView iv_commit;

        public ViewHolder(View itemView) {
            super(itemView);
            ll_back = itemView.findViewById(R.id.ll_back);
            iv_commit = itemView.findViewById(R.id.iv_commit);
            jzVideo = itemView.findViewById(R.id.jzVideo);
        }
    }
}
