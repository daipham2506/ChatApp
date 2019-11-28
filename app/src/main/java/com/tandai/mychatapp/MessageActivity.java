package com.tandai.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tandai.mychatapp.Adapter.MessageAdapter;
import com.tandai.mychatapp.Model.Chat;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Chat> arrMessage = new ArrayList<>();
    MessageAdapter messageAdapter;

    String username_receiver ="";
    String name_receiver="";
    String image_receiver="";

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    FirebaseStorage storage;
    FirebaseDatabase database1;
    Uri pdfUri;

    ProgressDialog progressDialog;

    String url="";
    String filename="";

    int REQUEST_CODE_SEND_FILE = 259;

    CircleImageView image;
    ImageView attach_image, attach_file, btn_send, check_send_text ,back , isonline;
    EditText textMessage;
    TextView userTyping,check_connect,name,text_online;


    ValueEventListener seenListener;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        storage = FirebaseStorage.getInstance();
        database1 = FirebaseDatabase.getInstance();

        //set color status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        final Intent intent = getIntent();
        username_receiver = intent.getStringExtra("username_receiver");
        image_receiver = intent.getStringExtra("image_receiver");
//        name_receiver = intent.getStringExtra("name_receiver");

        


        name = findViewById(R.id.name_message);
        image = findViewById(R.id.image_message);
        isonline = findViewById(R.id.isonline_message);
        back = findViewById(R.id.back_message);
        text_online = findViewById(R.id.text_online_message);

        attach_file= findViewById(R.id.AttachFile_message);
        attach_image= findViewById(R.id.AttachImage_message);
        btn_send = findViewById(R.id.btn_send_message);
        check_send_text = findViewById(R.id.check_send_text_message);
        textMessage = findViewById(R.id.text_message);
        userTyping = findViewById(R.id.UserTyping_message);
        check_connect= findViewById(R.id.wait_connect_message);

        initRecyclerView();

//        name.setText(name_receiver);
//        if(image_receiver.equals("")==false){
//            Picasso.with(getApplicationContext()).load(image_receiver).into(image);
//        }
        readDataUserHeader(username_receiver);

        readMesagges(Common.currUserID,username_receiver,image_receiver);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        textMessage.addTextChangedListener(new TextWatcher() {

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
                    attach_file.setVisibility(View.GONE);
                    attach_image.setVisibility(View.GONE);
                    check_send_text.setVisibility(View.VISIBLE);
                }
                else {
                    check_send_text.setVisibility(View.GONE);
                    attach_file.setVisibility(View.VISIBLE);
                    attach_image.setVisibility(View.VISIBLE);
                }

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get date-time
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy  hh:mm a");
                final String dateTime = dateFormat.format(c.getTime());

                if(pdfUri != null){
                    uploadFile(pdfUri,Common.currUserID,username_receiver,dateTime);
                    //reset when uploaded
                    pdfUri = null;
                }
                else{
                    final String msg = textMessage.getText().toString();
                    if (msg.equals("")){
                        Toast.makeText(MessageActivity.this, "Bạn chưa gõ tin nhắn", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        sendMessage(Common.currUserID, username_receiver, msg,dateTime);
                    }
                }

                textMessage.setText("");

            }
        });

        attach_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    selectFile();
                }
                else
                    ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
            }
        });

        seenMessage(username_receiver);




    }

    private void uploadFile(Uri pdfUri, final String sender, final String receiver, final String time ) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading file...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        progressDialog.show();

        StorageReference storageReference = storage.getReference();
        final StorageReference filePath = storageReference.child("Uploads").child(filename);
        final UploadTask uploadTask = filePath.putFile(pdfUri);


        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // get downloadUrl
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        url = uri.toString();
                        DatabaseReference reference = database1.getReference();

                        Chat chat = new Chat( 1,sender,receiver,filename,url,time,false);
                        reference.child("Chats").push().setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Toast.makeText(MessageActivity.this, "File successfully uploaded!", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(MessageActivity.this, "File not successfully uploaded!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, "File not successfully uploaded!", Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                // track the progress upload
                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()
                        /taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
                if (currentProgress == 100)
                    progressDialog.dismiss();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 9 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            selectFile();
        }
        else
            Toast.makeText(this, "Please grant access", Toast.LENGTH_SHORT).show();
    }

    private void selectFile(){

        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"ChooseFile"), REQUEST_CODE_SEND_FILE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //check whether user has selected a file or not
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEND_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();
            String pathSegment = data.getData().getLastPathSegment();
            String[] parts = pathSegment.split("/");
            filename = parts[parts.length - 1];
            textMessage.setText(filename);

        } else {
            Toast.makeText(this, "Please choose file", Toast.LENGTH_SHORT).show();
        }
    }


    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(Common.currUserID) && chat.getSender().equals(userid)){
                        snapshot.child("isSeen").getRef().setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String msg,String time){
        Chat chat = new Chat( 0,sender,receiver,msg,"",time,false);
        database.child("Chats").push().setValue(chat);
    }


    private void readMesagges(final String myid, final String userid, final String imageurl) {
        database.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrMessage.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        arrMessage.add(chat);
                    }
                }
                messageAdapter = new MessageAdapter(MessageActivity.this, arrMessage, imageurl);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readDataUserHeader(final String userID) {
        database.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                if(user.getImage().equals("")==false){
                    Picasso.with(getApplicationContext()).load(user.getImage()).into(image);
                }
                if(user.getStatus().equals("online")){
                    isonline.setVisibility(View.VISIBLE);
                    text_online.setVisibility(View.VISIBLE);
                }

                else{
                    isonline.setVisibility(View.GONE);
                    text_online.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_Messages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.Status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        Common.Status("offline");
    }



}
