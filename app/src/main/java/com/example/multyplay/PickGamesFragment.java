package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PickGamesFragment extends Fragment {

    public static final int PAGE_NUMBER = 2;
    private View view = null;
    private CallBackProceed callBackProceed;

    private RecyclerView gamesRecyclerView;
    private GameCardViewListAdapter gamesAdapter;

    private TextView pickGames_BTN_next;

    private ArrayList<Game> selectedGames = new ArrayList<>();


    public void setCallback(CallBackProceed proceedCallback) {
        this.callBackProceed = proceedCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_pick_games, container, false);
        }

        findViews();

        createGamesRecycler();

        pickGames_BTN_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBackProceed.next(PAGE_NUMBER);
            }
        });

        return view;
    }

    private void findViews() {
        pickGames_BTN_next = view.findViewById(R.id.pickGames_BTN_next);
    }

    private void createGamesRecycler() {
        gamesRecyclerView = view.findViewById(R.id.pickGames_RCL_gamesGrid);
        gamesRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        gamesAdapter = new GameCardViewListAdapter(view.getContext(), GamesConstants.getAllGamesList());
        gamesAdapter.setClickListener(myGamesAdapterClickListener);
        gamesRecyclerView.setAdapter(gamesAdapter);
    }

    GameCardViewListAdapter.ItemClickListener myGamesAdapterClickListener = new GameCardViewListAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            boolean isPicked = gamesAdapter.getMap().get(gamesAdapter.getItem(position).getGameName());
            Game selGame = gamesAdapter.getItem(position);
            gamesAdapter.setMapKey(gamesAdapter.getItem(position).getGameName(), !isPicked);
            gamesAdapter.notifyItemChanged(position);
            if(!isPicked) {
                selectedGames.add(selGame);
            } else {
                selectedGames.remove(selGame);
            }
            Log.d("vvv", "clicked on: " + position + " game");
        }
    };

    public ArrayList<Game> getSelectedGames() {
        return selectedGames;
    }
}
