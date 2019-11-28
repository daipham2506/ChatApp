package com.tandai.mychatapp.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tandai.mychatapp.Adapter.FriendAdapter;
import com.tandai.mychatapp.Adapter.InviteFriendAdapter;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.MakeFriend;
import com.tandai.mychatapp.Model.User;
import com.tandai.mychatapp.R;

import java.util.ArrayList;


public class FriendFragment extends Fragment {

    RecyclerView recyclerView;
    TextView no_friend;
    FriendAdapter friendAdapter;
    ArrayList<User> arrayList = new ArrayList<>();
    int count = 0;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Friends");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        no_friend= view.findViewById(R.id.NoFriend_friend);

        recyclerView = view.findViewById(R.id.recyler_friend);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //set divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        getData();
        return view;
    }

    private void getData() {
        mDatabase.child(Common.currUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    count += 1;
                    User user = ds.getValue(User.class);
                    arrayList.add(user);
                }
                arrayList = Common.reverseList(arrayList);
                friendAdapter = new FriendAdapter(arrayList,getContext());
                recyclerView.setAdapter(friendAdapter);
                if (count == 0)
                    no_friend.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
