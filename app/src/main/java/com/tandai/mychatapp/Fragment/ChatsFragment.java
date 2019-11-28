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
import com.tandai.mychatapp.Adapter.AvatarFriendAdapter;
import com.tandai.mychatapp.Adapter.ChatListAdapter;
import com.tandai.mychatapp.Adapter.FriendAdapter;
import com.tandai.mychatapp.Adapter.MessageAdapter;
import com.tandai.mychatapp.MessageActivity;
import com.tandai.mychatapp.Model.Chat;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.User;
import com.tandai.mychatapp.R;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    RecyclerView recyclerViewAvatar;
    TextView no_friend,no_chat;
    TextView loading_avatar,loading_chat;
    AvatarFriendAdapter avatarFriendAdapter;
    ArrayList<User> arrayList = new ArrayList<>();


    RecyclerView recyclerViewChat;
    ChatListAdapter chatListAdapter;
    ArrayList<Chat> arrChat = new ArrayList<>();

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        no_friend= view.findViewById(R.id.NoAnyFriends_fragment_chats);
        no_chat = view.findViewById(R.id.NoAnyConversations_fragment_chats);
        loading_avatar = view.findViewById(R.id.loading_avatar_fragment_chats);
        loading_chat = view.findViewById(R.id.loading_chat_fragment_chats);

        loading_avatar.setVisibility(View.VISIBLE);
        recyclerViewAvatar = view.findViewById(R.id.recyclerHorizontalAvatar_fragment_chats);
        recyclerViewAvatar.setHasFixedSize(true);
        recyclerViewAvatar.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));


        loading_chat.setVisibility(View.VISIBLE);
        recyclerViewChat = view.findViewById(R.id.recyclerMessages_fragment_chats);
        recyclerViewChat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewChat.setLayoutManager(layoutManager);
        //set divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewChat.getContext(),
                layoutManager.getOrientation());
        recyclerViewChat.addItemDecoration(dividerItemDecoration);


        getData();
        return view;
    }

    private void getData() {
        mDatabase.child("Friends").child(Common.currUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    arrayList.add(user);
                }
                loading_avatar.setVisibility(View.GONE);
                avatarFriendAdapter = new AvatarFriendAdapter(arrayList,getContext());
                recyclerViewAvatar.setAdapter(avatarFriendAdapter);
                if (arrayList.size() == 0)
                    no_friend.setVisibility(View.VISIBLE);

                //get data chat
                mDatabase.child("Chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        arrChat.clear();
                        for(DataSnapshot ds1 : dataSnapshot.getChildren()){
                            Chat chat = ds1.getValue(Chat.class);
                            if (chat.getReceiver().equals(Common.currUserID)  || chat.getSender().equals(Common.currUserID)){
                                arrChat.add(chat);
                                for(int i=0;i<arrChat.size()-1;i++){
                                    if((arrChat.get(i).getSender().equals(chat.getSender()) && arrChat.get(i).getReceiver().equals(chat.getReceiver()))
                                            || (arrChat.get(i).getSender().equals(chat.getReceiver()) && arrChat.get(i).getReceiver().equals(chat.getSender()))){
                                        arrChat.remove(i);
                                    }
                                }
                            }
                        }
                        // new message to head
                        arrChat = Common.reverseList(arrChat);
                        loading_chat.setVisibility(View.GONE);
                        if (arrChat.size() == 0)
                            no_chat.setVisibility(View.VISIBLE);
                        else{
                            chatListAdapter = new ChatListAdapter(arrChat,getContext());
                            recyclerViewChat.setAdapter(chatListAdapter);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
