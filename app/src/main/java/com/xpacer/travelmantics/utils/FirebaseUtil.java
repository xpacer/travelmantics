package com.xpacer.travelmantics.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xpacer.travelmantics.models.TravelDeal;

import java.util.ArrayList;

public class FirebaseUtil {
    public static ArrayList<TravelDeal> travelDeals;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil mFirebaseUtils;

    private FirebaseUtil() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    public static void openReference(String ref) {
        if (mFirebaseUtils == null) {

        }
    }
}
