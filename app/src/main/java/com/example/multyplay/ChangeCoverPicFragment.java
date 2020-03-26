package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

public class ChangeCoverPicFragment extends Fragment {

    private View view = null;
    private CallBackPictureReady callBackCoverPicReady;

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button changeCover_BTN_choose_file;
    private Button changeCover_BTN_upload;
    private Button changeCover_BTN_apply;
    private ImageView changeCover_IMG_coverPicture;
    private RoundedImageView changeCover_RDV_profilePic;
    private ProgressBar changeCover_progress_bar;

    private Uri imageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private UploadTask uploadTask;

    private Account account;
    private String account_serial_num;
    private MySharedPreferences prefs;
    private Picture coverPic;

    public void setCallback(CallBackPictureReady callback) {
        this.callBackCoverPicReady = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.change_cover_pic_fragment, container, false);
        }

        findViews();

        prefs = new MySharedPreferences(view.getContext());
//        get current account's id to replace its profile picture in storage appropriately.
        account = new Gson().fromJson(prefs.getString(Constants.PREFS_KEY_ACCOUNT, ""), Account.class);
        account_serial_num = account.getId().getSerialNum() + ""; //prefs.getString(Constants.PREFS_KEY_ACCOUNT_SERIAL, "Test");

        storageReference = FirebaseStorage.getInstance().getReference("cover_pictures");
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts");

        changeCover_BTN_choose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        changeCover_BTN_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(view.getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadPic();
                }

            }
        });

        changeCover_BTN_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBackCoverPicReady.pictureReady(coverPic);
            }
        });

        initProfilePic();

        return view;
    }

    private void findViews() {
        changeCover_BTN_choose_file = view.findViewById(R.id.changeCover_BTN_choose_file);
        changeCover_BTN_upload = view.findViewById(R.id.changeCover_BTN_upload);
        changeCover_BTN_apply = view.findViewById(R.id.changeCover_BTN_apply);
        changeCover_IMG_coverPicture = view.findViewById(R.id.changeCover_IMG_coverPicture);
        changeCover_RDV_profilePic = view.findViewById(R.id.changeCover_RDV_profilePic);
        changeCover_progress_bar = view.findViewById(R.id.changeCover_progress_bar);
    }

    private void initProfilePic() {
        //        show current profile pic
        if(account.getProfilePic().getUrl() == null) {
            Glide.with(this)
                    .load(R.drawable.ic_empty_profile_pic)
                    .centerCrop()
                    .into(changeCover_RDV_profilePic);
        } else {
            Glide.with(this)
                    .load(account.getProfilePic().getUrl())
                    .centerCrop()
                    .into(changeCover_RDV_profilePic);
        }
        if(account.getCoverPic().getUrl() == null) {
//            Glide.with(this)
//                    .load(R.drawable.cover_placeholder)
//                    .centerCrop()
//                    .into(changeCover_IMG_coverPicture);
        } else {
            Glide.with(this)
                    .load(account.getCoverPic().getUrl())
                    .centerCrop()
                    .into(changeCover_IMG_coverPicture);
        }
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
                    .into(changeCover_IMG_coverPicture);
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
                        changeCover_progress_bar.setProgress((int)progress);
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
                                    changeCover_progress_bar.setProgress(0);
                                }
                            }, 1000);
                            Toast.makeText(view.getContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                            coverPic = new Picture(Constants.COVER_PIC, downloadURL);
                            MyFirebase.setCoverPic(account, coverPic);
                            changeCover_BTN_apply.setVisibility(View.VISIBLE);
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
