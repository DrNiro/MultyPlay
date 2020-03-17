package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PickProfilePicsFragment extends Fragment {

    public static final int PAGE_NUMBER = 3;
    private View view = null;
    private CallBackProceed callBackProceed;
    private CallBackClickedBtn callBackClickedChangePicBtn;

    //    private CallBackClickedBtn clickedChangeProfilePicCallback;
    private CallBackClickedBtn clickedChangeCoverPicCallback;

    private TextView pickPics_BTN_finish;
    private RoundedImageView pickPics_RDV_profilePic;
    private RoundedImageView pickPics_RDV_changeprofilePic;
    private RoundedImageView pickPics_RDV_changeCoverPic;
    private ImageView pickPics_IMG_actualCoverPic;

    private Picture profilePic;
    private Picture coverPic;
    private String description;


    public void setCallback(CallBackProceed proceedCallback) {
        this.callBackProceed = proceedCallback;
    }

    public void setClickedChangePicCallback(CallBackClickedBtn callBackClickedBtn) {
        this.callBackClickedChangePicBtn = callBackClickedBtn;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_pick_profile_pics, container, false);
        }

        findViews();

        pickPics_BTN_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBackProceed.next(PAGE_NUMBER);
            }
        });

        pickPics_RDV_changeprofilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("vvvPickPics", "clicked change profile pic");
                callBackClickedChangePicBtn.buttonClicked(view);
            }
        });

        pickPics_RDV_changeCoverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("vvvPickPics", "clicked change cover pic");
                callBackClickedChangePicBtn.buttonClicked(view);
            }
        });

        initPics();

        return view;
    }

    private void findViews() {
        pickPics_RDV_profilePic = view.findViewById(R.id.pickPics_RDV_profilePic);
        pickPics_RDV_changeprofilePic = view.findViewById(R.id.pickPics_RDV_changeprofilePic);
        pickPics_IMG_actualCoverPic = view.findViewById(R.id.pickPics_IMG_actualCoverPic);
        pickPics_RDV_changeCoverPic = view.findViewById(R.id.pickPics_RDV_changeCoverPic);
        pickPics_BTN_finish = view.findViewById(R.id.pickPics_BTN_finish);
    }

    private void initPics() {
        Glide.with(this)
                .load(R.drawable.ic_empty_profile_pic)
                .centerCrop()
                .into(pickPics_RDV_profilePic);
//        Glide.with(this)
//                .load(R.drawable.cover_placeholder)
//                .centerCrop()
//                .into(pickPics_IMG_actualCoverPic);
    }

    public void setProfilePic(Picture profilePic) {
        Glide.with(this)
                .load(profilePic.getUrl())
                .centerCrop()
                .into(pickPics_RDV_profilePic);
        this.profilePic = profilePic;
    }

    public void setCoverPic(Picture coverPic) {
        Glide.with(this)
                .load(coverPic.getUrl())
                .centerCrop()
                .into(pickPics_IMG_actualCoverPic);
        this.coverPic = coverPic;
    }



}
