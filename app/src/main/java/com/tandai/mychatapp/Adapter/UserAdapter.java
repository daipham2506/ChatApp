package com.tandai.mychatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.tandai.mychatapp.MessageActivity;
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.MakeFriend;
import com.tandai.mychatapp.Model.User;
import com.tandai.mychatapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    ArrayList<User> arrUser;
    Context context;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("MakeFriends");

    public UserAdapter(ArrayList<User> arrUser,Context context) {
        this.arrUser = arrUser;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_user,viewGroup,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final User user = arrUser.get(i);
        viewHolder.name.setText(user.getName());
        viewHolder.email.setText(user.getEmail());
        if(user.getImage().equals("") == false){
            Picasso.with(context).load(user.getImage()).into(viewHolder.image);
        }

        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Đã gửi lời mời kết bạn", Toast.LENGTH_SHORT).show();
                final MakeFriend makeFriend = new MakeFriend(Common.currUserID,user.getUserID());
                mDatabase.child(user.getUserID()).push().setValue(makeFriend);
            }
        });

        viewHolder.chat.setOnClickListener(new View.OnClickListener() {
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
        TextView name ,email;
        CircleImageView image;
        ImageView add, chat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name =  itemView.findViewById(R.id.item_user_name);
            image = itemView.findViewById(R.id.item_user_image);
            email = itemView.findViewById(R.id.item_user_email);
            chat = itemView.findViewById(R.id.item_user_chat);
            add = itemView.findViewById(R.id.item_user_add_friend);
        }
    }

}
