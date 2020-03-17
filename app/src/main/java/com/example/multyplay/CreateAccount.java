package com.example.multyplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccount extends AppCompatActivity {

    private EditText create_EDT_firstName;
    private EditText create_EDT_lastName;
    private EditText create_EDT_email;
    private EditText create_EDT_confirm_email;
    private EditText create_EDT_password;
    private RadioGroup create_RAD_GRP_gender;
    private RadioButton selectedGender;
    private Button create_BTN_signup;

//    private boolean validAccount = true;
    private boolean validFirstName = true;
    private boolean validLastName = true;
    private boolean validEmail = true;
    private boolean emailNotExist = true;
    private boolean validConfirmEmail = false;
    private boolean matchEmails = false;
    private boolean validPassword = true;

    private ArrayList<Account> accounts;

//    private AccountCreatedFragment accountCreatedFragment;
    private Id id;

    private Account accountCreated;
    private MySharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        create_EDT_firstName = findViewById(R.id.create_EDT_firstName);
        create_EDT_lastName = findViewById(R.id.create_EDT_lastName);
        create_EDT_email = findViewById(R.id.create_EDT_email);
        create_EDT_confirm_email = findViewById(R.id.create_EDT_confirm_email);
        create_EDT_password = findViewById(R.id.create_EDT_password);
        create_RAD_GRP_gender = findViewById(R.id.create_RAD_GRP_gender);
        create_BTN_signup = findViewById(R.id.create_BTN_signup);

        prefs = new MySharedPreferences(this);

//        accountCreatedFragment = new AccountCreatedFragment();
//        accountCreatedFragment.setCallback(okCallback);

        create_BTN_signup.setOnClickListener(signupBtnListener);

    }

    public View.OnClickListener signupBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MyFirebase.getAccounts(new CallBackAccountsReady() {
                @Override
                public void accountsReady(ArrayList<Account> accounts) {
                    accountCreated = getValidAccount(accounts);
                    if(accountCreated == null) {
                        if(!validFirstName)
                            create_EDT_firstName.setError("Invalid name");
                        if(!validLastName)
                            create_EDT_lastName.setError("Invalid name");
                        if(!validEmail)
                            create_EDT_email.setError("Invalid email");
                        if(!emailNotExist)
                            create_EDT_email.setError("Email is already registered");
                        if(!validConfirmEmail)
                            create_EDT_confirm_email.setError("Invalid email");
                        if(!matchEmails)
                            create_EDT_confirm_email.setError("Confirm by insert same as email field");
                        if(!validPassword)
                            create_EDT_password.setError("Password must contain at least 6 characters, 1 Uppercase letter and 1 number");
                    } else {
//                        save account created into FireBase and SharedPrefs
                        MyFirebase.setAccount(accountCreated);
                        String jsAccountCreated = new Gson().toJson(accountCreated);
                        prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsAccountCreated);

                        prefs.putString(Constants.PREFS_KEY_CURRENT_LOGGED_IN, Constants.LOGGED_OUT);
//                        LoginHelper loginHelper = new LoginHelper(accountCreated, false, false);
//                        String jsLoginHelper = new Gson().toJson(loginHelper);
//                        prefs.putString(Constants.PREFS_KEY_CURRENT_LOGGED_IN, jsLoginHelper);

                        CreatedAccDialog createdAccDialog = new CreatedAccDialog();
                        createdAccDialog.show(getSupportFragmentManager(), "created account dialog");
                        createdAccDialog.setCallBackApproved(new CallBackApproved() {
                            @Override
                            public void onOkClick() {
                                Intent setupProfileIntent = new Intent(CreateAccount.this, InitialProfileSettings.class);
                                startActivity(setupProfileIntent);
                                finish();
                            }
                        });



//                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.create_LAY_success, accountCreatedFragment);
//                        transaction.commit();
                    }
                }

                @Override
                public void error() {

                }
            });

        }
    };

    private void updateLastId(Id id) {
        this.id = id;
    }

    private Id createUpdatedId() {
        return new Id(this.id.getCode(), this.id.getSerialNum()+1);
    }

    private Id getId() {
        return this.id;
    }

    public Account getValidAccount(ArrayList<Account> accounts) {
//        Check if first name is valid.
        String firstName = create_EDT_firstName.getText().toString();
        validFirstName = isNameValid(firstName);

//        check if last name is valid.
        String lastName = create_EDT_lastName.getText().toString();
        validLastName = isNameValid(lastName);

//        check if inserted a proper email address into Email field.
        String emailAddress = create_EDT_email.getText().toString();
        validEmail = isEmailValid(emailAddress);
//        check if email already exist in Firebase DB
        emailNotExist = true;
        for (Account acc : accounts) {
            if(emailAddress.equalsIgnoreCase(acc.getEmail().toString())) {
                emailNotExist = false;
            }
        }

//        check if inserted a proper email address into confirm email field.
        if(validEmail) {
            String confirmEmailAddress = create_EDT_confirm_email.getText().toString();
            validConfirmEmail = isEmailValid(confirmEmailAddress);
            if(validConfirmEmail) {
                matchEmails = emailAddress.equals(confirmEmailAddress);
            }
        }

//        check if password contains 1 Uppercase letter, 1 number and at lease 6 chars.
        String password = create_EDT_password.getText().toString();
        validPassword = isPasswordValid(password);

//        receive selected gender.
        int selectedRadioBtn = create_RAD_GRP_gender.getCheckedRadioButtonId();
        selectedGender = findViewById(selectedRadioBtn);
        String gender = selectedGender.getText().toString();

//      Check if all validations are true. return account if they are, else return null.
        boolean[] allValidations = {validFirstName, validLastName, validEmail, emailNotExist, validConfirmEmail, matchEmails, validPassword};
        int counter = 0;
        for (boolean validation : allValidations) {
            if(validation)
                counter++;
        }
        if(counter == allValidations.length)
            return new Account(firstName, lastName, new Email(emailAddress), password, gender);
        return null;
    }

//    only A-z and 0-9 are aloud in names, empty name not aloud.
    public boolean isNameValid(String name) {
        if(name.trim().length() == 0)
            return false;
        for(int i = 0; i < name.length(); i++) {
            char currentChar = name.charAt(i);
            if(!(('A' <= currentChar && currentChar <= 'z') || ('0' <= currentChar && currentChar <= '9'))) {
                Log.d("vvv", "invalid char: " + name.charAt(i));
                return false;
            }
        }
        return true;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

//      password has at least 1 Uppercase letter, 1 number and 6 chars long.
    boolean isPasswordValid(String password) {
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=\\S+$).{6,}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

//    public CallBackApproved okCallback = new CallBackApproved() {
//        @Override
//        public void onOkClick() {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.remove(accountCreatedFragment);
//            transaction.commit();
//            Log.d("vvvGoInitProfile", "acount from prefs: " + prefs.getString(Constants.PREFS_KEY_ACCOUNT, ""));
//            Intent setupProfileIntent = new Intent(CreateAccount.this, InitialProfileSettings.class);
//            startActivity(setupProfileIntent);
//            finish();
//        }
//    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent LoginIntent = new Intent(CreateAccount.this, Login.class);
        startActivity(LoginIntent);
        finish();

    }
}
