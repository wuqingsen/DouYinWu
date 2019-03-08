package com.example.qd.douyinwu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * author: wu
 * date: on 2018/5/3.
 * describe:评论
 */

public class CommitAdapter extends RecyclerView.Adapter<CommitAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    public interface OnItemClickListener {
        void onItemClick(int position, String Url);
    }

    public CommitAdapter.OnItemClickListener mOnItemClickListerer;

    public void setmOnItemClickListerer(CommitAdapter.OnItemClickListener listerer) {
        this.mOnItemClickListerer = listerer;
    }

    public CommitAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_video_commit, parent, false);
        CommitAdapter.MyViewHolder viewHolder = new CommitAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 15;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);

        }
    }
}
