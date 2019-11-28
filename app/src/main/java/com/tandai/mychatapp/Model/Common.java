package com.tandai.mychatapp.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Common {
    public static String currUserID ="";

    public static boolean containsIgnoreCase(String str, String subStr) {
        return str.toLowerCase().contains(subStr.toLowerCase());
    }

    public static<T> ArrayList<T> reverseList(ArrayList<T> list)
    {
        ArrayList<T> reverse = new ArrayList<>(list);
        Collections.reverse(reverse);
        return reverse;
    }
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static void Status(final String status){
//        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends");
//        Query query = reference.orderByChild(Common.currUserID+"/userID").equalTo(Common.currUserID);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                    ds.child(Common.currUserID).child("status").getRef().setValue(status);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        final  DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(Common.currUserID).child("status").setValue(status);

    }

}
