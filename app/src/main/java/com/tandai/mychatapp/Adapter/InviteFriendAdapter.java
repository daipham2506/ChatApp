package com.tandai.mychatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.tandai.mychatapp.Model.Common;
import com.tandai.mychatapp.Model.MakeFriend;
import com.tandai.mychatapp.Model.User;
import com.tandai.mychatapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class InviteFriendAdapter extends RecyclerView.Adapter<InviteFriendAdapter.ViewHolder>{
    ArrayList<MakeFriend> makeFriendArrayList;
    Context context;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public InviteFriendAdapter(ArrayList<MakeFriend> makeFriendArrayList,Context context) {
        this.makeFriendArrayList = makeFriendArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_invite_friend,viewGroup,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final MakeFriend makeFriend = makeFriendArrayList.get(i);
        mDatabase.child("Users").child(makeFriend.getSender()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                viewHolder.name.setText(user.getName());
                viewHolder.email.setText(user.getEmail());
                if(user.getImage().equals("") == false){
                    Picasso.with(context).load(user.getImage()).into(viewHolder.image);
                }
                final DatabaseReference mDatabase3 = FirebaseDatabase.getInstance().getReference("MakeFriends").child(makeFriend.getReceiver());
                viewHolder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "Đã chấp nhập lời mời", Toast.LENGTH_SHORT).show();
                        final DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference("Friends");
                        mDatabase1.child(makeFriend.getReceiver()).child(makeFriend.getSender()).setValue(user);

                        DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference("Users");
                        mDatabase2.child(makeFriend.getReceiver()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user1 = dataSnapshot.getValue(User.class);
                                mDatabase1.child(makeFriend.getSender()).child(makeFriend.getReceiver()).setValue(user1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        mDatabase3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    MakeFriend makeFriend1 = ds.getValue(MakeFriend.class);
                                    if(makeFriend1.getSender().equals(user.getUserID())){
                                        mDatabase3.child(ds.getKey()).setValue(null);
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });


                viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "Đã gỡ lời mời", Toast.LENGTH_SHORT).show();
                        mDatabase3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    MakeFriend makeFriend1 = ds.getValue(MakeFriend.class);
                                    if(makeFriend1.getSender().equals(user.getUserID())){
                                        mDatabase3.child(ds.getKey()).setValue(null);
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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
        return makeFriendArrayList.size();
    }



    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView name ,email;
        CircleImageView image;
        Button accept, cancel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name =  itemView.findViewById(R.id.item_invite_name);
            image = itemView.findViewById(R.id.item_invite_image);
            email = itemView.findViewById(R.id.item_invite_email);
            accept = itemView.findViewById(R.id.item_invite_accept);
            cancel = itemView.findViewById(R.id.item_invite_cancel);
        }
    }

}
