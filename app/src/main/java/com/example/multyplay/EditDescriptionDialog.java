package com.example.multyplay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class EditDescriptionDialog extends AppCompatDialogFragment {

    private CallBackEditStringReady callBackEditStringReady;

    public void setCallBackApproved(CallBackEditStringReady callBackEditStringReady) {
        this.callBackEditStringReady = callBackEditStringReady;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View doneSetProfileMsg = inflater.inflate(R.layout.layout_edit_describe_dialog, null);



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(doneSetProfileMsg)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText editDescription_EDT_description = getDialog().findViewById(R.id.editDescription_EDT_description);
                        String editedDescription = editDescription_EDT_description.getText().toString().trim();
                        callBackEditStringReady.stringReady(editedDescription);
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

}
