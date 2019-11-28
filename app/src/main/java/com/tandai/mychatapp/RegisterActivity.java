package com.tandai.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tandai.mychatapp.Model.User;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    EditText email,pass,name,phone;
    Button btnDangKy;
    FirebaseAuth mAuthencation;
    DatabaseReference mData;
    FirebaseUser firebaseUser;
    AlertDialog waiting;

    String Email,Pass,Name,Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Mapping();
        mAuthencation = FirebaseAuth.getInstance();
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting.show();
                Register();
            }
        });
        mData = FirebaseDatabase.getInstance().getReference();


    }
    private void Mapping(){
        email =  findViewById(R.id.emailReg);
        pass =  findViewById(R.id.passReg);
        name = findViewById(R.id.nameReg);
        phone = findViewById(R.id.phoneReg);
        btnDangKy=findViewById(R.id.btnReg);
        waiting =  new SpotsDialog.Builder().setContext(this).setMessage("Vui lòng đợi...").setCancelable(false).build();
    }


    private void Register() {
        Email = email.getText().toString().trim(); //trim() bỏ khoảng trống ở đầu và cuối chuỗi
        Pass = pass.getText().toString().trim();
        Name = name.getText().toString().trim();
        Phone = phone.getText().toString().trim();


        if (Email.isEmpty() || Pass.isEmpty() || Name.isEmpty() || Phone.isEmpty() ) {
            waiting.dismiss();
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin. ", Toast.LENGTH_SHORT).show();
        }
        else {
            mAuthencation.createUserWithEmailAndPassword(Email, Pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                firebaseUser = mAuthencation.getCurrentUser();
                                final User user    = new User(Name,Email,Pass,Phone,"",firebaseUser.getUid(),"offline");
                                firebaseUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    waiting.dismiss();
                                                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công. Vui lòng xác thực Email", Toast.LENGTH_LONG).show();
                                                    //push data len realtime database
                                                    mData.child("Users").child(firebaseUser.getUid()).setValue(user);

                                                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

                                                }
                                                else{
                                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });



                            }
                            else {
                                waiting.dismiss();
                                Toast.makeText(RegisterActivity.this, "Tài khoản đã tồn tại.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


}
