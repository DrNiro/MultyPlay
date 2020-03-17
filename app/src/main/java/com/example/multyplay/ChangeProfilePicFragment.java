package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

public class ChangeProfilePicFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button BTN_choose_file;
    private Button BTN_upload;
    private Button changePPic_BTN_apply;
    private ImageView IMG_picture;
    private ProgressBar progress_bar;

    private Uri imageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private UploadTask uploadTask;

    private View view = null;
    private CallBackPictureReady callBackProfilePicReady;

    private Account account;
    private String account_serial_num;
    private MySharedPreferences prefs;

    private Picture profilePic;

//    private boolean isUploading = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.change_profile_pic_fragment, container, false);
        }

        findViews();

        changePPic_BTN_apply.setVisibility(View.INVISIBLE);

        prefs = new MySharedPreferences(view.getContext());
//        get current account's id to replace its profile picture in storage appropriately.
        account_serial_num = prefs.getString(Constants.PREFS_KEY_ACCOUNT_SERIAL, "Test");
        account = new Gson().fromJson(prefs.getString(Constants.PREFS_KEY_ACCOUNT, ""), Account.class);

        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts");

        BTN_choose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        BTN_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (uploadTask != null && uploadTask.isInProgress()) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(view.getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadPic();
                }

            }
        });

        changePPic_BTN_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBackProfilePicReady.pictureReady(profilePic);
            }
        });

        return view;
    }

    public void setCallback(CallBackPictureReady callback) {
        this.callBackProfilePicReady = callback;
    }

    private void findViews() {
//        profile_RDV_pic = view.findViewById(R.id.profile_RDV_pic);
        BTN_choose_file = view.findViewById(R.id.BTN_choose_file);
        BTN_upload = view.findViewById(R.id.BTN_upload);
        changePPic_BTN_apply = view.findViewById(R.id.changePPic_BTN_apply);
        IMG_picture = view.findViewById(R.id.IMG_picture);
        progress_bar = view.findViewById(R.id.progress_bar);
    }

    //    choose file from phone libs.
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //    show selected picture on display.sss
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .centerCrop()
                    .into(IMG_picture);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = view.getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadPic() {
        if (imageUri != null) {
            if (!account_serial_num.equals("")) {
                final StorageReference fileReference = storageReference.child(account_serial_num + "." + getFileExtension(imageUri));
                uploadTask = fileReference.putFile(imageUri);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progress_bar.setProgress((int)progress);
                    }
                }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return fileReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String downloadURL = downloadUri.toString();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progress_bar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(view.getContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                            profilePic = new Picture(Constants.PROFILE_PIC, downloadURL);
                            MyFirebase.setProfilePic(account, profilePic);
                            changePPic_BTN_apply.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(view.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                Toast.makeText(view.getContext(), "Cannot get account's id", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(view.getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
