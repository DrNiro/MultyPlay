package com.example.multyplay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class EditLanguagesDialog extends AppCompatDialogFragment {

    private LangPickerListAdapter langPickerAdapter;
    private RecyclerView editLanguage_RCL_chooseLangs;
    private ArrayList<Language> selectedLangs;

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
        View editLangauges = inflater.inflate(R.layout.layout_edit_languages_dialog, null);

        prefs = new MySharedPreferences(getActivity());
        jsAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
        myAccount = new Gson().fromJson(jsAccount, Account.class);



        createLanguagesRecycler(editLangauges);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(editLangauges)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ArrayList<Language> newLangList = new ArrayList<>();
                        HashMap<String, Boolean> map = langPickerAdapter.getMap();
                        for(Language language : LangsConstants.getAllLangsList()) {
                            if(map.get(language.getLangName())){
                                newLangList.add(language);
                            }
                        }
                        if(newLangList.size() == 0) {
                            newLangList.add(selectedLangs.get(0));
                            Toast.makeText(getActivity(), "Must have at least 1 language", Toast.LENGTH_SHORT).show();
                        }
                        myAccount.setLanguages(new LanguageList(newLangList));
                        jsAccount = new Gson().toJson(myAccount);
                        prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsAccount);
                        MyFirebase.setAccount(myAccount);

                        callBackApproved.onOkClick();
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    private void createLanguagesRecycler(View view) {
//        creating the language recyclerView
        selectedLangs = myAccount.getLanguages().getLanguages();

        editLanguage_RCL_chooseLangs = view.findViewById(R.id.editLanguage_RCL_chooseLangs);
        editLanguage_RCL_chooseLangs.setLayoutManager(new LinearLayoutManager(getActivity()));
        langPickerAdapter = new LangPickerListAdapter(getActivity(), LangsConstants.getAllLangsList());
        langPickerAdapter.setClickListener(myLangPickerAdapterClickListener);
        for(Language lang : selectedLangs) {
            langPickerAdapter.setMapKey(lang.getLangName(), true);
        }
        editLanguage_RCL_chooseLangs.setAdapter(langPickerAdapter);
    }

    private LangPickerListAdapter.ItemClickListener myLangPickerAdapterClickListener = new LangPickerListAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            boolean isPicked = langPickerAdapter.getMap().get(langPickerAdapter.getItem(position).getLangName());
            Language selLang = LangsConstants.getAllLangsList().get(LangsConstants.getAllLangsList().indexOf(langPickerAdapter.getItem(position)));
            langPickerAdapter.setMapKey(selLang.getLangName(), !isPicked);
            langPickerAdapter.notifyItemChanged(position);
            if(!isPicked) {
                selectedLangs.add(selLang);
            } else {
                selectedLangs.remove(selLang);
            }
        }
    };

}
