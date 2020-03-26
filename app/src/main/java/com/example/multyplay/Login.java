package com.example.multyplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Login extends AppCompatActivity {

    private EditText login_EDT_email;
    private EditText login_EDT_password;
    private Button login_BTN_login;
    private ImageView login_IMG_showHidePass;

//    private boolean gotAccounts = false;
    private ArrayList<Account> accounts;

    private boolean foundEmail = false;
    private boolean correctPass = false;
    private Account myAccount;
    private MySharedPreferences prefs;


    //    private MySharedPreferences prefs;
    // TODO: 1/18/2020 insert to prefs and update there everytime to prevent leaving app and reset tries.
    private int wrongPassCounter = 0;
    private int numWrongPassAloud = 3;

    private boolean isPassShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO: 1/18/2020 uncomment when wrong password timer system is ready, meanwhile 0 at onCreate
//        wrongPassCounter = prefs.getInt(Constants.PREFS_KEY_WRONG_PASS_COUNTER, 0);
        prefs = new MySharedPreferences(this);

        login_EDT_email = findViewById(R.id.login_EDT_email);
        login_EDT_password = findViewById(R.id.login_EDT_password);
        login_IMG_showHidePass = findViewById(R.id.login_IMG_showHidePass);

        isPassShown = false;

        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_BTN_login.setOnClickListener(loginListener);

//        String loggedState = prefs.getString(Constants.PREFS_KEY_CURRENT_LOGGED_IN, Constants.LOGGED_OUT);
//        if(loggedState.equals(Constants.LOGGED_IN)) {
////            prefs.putString(Constants.PREFS_KEY_CURRENT_LOGGED_IN, Constants.LOGGED_IN);
//            Intent profileIntent = new Intent(Login.this, FragmentManager.class);
//            startActivity(profileIntent);
//            finish();
//        }

        login_IMG_showHidePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPassShown) {
                    login_IMG_showHidePass.setImageResource(R.drawable.ic_show_pass);
                    login_EDT_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    login_EDT_password.setSelection(login_EDT_password.length());
                    isPassShown = false;
                } else {
                    login_IMG_showHidePass.setImageResource(R.drawable.ic_hide_pass);
                    login_EDT_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    login_EDT_password.setSelection(login_EDT_password.length());
                    isPassShown = true;
                }

            }
        });

    }

    // TODO: 1/18/2020 First time ever login with firebase,
    //  later save account on prefs and compare to that.
    //  if creating new account or login with someone elses account - get from firebase again.
    //  (meaning when cant find the email on the device.)

    private void updateList(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    private boolean verifyLogin() {
        String email = login_EDT_email.getText().toString();
        String password = login_EDT_password.getText().toString();

        for (Account account : this.accounts) {
            foundEmail = email.equalsIgnoreCase(account.getEmail().toString());
            correctPass = password.equals(account.getPassword());

            if(foundEmail && correctPass) {
                Log.d("vvvLogin: ", "Account found: " + new Gson().toJson(account));
                myAccount = account;
                return true;
            }
        }
        return false;
    }

    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MyFirebase.getAccounts(new CallBackAccountsReady() {
                @Override
                public void accountsReady(ArrayList<Account> accounts) {
                    updateList(accounts);
                    if(verifyLogin()) {
                        String jsonAccount = new Gson().toJson(myAccount);
                        prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsonAccount);

                        String acc = new Gson().toJson(myAccount);
                        Log.d("vvvLogin", "account: " + acc);
//                        no languages meaning didn't finish setup profile.
                        if(myAccount.getLanguages() == null) {
                            Intent initProfileIntent = new Intent(Login.this, InitialProfileSettings.class);
                            startActivity(initProfileIntent);
                            finish();
                        } else {
                            prefs.putString(Constants.PREFS_KEY_CURRENT_LOGGED_IN, Constants.LOGGED_IN);
                            SearchFilters defaultSearchFilters = new SearchFilters(Constants.DEFAULT_SEARCH_FILTER_MAX_DISTANCE, Constants.DEFAULT_SEARCH_FILTER_MIN_AGE, Constants.DEFAULT_SEARCH_FILTER_MAX_AGE);
                            String jsSearchFilters = new Gson().toJson(defaultSearchFilters);
                            prefs.putString(Constants.PREFS_KEY_SEARCH_FILTERS, jsSearchFilters);

                            Intent profileIntent = new Intent(Login.this, FragmentManager.class);
                            profileIntent.putExtra(Constants.KEY_MARK_IS_OWNER, Constants.MY_PROFILE);
                            startActivity(profileIntent);
                            finish();
                        }

                    } else {
                        if(!foundEmail) {
                            login_EDT_email.setError(Constants.MSG_EMAIL_DONT_EXIST);
                            Toast.makeText(Login.this, Constants.MSG_EMAIL_DONT_EXIST, Toast.LENGTH_SHORT).show();
                        }
                        else if(!correctPass) {
                            // TODO: 1/18/2020 reset timer running for 5 minutes (if timer finishes restart tries.).
                            login_EDT_password.setError(Constants.MSG_WRONG_PASSWORD);
                            Toast.makeText(Login.this, Constants.MSG_WRONG_PASSWORD, Toast.LENGTH_SHORT).show();
                            wrongPassCounter++;
                            if(wrongPassCounter == numWrongPassAloud) {
                                // TODO: 1/18/2020 make login button useless until timer finishes.
                            }
                        }
                    }
                }

                @Override
                public void error() {
                }
            });
        }
    };

//    called from xml onClick
    public void goToCreateAccount(View view) {
        Intent createAccountIntent = new Intent(Login.this, CreateAccount.class);
        startActivity(createAccountIntent);
        finish();
    }

//login_BTN_login.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            MyFirebase.getAccounts(new CallBackAccountsReady() {
//                @Override
//                public void accountsReady(ArrayList<Account> accounts) {
//                    updateList(accounts);
//                    if(verifyLogin()) {
//                        String jsonAccount = new Gson().toJson(myAccount);
//                        Intent profileIntent = new Intent(Login.this, Profile.class);
//                        profileIntent.putExtra(Constants.KEY_MY_ACCOUNT_JSON, jsonAccount);
//                        startActivity(profileIntent);
//                        finish();
//                    } else {
//                        if(!foundEmail)
//                            login_EDT_email.setError("Email doesn't exist");
//                        if(!correctPass) {
//                            login_EDT_password.setError("Wrong password. try again.");
//                            wrongPassCounter++;
//                        }
//                    }
//                }
//
//                @Override
//                public void error() {
//                }
//            });
//        }
//    });

}


