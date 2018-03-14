package com.kang.findfilesopen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kang.findfilesopen.R;
import com.kang.findfilesopen.bean.File_M;

import java.util.ArrayList;
import java.util.List;



public class FileListAdapter extends RecyclerView.Adapter{

    private List<File_M> list = new ArrayList<File_M>();
    private Context mContext;
    private onItemClickListener mOnItemClickListener = null;
    public FileListAdapter(Context context, List<File_M> list) {
        this.mContext = context;
        this.list = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(mContext).inflate(R.layout.file_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        myViewHolder.file_list_item_name.setText(list.get(position).getFileTitle());
        if (list.get(position).isChecked()) {
            myViewHolder.file_list_item_checkImg.setImageResource(R.mipmap.checkbox_yes);
        } else {
            myViewHolder.file_list_item_checkImg.setImageResource(R.mipmap.checkbox_no);
        }

        GradientDrawable myDrawable = (GradientDrawable) myViewHolder.file_list_item_type.getBackground();
        myDrawable.setColor(Color.parseColor("#FF4081"));

        myViewHolder.file_list_item_type.setText(list.get(position).getFileType());

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

        public TextView file_list_item_type;
        public TextView file_list_item_name;
        public ImageView file_list_item_checkImg;
        public MyViewHolder(View v) {
            super(v);
            file_list_item_type = (TextView) v.findViewById(R.id.file_list_item_type);
            file_list_item_name = (TextView) v.findViewById(R.id.file_list_item_name);
            file_list_item_checkImg = (ImageView) v.findViewById(R.id.file_list_item_checkImg);
        }
    }


    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
