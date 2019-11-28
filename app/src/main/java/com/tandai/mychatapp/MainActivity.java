package com.tandai.mychatapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tandai.mychatapp.Fragment.ChatsFragment;
import com.tandai.mychatapp.Fragment.GroupChatFragment;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.User;
import com.tandai.mychatapp.Model.ViewPagerAdapter;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int TIME_DELAY = 2500;
    private static long back_pressed = 0;


    TextView email_header;
    CircleImageView image_header;

    DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);

        email_header = headerview.findViewById(R.id.email_header);
        image_header = headerview.findViewById(R.id.imagePerson_header);

        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        final ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
        viewPagerAdapter.addFragment(new GroupChatFragment(), "Group Chats");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        LoadDataUser();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if(back_pressed + TIME_DELAY > System.currentTimeMillis()){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this, "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(getApplicationContext(),SearchUserActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_friends) {
            startActivity(new Intent(MainActivity.this,FriendActivity.class));
        }
        else if (id == R.id.nav_profile_setting) {
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        }
        else if (id == R.id.nav_change_password) {
            startActivity(new Intent(MainActivity.this,ChangePasswordActivity.class));
        }
        else if (id == R.id.nav_logout) {
            // open dialog
            final Dialog dialogLogOut = new Dialog(MainActivity.this,R.style.Theme_Dialog);
            dialogLogOut.setContentView(R.layout.dialog_logout);
            dialogLogOut.show();

            Button khong= dialogLogOut.findViewById(R.id.btnKhongDialogDangXuat);
            Button thoat= dialogLogOut.findViewById((R.id.btnDialogDangXuat));
            khong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogLogOut.cancel();
                }
            });
            thoat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogLogOut.cancel();
                    //delete remember user and password
                    Paper.book().destroy();
                    Common.Status("offline");
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }
            });

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void LoadDataUser(){
        mDatabase.child("Users").child(Common.currUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                email_header.setText(user.getEmail());
                if(user.getImage().equals("")==false) {
                    Picasso.with(getApplicationContext()).load(user.getImage()).into(image_header);
                }
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
