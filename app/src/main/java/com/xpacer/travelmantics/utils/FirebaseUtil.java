package com.xpacer.travelmantics.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.xpacer.travelmantics.ListActivity;
import com.xpacer.travelmantics.models.TravelDeal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    private static final int RC_SIGN_IN = 1253;
    public static ArrayList<TravelDeal> mDeals;
    private static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    private static FirebaseUtil mFirebaseUtil;
    private static ListActivity caller;
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageReference;

    public static boolean isAdmin;

    private FirebaseUtil() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public static void openReference(String ref, final ListActivity callerActivity) {
        if (mFirebaseUtil == null) {
            mFirebaseUtil = new FirebaseUtil();
        }
        caller = callerActivity;
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    FirebaseUtil.signIn();
                } else {
                    String userId = firebaseAuth.getUid();
                    checkAdmin(userId);
                }
                Toast.makeText(callerActivity, "Welcome Back!", Toast.LENGTH_SHORT).show();
            }
        };

        mDeals = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference(ref);
        connectToStorage();
    }

    private static void checkAdmin(String userId) {
        FirebaseUtil.isAdmin = false;
        DatabaseReference adminRef = mFirebaseDatabase.getReference().child("administrators")
                .child(userId);

        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        adminRef.addChildEventListener(eventListener);

    }

    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    private static void signIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private static void connectToStorage() {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("deals_pictures");
    }
}
