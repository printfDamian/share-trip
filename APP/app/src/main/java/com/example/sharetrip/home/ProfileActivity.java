package com.example.sharetrip.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sharetrip.R;
import com.example.sharetrip.api.ApiClient;
import com.example.sharetrip.api.auth.LoginRequest;
import com.example.sharetrip.api.auth.LoginResponse;
import com.example.sharetrip.api.auth.SignupResponse;
import com.example.sharetrip.api.user.UpdateRequest;
import com.example.sharetrip.auth.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    static final public Integer RESULT_LOGOUT = 200;
    private boolean editState = false;
    SharedPreferences prefs;
    String prefsUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences(LoginActivity.PREFS, MODE_PRIVATE);

        LinearLayout profile_llBack = findViewById(R.id.Profile_llBack);
        TextInputEditText profile_inputName = findViewById(R.id.Profile_inputName);
        TextInputEditText profile_inputEmail = findViewById(R.id.Profile_inputEmail);
        TextInputEditText profile_inputPassword = findViewById(R.id.Profile_inputPassword);
        ImageButton Profile_btnRemove = findViewById(R.id.Profile_btnRemove);
        ImageButton profile_btnEdit = findViewById(R.id.Profile_btnEdit);
        ImageButton profile_btnSave = findViewById(R.id.Profile_btnSave);
        ImageButton profile_btnLogout = findViewById(R.id.Profile_btnLogout);
        CardView profile_CardSave = findViewById(R.id.Profile_CardSave);

        profile_inputEmail.setEnabled(false);
        profile_inputEmail.setFocusable(false);
        profile_inputEmail.setFocusableInTouchMode(false);
        profile_inputEmail.setCursorVisible(false);

        setEditMode(profile_inputName, profile_inputEmail, profile_inputPassword, profile_CardSave, false);
        loadInfo(profile_inputName, profile_inputEmail, profile_inputPassword);

        profile_llBack.setOnClickListener(v ->  {
            finish();
        });

        Profile_btnRemove.setOnClickListener(v -> {
            removeAccount();
        });

        profile_btnEdit.setOnClickListener(v -> {
            editState = !editState;
            setEditMode(profile_inputName, profile_inputEmail, profile_inputPassword, profile_CardSave, editState);
        });

        profile_btnSave.setOnClickListener(v -> {
            saveChanges(profile_inputName, profile_inputEmail, profile_inputPassword, profile_CardSave);
        });

        profile_btnLogout.setOnClickListener(v -> {
            setResult(RESULT_LOGOUT);
            finish();
        });
    }

    private void saveChanges(TextInputEditText profile_inputName, TextInputEditText profile_inputEmail, TextInputEditText profile_inputPassword, CardView profile_CardSave) {
        String username = profile_inputName.getText().toString().trim();
        String password = profile_inputPassword.getText().toString();

        prefsUsername = username;

        if(username.equals(prefs.getString(LoginActivity.USERNAME, getString(R.string.username_notfound)))) {
            username = null;
        }

        if(password.equals("********")) {
            password = null;
        }

        if(username == null && password == null) {
            return;
        }

        UpdateRequest request = new UpdateRequest(username, password);

        ApiClient.getApiService(this).updateUser(request).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SignupResponse updateResponse = response.body();

                    if (updateResponse.isSuccess()) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(LoginActivity.USERNAME, prefsUsername);
                        editor.apply();

                        Toast.makeText(ProfileActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();

                        setEditMode(profile_inputName, profile_inputEmail, profile_inputPassword, profile_CardSave, false);
                    } else {
                        Toast.makeText(ProfileActivity.this, updateResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                System.out.println(getString(R.string.error) + t.getMessage());
                Toast.makeText(ProfileActivity.this, getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeAccount() {
        ApiClient.getApiService(this).deleteUser().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, getString(R.string.acc_eliminated), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_LOGOUT);
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.err_acc_elimin), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadInfo(TextInputEditText profile_inputName, TextInputEditText profile_inputEmail, TextInputEditText profile_inputPassword) {
        profile_inputName.setText(prefs.getString(LoginActivity.USERNAME, getString(R.string.username_notfound)));
        profile_inputEmail.setText(prefs.getString(LoginActivity.EMAIL, getString(R.string.email_notfound)));
        profile_inputPassword.setText("********");
    }

    private void setEditMode(TextInputEditText profile_inputName, TextInputEditText profile_inputEmail, TextInputEditText profile_inputPassword, CardView profile_CardSave, boolean enabled) {
        loadInfo(profile_inputName, profile_inputEmail, profile_inputPassword);

        profile_inputName.setEnabled(enabled);
        profile_inputName.setFocusable(enabled);
        profile_inputName.setFocusableInTouchMode(enabled);
        profile_inputName.setCursorVisible(enabled);

        profile_inputPassword.setEnabled(enabled);
        profile_inputPassword.setFocusable(enabled);
        profile_inputPassword.setFocusableInTouchMode(enabled);
        profile_inputPassword.setCursorVisible(enabled);

        profile_CardSave.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }
}