package com.example.multyplay;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LangPickerListAdapter extends RecyclerView.Adapter<LangPickerListAdapter.ViewHolder> {
    private List<Language> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private HashMap<String, Boolean> langShownMap;
    private void setMap() {
        this.langShownMap = new HashMap<>();
        for(Language lang : this.mData) {
            langShownMap.put(lang.getLangName(), false);
        }
    }
    public HashMap<String, Boolean> getMap() {
        return this.langShownMap;
    }

    public void setMapKey(String langName, boolean picker) {
        this.langShownMap.put(langName, picker);
    }

    // data is passed into the constructor
    LangPickerListAdapter(Context context, List<Language> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        setMap();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.language_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Language language = mData.get(position);

        holder.languageImageView.setImageResource(language.getFlag());
        holder.languageNameTextView.setText(String.valueOf(language.getLangName()));
        if(!this.langShownMap.get(language.getLangName())) {
            holder.langItem_IMG_checkMark.setVisibility(View.INVISIBLE);
        } else {
            holder.langItem_IMG_checkMark.setVisibility(View.VISIBLE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView languageImageView;
        TextView languageNameTextView;
        RelativeLayout langItem_LAY_layout;
        ImageView langItem_IMG_checkMark;

        ViewHolder(View itemView) {
            super(itemView);
            languageImageView = itemView.findViewById(R.id.langItem_IMG_langPic);
            languageNameTextView = itemView.findViewById(R.id.langItem_TXT_langName);
            langItem_LAY_layout = itemView.findViewById(R.id.langItem_LAY_layout);
            langItem_IMG_checkMark = itemView.findViewById(R.id.langItem_IMG_checkMark);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Language getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
