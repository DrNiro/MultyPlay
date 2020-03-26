package com.example.multyplay;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class ProfileSearchResAdapter extends RecyclerView.Adapter<ProfileSearchResAdapter.ViewHolder> {
    private List<Account> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private double negVal = -100.0;
    private HashMap<String, Double> disntanceFromCallerMap;
    private void setMap() {
        this.disntanceFromCallerMap = new HashMap<>();
        for(Account account : this.mData) {
            disntanceFromCallerMap.put(account.getEmail().toString(), negVal);
        }
    }
    public void setMap(HashMap<String, Double> accountsDistanceMap) {
        this.disntanceFromCallerMap = accountsDistanceMap;
    }
    public HashMap<String, Double> getMap() {
        return this.disntanceFromCallerMap;
    }
    public void setMapKey(String accEmail, double distance) {
        this.disntanceFromCallerMap.put(accEmail, distance);
    }

    // data is passed into the constructor
    ProfileSearchResAdapter(Context context, List<Account> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.profile_search_result_holder, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Account account = mData.get(position);

        if(account.isOnline()) {
            holder.searchRes_IMG_onlineState.setImageResource(R.drawable.ic_green_dot);
        } else {
            holder.searchRes_IMG_onlineState.setImageResource(R.drawable.ic_red_dot);
        }
        if(account.getDescription() != null) {
            if(!account.getDescription().trim().equals("")) {
                holder.searchRes_TXT_description.setText(account.getDescription());
            } else {
                holder.searchRes_TXT_description.setText(" ");
            }
        }

        holder.searchRes_TXT_userNickName.setText(account.getNickName());
        if(account.getProfilePic().getUrl() == null) {
            Glide.with(mInflater.getContext())
                    .load(R.drawable.ic_empty_profile_pic)
                    .centerCrop()
                    .into(holder.searchRes_RDV_pic);
        } else {
            Glide.with(mInflater.getContext())
                    .load(account.getProfilePic().getUrl())
                    .centerCrop()
                    .into(holder.searchRes_RDV_pic);
        }

        if(!this.disntanceFromCallerMap.isEmpty()) {
            double distance = this.disntanceFromCallerMap.get(account.getEmail().toString());
            if(distance > negVal) {
                if(distance < 1) {
                    holder.searchRes_TXT_distance.setText("Less than 1 km away");
                } else if(distance > 9999) {
                    holder.searchRes_TXT_distance.setText("More than 10,000 km away");
                } else {
                    String distanceShort = new DecimalFormat("####.#").format(distance);
                    holder.searchRes_TXT_distance.setText(distanceShort + " km away");
                }
            } else {
                holder.searchRes_TXT_distance.setText("Location unknown");
            }
        }

        if(account.getGames() != null) {
            int numOfGames = account.getGames().Size();
            if(numOfGames >= 3) {
                holder.searchRes_IMG_game1.setImageResource(account.getGames().getGames().get(0).getThumbnail());
                holder.searchRes_IMG_game2.setImageResource(account.getGames().getGames().get(1).getThumbnail());
                holder.searchRes_IMG_game3.setImageResource(account.getGames().getGames().get(2).getThumbnail());
                if(numOfGames > 3) {
                    holder.searchRes_TXT_plusSign.setVisibility(View.VISIBLE);
                }
            } else if(numOfGames == 2) {
                holder.searchRes_IMG_game1.setImageResource(account.getGames().getGames().get(0).getThumbnail());
                holder.searchRes_IMG_game2.setImageResource(account.getGames().getGames().get(1).getThumbnail());
                holder.searchRes_IMG_game3.setVisibility(View.GONE);
            } else if(numOfGames == 1) {
                holder.searchRes_IMG_game1.setImageResource(account.getGames().getGames().get(0).getThumbnail());
                holder.searchRes_IMG_game2.setVisibility(View.GONE);
                holder.searchRes_IMG_game3.setVisibility(View.GONE);
            } else {
                holder.searchRes_IMG_game1.setVisibility(View.GONE);
                holder.searchRes_IMG_game2.setVisibility(View.GONE);
                holder.searchRes_IMG_game3.setVisibility(View.GONE);
            }
        }

    }

    private View.OnClickListener addFriendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("vvvAddFriendListener", "Add friend clicked.");
        }
    };

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView searchRes_TXT_userNickName;
        RoundedImageView searchRes_RDV_pic;
        TextView searchRes_TXT_distance;
        ImageView searchRes_IMG_game1;
        ImageView searchRes_IMG_game2;
        ImageView searchRes_IMG_game3;
        ImageView searchRes_IMG_onlineState;
        TextView searchRes_TXT_description;
        TextView searchRes_TXT_plusSign;

        ViewHolder(View itemView) {
            super(itemView);
            searchRes_TXT_userNickName = itemView.findViewById(R.id.searchRes_TXT_userNickName);
            searchRes_RDV_pic = itemView.findViewById(R.id.searchRes_RDV_pic);
            searchRes_TXT_distance = itemView.findViewById(R.id.searchRes_TXT_distance);
            searchRes_IMG_game1 = itemView.findViewById(R.id.searchRes_IMG_game1);
            searchRes_IMG_game2 = itemView.findViewById(R.id.searchRes_IMG_game2);
            searchRes_IMG_game3 = itemView.findViewById(R.id.searchRes_IMG_game3);
            searchRes_IMG_onlineState = itemView.findViewById(R.id.searchRes_IMG_onlineState);
            searchRes_TXT_description = itemView.findViewById(R.id.searchRes_TXT_description);
            searchRes_TXT_plusSign = itemView.findViewById(R.id.searchRes_TXT_plusSign);
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
