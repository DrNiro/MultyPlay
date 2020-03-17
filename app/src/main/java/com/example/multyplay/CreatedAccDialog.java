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

public class CreatedAccDialog extends AppCompatDialogFragment {

    private CallBackApproved callBackApproved;

    public void setCallBackApproved(CallBackApproved callBackApproved) {
        this.callBackApproved = callBackApproved;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View cutomProfileMsg = inflater.inflate(R.layout.layout_acc_created_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(cutomProfileMsg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callBackApproved.onOkClick();
            }
        });

        return builder.create();
    }
}
