package com.example.multyplay;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PickLanguageFragment extends Fragment {

    public static final int PAGE_NUMBER = 1;
    private View view = null;
    private CallBackProceed callBackProceed;

    private EditText initProfile_EDT_nickname;
    private EditText initProfile_EDT_age;
    private EditText initProfile_EDT_description;
    private Switch initProfile_SWC_showLocation;
    private TextView initProfile_BTN_next;
    private ImageView initProfile_IMG_editLangs;
    private int numOfSelectedLangs;
    private boolean hasEnteredNickname = false;
    private TextView initProfile_TXT_locationDetected;

    private RecyclerView langListRecyclerView;
    private LangPickerListAdapter langPickerAdapter;
    private RecyclerView initProfile_RCL_chooseLangs;

    private String nickNameInserted;
    private int age = 1;
    private String description;
    private ArrayList<Language> selectedLangs = new ArrayList<>();
    private boolean isLocationAllowed;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_LOCATION_CODE = 1000;
    private Location currentLocation;
    private double latitude, longitude;
    private String locationStr;
    private MyLocation myLocation;

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
            view = inflater.inflate(R.layout.fragment_pick_language, container, false);
        }

        findViews();

//        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        getLocation();

        createLanguagesRecycler();

//        switch arrow direction when "opening" languages menu.
        initProfile_IMG_editLangs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(initProfile_RCL_chooseLangs.getVisibility() == View.GONE) {
                    initProfile_RCL_chooseLangs.setVisibility(View.VISIBLE);
                    initProfile_IMG_editLangs.setImageResource(R.drawable.ic_up_arrow_menu);
                } else {
                    initProfile_RCL_chooseLangs.setVisibility(View.GONE);
                    initProfile_IMG_editLangs.setImageResource(R.drawable.ic_down_arrow_menu);
                }

//                check if 1 language has been chosen and show next button if true.
                numOfSelectedLangs = 0;
                HashMap<String, Boolean> langMap = langPickerAdapter.getMap();
                for(boolean isPicked : langMap.values()) {
                    if(isPicked)
                        numOfSelectedLangs++;
                }

                if(numOfSelectedLangs > 0 && initProfile_RCL_chooseLangs.getVisibility() == View.GONE && hasEnteredNickname && age >= Constants.MINIMUM_AGE) {
                    initProfile_BTN_next.setVisibility(View.VISIBLE);
                } else {
                    initProfile_BTN_next.setVisibility(View.INVISIBLE);
                }
            }
        });

        initProfile_EDT_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().equals("")) {
                    hasEnteredNickname = true;
                } else {
                    hasEnteredNickname = false;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(numOfSelectedLangs > 0 && initProfile_RCL_chooseLangs.getVisibility() == View.GONE && hasEnteredNickname && age >= Constants.MINIMUM_AGE) {
                    initProfile_BTN_next.setVisibility(View.VISIBLE);
                } else {
                    initProfile_BTN_next.setVisibility(View.INVISIBLE);
                }
            }
        });

        initProfile_EDT_age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String ageReceived = initProfile_EDT_age.getText().toString();
                if(ageReceived.trim().equals("") || ageReceived.length() == 1) {
                    if(ageReceived.length() == 1) {
                        initProfile_EDT_age.setError("Must be 13+ to use this app");
                    }
                    age = Constants.MINIMUM_AGE-1;
//                    do nothing.
                } else {
                    if(Integer.parseInt(ageReceived) < 13) {
                        initProfile_EDT_age.setError("Must be 13+ to use this app");
                        age = Constants.MINIMUM_AGE-1;
                    } else {
                        age = Integer.parseInt(ageReceived);
                    }
                }

                if(numOfSelectedLangs > 0 && initProfile_RCL_chooseLangs.getVisibility() == View.GONE && hasEnteredNickname && age >= Constants.MINIMUM_AGE) {
                    initProfile_BTN_next.setVisibility(View.VISIBLE);
                } else {
                    initProfile_BTN_next.setVisibility(View.INVISIBLE);
                }
            }
        });

        initProfile_BTN_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickNameInserted = initProfile_EDT_nickname.getText().toString();
                isLocationAllowed = initProfile_SWC_showLocation.isChecked();
                description = initProfile_EDT_description.getText().toString();
                callBackProceed.next(PAGE_NUMBER);
            }
        });


        return view;
    }

    private void findViews() {
        initProfile_EDT_nickname = view.findViewById(R.id.initProfile_EDT_nickname);
        initProfile_EDT_age = view.findViewById(R.id.initProfile_EDT_age);
        initProfile_EDT_description = view.findViewById(R.id.initProfile_EDT_description);
        initProfile_SWC_showLocation = view.findViewById(R.id.initProfile_SWC_showLocation);
        initProfile_TXT_locationDetected = view.findViewById(R.id.initProfile_TXT_locationDetected);
        initProfile_BTN_next = view.findViewById(R.id.initProfile_BTN_next);
        initProfile_IMG_editLangs = view.findViewById(R.id.initProfile_IMG_editLangs);
        initProfile_RCL_chooseLangs = view.findViewById(R.id.initProfile_RCL_chooseLangs);
    }

    private void createLanguagesRecycler() {
//        creating the language recyclerView
        langListRecyclerView = view.findViewById(R.id.initProfile_RCL_chooseLangs);
        langListRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        langPickerAdapter = new LangPickerListAdapter(view.getContext(), LangsConstants.getAllLangsList());
        langPickerAdapter.setClickListener(myLangPickerAdapterClickListener);
        langListRecyclerView.setAdapter(langPickerAdapter);
    }

    LangPickerListAdapter.ItemClickListener myLangPickerAdapterClickListener = new LangPickerListAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            boolean isPicked = langPickerAdapter.getMap().get(langPickerAdapter.getItem(position).getLangName());
            Language selLang = langPickerAdapter.getItem(position);
            langPickerAdapter.setMapKey(selLang.getLangName(), !isPicked);
            langPickerAdapter.notifyItemChanged(position);
            if(!isPicked) {
                selectedLangs.add(selLang);
            } else {
                selectedLangs.remove(selLang);
            }
            Log.d("vvv", "pressed " + position + " language");
        }
    };

    public String getDescription() {
        return description;
    }

    public int getAge() {
        return age;
    }

    public String getNickNameInserted() {
        return nickNameInserted;
    }

    public ArrayList<Language> getSelectedLangs() {
        return selectedLangs;
    }

    public boolean isLocationAllowed() {
        return isLocationAllowed;
    }

    public MyLocation getMyLocation() {
        return this.myLocation;
    }

    private void getLocation() {
        //check location permissions
        if (ActivityCompat.checkSelfPermission(
                view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            return;
        }
        //set location
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    myLocation = new MyLocation(longitude, latitude, setLocation(latitude, longitude));
                    Log.d("vvvLocation", "onSuccess: " + latitude + " " + longitude);
                } else {
                    longitude = 1;
                    latitude = 1;
                    myLocation = new MyLocation(longitude, latitude, Constants.MSG_LOCATION_NOT_FOUND);
                    Log.d("vvvLocation", "else (not success): " + latitude + " " + longitude);
                }
                showLocationOnTextView(myLocation.getAddress());
            }
        });
    }

    private String setLocation(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(view.getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String knownName = addresses.get(0).getFeatureName();

                if (knownName != null) {
                    locationStr = knownName + " ," + city + ", " + country;
                } else {
                    if (address != null)
                        locationStr = address + " ," + city + ", " + country;
                }
                return locationStr;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No location is found";
    }

    private void showLocationOnTextView(String location) {
        initProfile_TXT_locationDetected.setText(location);
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case LOCATION_REQUEST_CODE: {// If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            if (location != null) {
//                                longitude = location.getLongitude();
//                                latitude = location.getLatitude();
//                            }
//                        }
//                    });
//                } else {
//                    if (grantResults.length > 0)
//                        Toast.makeText(view.getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }


}
