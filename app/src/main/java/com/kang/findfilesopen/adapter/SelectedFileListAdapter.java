package com.kang.findfilesopen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kang.findfilesopen.R;
import com.kang.findfilesopen.bean.File_M;

import java.util.ArrayList;
import java.util.List;


public class SelectedFileListAdapter extends RecyclerView.Adapter{

    private List<File_M> list = new ArrayList<File_M>();
    private Context mContext;
    private onItemClickListener mOnItemClickListener = null;
    public SelectedFileListAdapter(Context context, List<File_M> list) {
        this.mContext = context;
        this.list = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(mContext).inflate(R.layout.selected_file_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        myViewHolder.selected_file_list_item_address.setText(list.get(position).getFilePath());

        GradientDrawable myDrawable = (GradientDrawable) myViewHolder.selected_file_list_item_type.getBackground();
        myDrawable.setColor(Color.parseColor("#FF5081"));

        myViewHolder.selected_file_list_item_type.setText(list.get(position).getFileType());

        if (mOnItemClickListener!=null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // 返回数据总数
        return list == null ? 0 : list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView selected_file_list_item_type;
        public TextView selected_file_list_item_address;
        public MyViewHolder(View v) {
            super(v);
            selected_file_list_item_type = (TextView) v.findViewById(R.id.selected_file_list_item_type);
            selected_file_list_item_address = (TextView) v.findViewById(R.id.selected_file_list_item_address);
        }
    }


    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
