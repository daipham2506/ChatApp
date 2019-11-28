package com.tandai.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.User;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText newPass,confirmPass , oldPass;
    private Button btnConfirm,btnCancel;
    DatabaseReference mDatabase ;
    private String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //set color status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        Mapping();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(Common.currUserID);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User uInfo = dataSnapshot.getValue(User.class);
                pass = uInfo.getPass();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChangePass();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePasswordActivity.this,MainActivity.class));
            }
        });

    }



    private void Mapping(){
        newPass =  findViewById(R.id.newPass);
        confirmPass= findViewById(R.id.confirmPass);
        oldPass = findViewById(R.id.PassOld);
        btnConfirm= findViewById(R.id.btnConfirm);
        btnCancel= findViewById(R.id.btnCancel);
    }


    private void handleChangePass(){

        final String MKcu = oldPass.getText().toString().trim();
        final String MK = newPass.getText().toString().trim();
        final String MK1 = confirmPass.getText().toString().trim();
        if(MK1.isEmpty() || MK.isEmpty() || MKcu.isEmpty()){
            Toast.makeText(ChangePasswordActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
        else {
            if (MKcu.equals(pass) == false) {
                Toast.makeText(ChangePasswordActivity.this, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
            }
            else {
                if (MK.equals(MK1)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String userID = user.getUid();
                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    user.updatePassword(MK)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ChangePasswordActivity.this, "Bạn đã đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                        // updata pass in database
                                        mDatabase.child("Users").child(userID).child("pass").setValue(MK);
                                        //ve man hinh login
                                        startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Đổi không thành công", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(ChangePasswordActivity.this, "Mật khẩu nhập lại không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Common.Status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Common.Status("offline");
    }


}

