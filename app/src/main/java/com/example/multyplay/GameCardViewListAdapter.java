package com.example.multyplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class GameCardViewListAdapter extends RecyclerView.Adapter<GameCardViewListAdapter.ViewHolder> {
    private List<Game> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private HashMap<String, Boolean> gameSelectedMap;
    private void setMap() {
        this.gameSelectedMap = new HashMap<>();
        for(Game game : this.mData) {
            gameSelectedMap.put(game.getGameName(), false);
        }
    }
    public HashMap<String, Boolean> getMap() {
        return this.gameSelectedMap;
    }
    public void setMapKey(String gameName, boolean picker) {
        this.gameSelectedMap.put(gameName, picker);
    }

    // data is passed into the constructor
    GameCardViewListAdapter(Context context, List<Game> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        setMap();
    }

    // inflates the card layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cardview_item_game, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Game game = mData.get(position);

        holder.gameImageView.setImageResource(game.getThumbnail());
        holder.gameNameTextView.setText(game.getGameName());
        holder.gameCard_LAY_gameLayout.setBackgroundColor(mInflater.getContext().getResources().getColor(R.color.white));
        if(!this.gameSelectedMap.isEmpty()) {
            if(this.gameSelectedMap.get(game.getGameName())) {
//                holder.gameCard_CARD_gameCardView.setBackgroundColor(mInflater.getContext().getResources().getColor(R.color.blue_marker));
                holder.gameCard_CARD_gameCardView.setCardBackgroundColor(mInflater.getContext().getResources().getColor(R.color.blue_marker));
                holder.gameCard_CARD_gameCardView.setCardElevation(20);
            } else {
                holder.gameCard_CARD_gameCardView.setCardBackgroundColor(mInflater.getContext().getResources().getColor(R.color.white));
//                holder.gameCard_CARD_gameCardView.setBackgroundColor(mInflater.getContext().getResources().getColor(R.color.white));
                holder.gameCard_CARD_gameCardView.setCardElevation(9);
            }
        }
//        holder.gameCard_CARD_gameCardView.setBackgroundColor(mInflater.getContext().getResources().getColor(R.color.light_gray));
//        holder.gameCard_CARD_gameCardView.setCardElevation(20);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    //        CardView gameCardView;
        ImageView gameImageView;
        TextView gameNameTextView;
        RelativeLayout gameCard_LAY_gameLayout;
        CardView gameCard_CARD_gameCardView;
        ViewHolder(View itemView) {
            super(itemView);
            gameImageView = itemView.findViewById(R.id.gameCard_IMG_gamePic);
            gameNameTextView = itemView.findViewById(R.id.gameCard_TXT_gameName);
            gameCard_LAY_gameLayout = itemView.findViewById(R.id.gameCard_LAY_gameLayout);
            gameCard_CARD_gameCardView = itemView.findViewById(R.id.gameCard_CARD_gameCardView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Game getItem(int id) {
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
