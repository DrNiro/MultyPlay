package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AccountCreatedFragment extends Fragment {


    private View view = null;
    private CallBackApproved callBackApproved;

    private Button accOkFrag_BTN_ok;

    public void setCallback(CallBackApproved callback) {
        this.callBackApproved = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.activity_account_created_fragment, container, false);
        }

        findViews(view);

        accOkFrag_BTN_ok.setOnClickListener(onClickOkLintener);

        return view;
    }

    private void findViews(View view) {
        accOkFrag_BTN_ok = view.findViewById(R.id.accOkFrag_BTN_ok);
    }

    private View.OnClickListener onClickOkLintener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callBackApproved.onOkClick();
        }
    };

}
