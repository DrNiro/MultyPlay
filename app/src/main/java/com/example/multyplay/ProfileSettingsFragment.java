package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ProfileSettingsFragment extends Fragment {

    private View view = null;
    private CallBackClickedBtn callBackClickedBtn;

    private RelativeLayout settings_LAY_logout;

    private MySharedPreferences prefs;
    private Account myAccount;
    private String jsAccount;


    public void setCallback(CallBackClickedBtn callback) {
        this.callBackClickedBtn = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_profile_settings, container, false);
        }

        findViews();

        prefs = new MySharedPreferences(view.getContext());

        settings_LAY_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(view);
            }
        });

        return view;
    }

    private void logout(View logoutBtn) {
        prefs.putString(Constants.PREFS_KEY_CURRENT_LOGGED_IN, Constants.LOGGED_OUT);
        prefs.putString(Constants.PREFS_KEY_ACCOUNT, "");
        callBackClickedBtn.buttonClicked(logoutBtn);
    }

    private void findViews() {
        settings_LAY_logout = view.findViewById(R.id.settings_LAY_logout);
    }

}
