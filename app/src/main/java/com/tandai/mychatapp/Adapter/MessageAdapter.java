package com.tandai.mychatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tandai.mychatapp.Model.Chat;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.User;
import com.tandai.mychatapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private ArrayList<Chat> mChat;
    private String imageSender;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");

    public MessageAdapter(Context mContext, ArrayList<Chat> mChat, String imageurl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageSender = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder viewHolder, int position) {
        final Chat chat = mChat.get(position);


        viewHolder.show_message.setText(chat.getMessage());

//        if(chat.getType()== 1){
//            viewHolder.show_message.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
//        }

        if(chat.getSender().equals(Common.currUserID) ) {
            if (position == mChat.size() - 1) {
                if (chat.getIsSeen()) {
                    viewHolder.is_seen.setText("Đã xem");
                } else {
                    viewHolder.is_seen.setText("Đã gửi");

                }
            }
            else {
                viewHolder.is_seen.setVisibility(View.GONE);
            }
        }


        if(imageSender.equals("") ==  false && chat.getSender().equals(Common.currUserID) == false){
            Picasso.with(mContext).load(imageSender).into(viewHolder.profile_image);
            mDatabase.child(chat.getSender()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    if(user.getStatus().equals("online"))
                        viewHolder.is_online.setVisibility(View.VISIBLE);
                    else
                        viewHolder.is_online.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(chat.getType() == 1 ){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getLinkFile()));
                viewHolder.itemView.getContext().startActivity(intent);
            }

            }
        });




    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        TextView show_message;
        ImageView is_online, check_send;
        CircleImageView profile_image, check_send_file;
        TextView is_seen;

        public ViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.image_item_chats);
            is_online = itemView.findViewById(R.id.item_mess_is_online);
            is_seen = itemView.findViewById(R.id.item_mess_is_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mChat.get(position).getSender().equals(Common.currUserID)){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

}
