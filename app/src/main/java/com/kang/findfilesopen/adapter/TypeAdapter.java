package com.kang.findfilesopen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kang.findfilesopen.R;
import java.util.ArrayList;
import java.util.List;



public class TypeAdapter extends RecyclerView.Adapter{

    private List<String> list = new ArrayList<String>();
    private Context mContext;
    private onItemClickListener mOnItemClickListener = null;
    public TypeAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.list = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(mContext).inflate(R.layout.type_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        myViewHolder.type_list_item_name.setText(list.get(position));

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

        public TextView type_list_item_name;
        public MyViewHolder(View v) {
            super(v);
            type_list_item_name = (TextView) v.findViewById(R.id.type_list_item_name);
        }
    }


    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
