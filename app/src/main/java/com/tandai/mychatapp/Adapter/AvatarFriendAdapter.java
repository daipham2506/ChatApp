package com.tandai.mychatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.User;
import com.tandai.mychatapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AvatarFriendAdapter extends RecyclerView.Adapter<AvatarFriendAdapter.ViewHolder> {
    ArrayList<User> arrUser;
    Context context;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");

    public AvatarFriendAdapter(ArrayList<User> arrUser,Context context) {
        this.arrUser = arrUser;
        this.context = context;
    }

    @NonNull
    @Override
    public AvatarFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_horizontal_avatar,viewGroup,false);

        return new AvatarFriendAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AvatarFriendAdapter.ViewHolder viewHolder, int i) {
        final User user = arrUser.get(i);
        viewHolder.name.setText(user.getName());
        if(user.getImage().equals("") == false) {
            Picasso.with(context).load(user.getImage()).into(viewHolder.image);
        }
        mDatabase.child(user.getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user1 = dataSnapshot.getValue(User.class);

                if(user1.getStatus().equals("online"))
                    viewHolder.is_online.setVisibility(View.VISIBLE);
                else
                    viewHolder.is_online.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
    public int getItemCount() {
        return arrUser.size();
    }



    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView name ;
        CircleImageView image;
        ImageView is_online;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name =  itemView.findViewById(R.id.item_avatar_name);
            image = itemView.findViewById(R.id.item_avatar_image);
            is_online = itemView.findViewById(R.id.item_avatar_isonline);
        }
    }
}
