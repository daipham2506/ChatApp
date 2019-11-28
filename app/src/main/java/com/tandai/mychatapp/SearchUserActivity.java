package com.tandai.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tandai.mychatapp.Adapter.UserAdapter;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.User;

import java.util.ArrayList;

public class SearchUserActivity extends AppCompatActivity {

    DatabaseReference database;
    String userID = Common.currUserID;

    RecyclerView recyclerView;
    ArrayList<User> arrUser = new ArrayList<>();
    UserAdapter userAdapter;


    ImageView back,search;
    EditText text_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        //set color status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        database = FirebaseDatabase.getInstance().getReference().child("Users");
        back = findViewById(R.id.back_search);
        search = findViewById(R.id.search_search);
        text_search = findViewById(R.id.text_search_search);

        init_recyclerView_user();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchUserActivity.this,MainActivity.class));
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTextSearch();
            }
        });


        text_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    handleTextSearch();
            }
        });



    }



    private void init_recyclerView_user(){
        recyclerView = findViewById(R.id.recyler_user);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        handleTextSearch();
    }

    private void handleTextSearch(){
        final String text = text_search.getText().toString();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrUser.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if(user.getUserID().equals(Common.currUserID) == false){
                        if(Common.containsIgnoreCase(user.getName(),text) || Common.containsIgnoreCase(user.getEmail(),text))
                            arrUser.add(user);
                    }

                }
                userAdapter = new UserAdapter(arrUser,getApplicationContext());
                recyclerView.setAdapter(userAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
