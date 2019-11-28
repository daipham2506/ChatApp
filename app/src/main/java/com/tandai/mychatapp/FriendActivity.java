package com.tandai.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tandai.mychatapp.Fragment.ChatsFragment;
import com.tandai.mychatapp.Fragment.FriendFragment;
import com.tandai.mychatapp.Fragment.GroupChatFragment;
import com.tandai.mychatapp.Fragment.InviteFriendFragment;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.ViewPagerAdapter;

public class FriendActivity extends AppCompatActivity {

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    int count_invite = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        //set color status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        final TabLayout tabLayout = findViewById(R.id.tab_layout_friend);
        final ViewPager viewPager = findViewById(R.id.view_pager_friend);


        mDatabase.child("MakeFriends").child(Common.currUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

                viewPagerAdapter.addFragment(new FriendFragment(), "Bạn bè");

                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    count_invite += 1;
                }
                if (count_invite>0)
                    viewPagerAdapter.addFragment(new InviteFriendFragment(), "Lời mời kết bạn ("+count_invite+")");
                else
                    viewPagerAdapter.addFragment(new InviteFriendFragment(), "Lời mời kết bạn");

                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);
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
