package com.tandai.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    ImageView back,change_image;
    CircleImageView image;
    TextView name,email,phone;

    Button btnUpdate;

    AlertDialog waiting;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(Common.currUserID);

    int REQUEST_CODE_FOLDER = 123;
    String link_image = "";

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://mychatapp-14037.appspot.com/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back = findViewById(R.id.btn_back_profile);
        change_image =findViewById(R.id.change_image_profile);
        image =findViewById(R.id.image_profile);
        name = findViewById(R.id.name_profile);
        email = findViewById(R.id.email_profile);
        phone = findViewById(R.id.phone_profile);
        btnUpdate = findViewById(R.id.btnUpdateInfo);
        waiting = new ProgressDialog(ProfileActivity.this);
        waiting.setMessage("Vui lòng đợi...");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getImage().equals("") == false){
                    Picasso.with(getApplicationContext()).load(user.getImage()).into(image);
                }
                name.setText(user.getName());
                email.setText(user.getEmail());
                phone.setText(user.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_FOLDER);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogUpdate();
            }

        });


    }

    // get image from folder
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                waiting.show();
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                image.setImageBitmap(bitmap);

                Calendar calendar = Calendar.getInstance();
                String tenhinh="image"+calendar.getTimeInMillis();
                final StorageReference mountainsRef = storageRef.child(tenhinh+".png");
                image.setDrawingCacheEnabled(true);
                image.buildDrawingCache();

                Bitmap bitmap1 = ((BitmapDrawable) image.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data1 = baos.toByteArray();

                final UploadTask uploadTask = mountainsRef.putBytes(data1);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // get downloadUrl
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                link_image = mountainsRef.getDownloadUrl().toString();
                                // Continue with the task to get the download URL
                                return mountainsRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                waiting.dismiss();
                                if (task.isSuccessful()) {
                                    link_image = task.getResult().toString();
                                    ref.child("image").setValue(link_image);
                                    Toast.makeText(ProfileActivity.this, "Thêm ảnh thành công", Toast.LENGTH_SHORT).show();
                                    // them image vao Friend
                                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Friends");

                                    Query query = databaseReference.orderByChild(Common.currUserID+"/userID").equalTo(Common.currUserID);
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                                ds.child(Common.currUserID).child("image").getRef().setValue(link_image);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                                else {
                                    Toast.makeText(ProfileActivity.this, "Thêm không thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });


            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void showDialogUpdate() {
        final Dialog dialog   = new Dialog(ProfileActivity.this,R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_update_profile);
        dialog.show();
        final EditText name = dialog.findViewById(R.id.updateName);
        final EditText phone = dialog.findViewById(R.id.updatePhone);
        Button update = dialog.findViewById(R.id.btnUpdateProfile);
        Button cancel = dialog.findViewById(R.id.btnCancelProfile);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                phone.setText(user.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Name = name.getText().toString();
                final String Phone = phone.getText().toString();
                if(Name.isEmpty() || Phone.isEmpty()){
                    Toast.makeText(ProfileActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ProfileActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    ref.child("name").setValue(Name);
                    ref.child("phone").setValue(Phone);
                }
            }
        });

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
