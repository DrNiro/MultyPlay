package com.example.multyplay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class EditGamesDialog extends AppCompatDialogFragment {

    private RecyclerView gamesRecyclerView;
    private GameCardViewListAdapter gamesAdapter;
    private ArrayList<Game> selectedGames;

    private MySharedPreferences prefs;
    private String jsAccount;
    private Account myAccount;

    private CallBackApproved callBackApproved;

    public void setCallBackApproved(CallBackApproved callBackApproved) {
        this.callBackApproved = callBackApproved;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View editGames = inflater.inflate(R.layout.layout_edit_games_dialog, null);

        prefs = new MySharedPreferences(getActivity());
        jsAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
        myAccount = new Gson().fromJson(jsAccount, Account.class);

        createGamesRecycler(editGames);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(editGames)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ArrayList<Game> newGamesList = new ArrayList<>();
                        HashMap<String, Boolean> map = gamesAdapter.getMap();
                        for(Game game : GamesConstants.getAllGamesList()) {
                            if(map.get(game.getGameName())){
                                newGamesList.add(game);
                            }
                        }

                        myAccount.setGames(new GameList(newGamesList));
                        jsAccount = new Gson().toJson(myAccount);
                        prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsAccount);
                        MyFirebase.setAccount(myAccount);

                        callBackApproved.onOkClick();
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }
//
//    private void createLanguagesRecycler(View view) {
////        creating the language recyclerView
//        selectedLangs = myAccount.getLanguages().getLanguages();
//
//        editLanguage_RCL_chooseLangs = view.findViewById(R.id.editLanguage_RCL_chooseLangs);
//        editLanguage_RCL_chooseLangs.setLayoutManager(new LinearLayoutManager(getActivity()));
//        langPickerAdapter = new LangPickerListAdapter(getActivity(), LangsConstants.getAllGamesList());
//        langPickerAdapter.setClickListener(myLangPickerAdapterClickListener);
//        for(Language lang : selectedLangs) {
//            langPickerAdapter.setMapKey(lang.getLangName(), true);
//        }
//        editLanguage_RCL_chooseLangs.setAdapter(langPickerAdapter);
//    }

    private void createGamesRecycler(View view) {
        selectedGames = myAccount.getGames().getGames();

        gamesRecyclerView = view.findViewById(R.id.editGames_RCL_gamesGrid);
        gamesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        gamesAdapter = new GameCardViewListAdapter(getActivity(), GamesConstants.getAllGamesList());
        gamesAdapter.setClickListener(myGamesAdapterClickListener);
        for(Game game : selectedGames) {
            gamesAdapter.setMapKey(game.getGameName(), true);
        }
        gamesRecyclerView.setAdapter(gamesAdapter);
    }

    private GameCardViewListAdapter.ItemClickListener myGamesAdapterClickListener = new GameCardViewListAdapter.ItemClickListener() {
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
        }
    };

}
