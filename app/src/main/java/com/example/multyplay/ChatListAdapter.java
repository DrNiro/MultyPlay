package com.example.multyplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> implements Filterable {
    private List<Account> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private List<Account> mDataSearchFull;

    // data is passed into the constructor
    ChatListAdapter(Context context, List<Account> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        mDataSearchFull = new ArrayList<>(mData);
    }

    // inflates the card layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Account account = mData.get(position);

        holder.chatItem_TXT_Nickname.setText(account.getNickName());
        if(account.getProfilePic().getUrl() == null) {
            Glide.with(mInflater.getContext())
                    .load(R.drawable.ic_empty_profile_pic)
                    .centerCrop()
                    .into(holder.chatItem_RDV_profilePic);
        } else {
            Glide.with(mInflater.getContext())
                    .load(account.getProfilePic().getUrl())
                    .centerCrop()
                    .into(holder.chatItem_RDV_profilePic);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        return followingFilter;
    }

    private Filter followingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Account> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(mDataSearchFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Account acc : mDataSearchFull) {
                    if(acc.getNickName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(acc);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mData.clear();
            mData.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    //        CardView gameCardView;
        RoundedImageView chatItem_RDV_profilePic;
        TextView chatItem_TXT_Nickname;

        ViewHolder(View itemView) {
            super(itemView);
            chatItem_RDV_profilePic = itemView.findViewById(R.id.chatItem_RDV_profilePic);
            chatItem_TXT_Nickname = itemView.findViewById(R.id.chatItem_TXT_Nickname);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Account getItem(int id) {
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
