package com.example.multyplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

public class LangListAdapter extends RecyclerView.Adapter<LangListAdapter.ViewHolder> {
    private List<Language> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    LangListAdapter(Context context, List<Language> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.language_list_view_holder, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Language language = mData.get(position);

//        int county_image_id = 0;
//        switch (language.getLangName()) {
//            case Constants.HEBREW:
//                county_image_id = R.drawable.ic_israel_flag;
//                break;
//            case Constants.ENGLISH_AMERICAN:
//                county_image_id = R.drawable.ic_usa_flag;
//                break;
//            case Constants.ENGLISH_BRITISH:
//                county_image_id = R.drawable.ic_uk_flag;
//                break;
//            case Constants.DEUTSCH:
//                county_image_id = R.drawable.ic_german_flag;
//                break;
//            case Constants.ITALIAN:
//                county_image_id = R.drawable.ic_italy_flag;
//                break;
//            case Constants.ARABIC:
//                county_image_id = R.drawable.ic_suadi_arabia_flag;
//                break;
//            case Constants.ESPANOL:
//                county_image_id = R.drawable.flag_spanish;
//                break;
//            case Constants.RUSSIAN:
//                county_image_id = R.drawable.ic_russia_flag;
//                break;
//            default:
//                county_image_id = R.drawable.ic_q_mark;
//                break;
//        }
//        holder.languageImageView.setImageResource(county_image_id);


        holder.languageImageView.setImageResource(language.getFlag());
        holder.languageNameTextView.setText(String.valueOf(language.getLangName()));
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

        ViewHolder(View itemView) {
            super(itemView);
            languageImageView = itemView.findViewById(R.id.language_image);
            languageNameTextView = itemView.findViewById(R.id.language_name);
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
