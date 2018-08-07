package com.gillben.hodgepodgecode.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gillben.hodgepodgecode.R;
import com.gillben.hodgepodgecode.view.SlideMenuPage;

import java.util.List;

public class SlidePageRecyclerViewAdapter extends RecyclerView.Adapter<SlidePageRecyclerViewAdapter.SlideMenuPageHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<String> dataList;
    private RecyclerItemCallback recyclerItemCallback;

    public SlidePageRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }


    @NonNull
    @Override
    public SlideMenuPageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.slide_page_layout, parent,false);
        return new SlideMenuPageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SlideMenuPageHolder holder, int position) {
        if (dataList != null) {
            holder.textView.setText(dataList.get(position));
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.slideMenuPage.smoothClose();
                }
            });
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    public void onClick(View v) {
        if (recyclerItemCallback != null) {
            recyclerItemCallback.clickItem(v, (Integer) v.getTag());
        }
    }


    static class SlideMenuPageHolder extends RecyclerView.ViewHolder {
        private SlideMenuPage slideMenuPage;
        private TextView textView;
        private Button button;

        private SlideMenuPageHolder(View itemView) {
            super(itemView);
            slideMenuPage = itemView.findViewById(R.id.slideMenuPage);
            textView = itemView.findViewById(R.id.mainContent);
            button = itemView.findViewById(R.id.menuBt);
        }
    }

    public void notifyData(List<String> list) {
        this.dataList = list;
        notifyDataSetChanged();
    }


    public void setRecyclerItemCallback(RecyclerItemCallback callback) {
        this.recyclerItemCallback = callback;
    }

    public interface RecyclerItemCallback {
        void clickItem(View view, int position);
    }
}
