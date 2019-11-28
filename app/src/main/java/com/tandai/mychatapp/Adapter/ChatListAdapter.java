package com.tandai.mychatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tandai.mychatapp.MessageActivity;
import com.tandai.mychatapp.Model.Chat;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.User;
import com.tandai.mychatapp.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>{
    ArrayList<Chat> arrChat;
    Context context;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");

    public ChatListAdapter(ArrayList<Chat> arrChat,  Context context) {
        this.arrChat = arrChat;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_list_chats,viewGroup,false);

        return new ChatListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListAdapter.ViewHolder viewHolder, int i) {
        final Chat chat = arrChat.get(i);

        String checkUserID = (chat.getSender().equals(Common.currUserID))? chat.getReceiver() : chat.getSender();

        mDatabase.child(checkUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                viewHolder.name.setText(user.getName());
                if(user.getImage().equals("") == false){
                    Picasso.with(context).load(user.getImage()).into(viewHolder.image);
                }
                if(chat.getSender().equals(Common.currUserID)){
                    viewHolder.last_chat.setText("Bạn: "+chat.getMessage());
                }
                else {
                    viewHolder.last_chat.setText(chat.getMessage());
                }

                viewHolder.time.setText(chat.getTime());

                if(user.getStatus().equals("online"))
                    viewHolder.is_online.setVisibility(View.VISIBLE);
                else
                    viewHolder.is_online.setVisibility(View.GONE);


                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, MessageActivity.class);
                        intent.putExtra("username_receiver",user.getUserID());
                        intent.putExtra("image_receiver",user.getImage());
                        viewHolder.itemView.getContext().startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public int getItemCount() {
        return arrChat.size();
    }



    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView name ,last_chat,time;
        CircleImageView image;
        ImageView is_online;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name =  itemView.findViewById(R.id.item_chat_name);
            image = itemView.findViewById(R.id.item_chat_image);
            is_online = itemView.findViewById(R.id.item_chat_isonline);
            last_chat =  itemView.findViewById(R.id.item_chat_last_chat);
            time = itemView.findViewById(R.id.item_chat_time);
        }
    }
}
