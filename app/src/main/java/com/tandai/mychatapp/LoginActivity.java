package com.tandai.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.User;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuthencation;
    Button btnLog;
    EditText email;
    EditText pass;

    CheckBox Remember;
    TextView forgotPass, register;

    AlertDialog waiting;

    FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Mapping();

        mAuthencation = FirebaseAuth.getInstance();

        Paper.init(this);

        if(isNetworkAvailable() )
            AutoLogin();
        else
            handleInternet();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Email = email.getText().toString().trim();
                final String Pass = pass.getText().toString().trim();
                Login(Email,Pass);
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPass();
            }
        });

    }

    private void Mapping(){
        btnLog= findViewById(R.id.btnLog);
        email=  findViewById(R.id.emailLog);
        pass=findViewById((R.id.passLog));
        Remember = findViewById(R.id.ckbRemember);
        forgotPass =  findViewById(R.id.forgotPass);
        register = findViewById(R.id.register);
        waiting =  new SpotsDialog.Builder().setContext(this).setMessage("Vui lòng đợi...").setCancelable(false).build();
    }

    private void Login(String Email, final String Pass){

        if (Email.isEmpty() || Pass.isEmpty() ) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin. ", Toast.LENGTH_SHORT).show();
        }
        else {
            if(isNetworkAvailable()) {
                waiting.show();
                if (Remember.isChecked()) {
                    Paper.book().write(Common.USER_KEY, Email);
                    Paper.book().write(Common.PWD_KEY, Pass);
                }

                mAuthencation.signInWithEmailAndPassword(Email, Pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
                            String userID = USER.getUid();

                            waiting.dismiss();
                            if (USER.isEmailVerified()) {
                                Common.currUserID = userID;
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Vui lòng xác thực Email để đăng nhập", Toast.LENGTH_SHORT).show();
                            }



                            // ghi lai mk trong database neu quen mat kau sau khi lay lai

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    dataSnapshot.child("pass").getRef().setValue(Pass);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            mDatabase.addListenerForSingleValueEvent(eventListener);

                        }
                        else {
                            waiting.dismiss();
                            Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không hợp lệ.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "Kiểm tra kết nối Internet", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void AutoLogin(){
        String user1 = Paper.book().read(Common.USER_KEY);
        String pass1 = Paper.book().read(Common.PWD_KEY);
        email.setText(user1);
        pass.setText(pass1);
        Remember.setChecked(true);
        if(user1 != null && pass1 != null){
            if(!user1.isEmpty() && !pass1.isEmpty()){
                Login(user1,pass1);
            }
        }
    }

    // kiểm tra kết nối internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void handleInternet(){
        final Dialog dialog   = new Dialog(this,R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_internet);
        Button btnThoat =    dialog.findViewById(R.id.btnThoatDiaLogInternet);
        Button btnConnect=  dialog.findViewById(R.id.btnBatWifi);
        dialog.show();

        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifi.setWifiEnabled(true);

                // wait turn on wifi done
                Handler setDelay = new Handler();
                Runnable startDelay;

                startDelay = new Runnable() {
                    @Override
                    public void run() {
                        AutoLogin();
                    }
                };
                setDelay.postDelayed(startDelay,3500);

            }
        });
    }


    private void handleForgotPass() {
        final Dialog dialog   = new Dialog(LoginActivity.this,R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_forgot_pass);
        dialog.show();
        final EditText email = dialog.findViewById(R.id.email_forgot_pass);
        final Button confirm = dialog.findViewById(R.id.btn_confirm_forgot_pass);
        email.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    confirm.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.blue_border));
                    confirm.setTextColor(getResources().getColor(R.color.colorWhite));
                }
                else{
                    confirm.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gray_border));
                    confirm.setTextColor(Color.parseColor("#79000000"));
                }

            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                if (Email.length() > 0) {
                    firebaseAuth.sendPasswordResetEmail(Email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Mở Email để đổi mật khẩu mới", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Email không chính xác.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

        });

    }

}
