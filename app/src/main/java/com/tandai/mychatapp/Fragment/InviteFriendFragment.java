package com.tandai.mychatapp.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tandai.mychatapp.Adapter.InviteFriendAdapter;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.MakeFriend;
import com.tandai.mychatapp.Model.User;
import com.tandai.mychatapp.R;

import java.util.ArrayList;
import java.util.Collections;


public class InviteFriendFragment extends Fragment {
    RecyclerView recyclerView;
    TextView no_invite;
    InviteFriendAdapter inviteFriendAdapter;
    ArrayList<MakeFriend> arrayList = new ArrayList<>();
    int count = 0;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("MakeFriends");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_invite_friend, container, false);

        no_invite= view.findViewById(R.id.NoInvite_invite);

        recyclerView = view.findViewById(R.id.recyler_invite_friend);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
                    MakeFriend makeFriend = ds.getValue(MakeFriend.class);
                    arrayList.add(makeFriend);
                }
                arrayList = Common.reverseList(arrayList);
                inviteFriendAdapter = new InviteFriendAdapter(arrayList,getContext());
                recyclerView.setAdapter(inviteFriendAdapter);
                if (count == 0)
                    no_invite.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
