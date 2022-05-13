package com.example.nexus_scribes.models;

import com.example.nexus_scribes.UploadUser;
import com.example.nexus_scribes.utilities.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class  UserProfileModel {
    private FirebaseFirestore fb;
    private List<ListenerRegistration> listener;
    private String userId;

    public UserProfileModel() {
        fb = FirebaseFirestore.getInstance();
        listener = new ArrayList<>();
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        userId = myAuth.getCurrentUser().getUid();
    }

    public void getProfileInformation(Consumer<DocumentSnapshot> dataChangedCallBack,
                               Consumer<FirebaseFirestoreException> dataErrorCallback){
        ListenerRegistration listenerRegistration =
                fb.collection(Constants.KEY_COLLECTIONS_USERS).document(userId)
                        .addSnapshotListener((queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                dataErrorCallback.accept(e);
                            }
                            dataChangedCallBack.accept(queryDocumentSnapshots);
                        });
        listener.add(listenerRegistration);
    }

    public void updateProfileById(UploadUser profile) {
        DocumentReference profileRef =
                fb.collection(Constants.KEY_COLLECTIONS_USERS).document(profile.getUserId());
        Map<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_USER_IMAGE, profile.getImageProfile());
        user.put(Constants.KEY_FULL_NAME, profile.getFullName());
        user.put(Constants.KEY_PEN_NAME, profile.getPenName());
        user.put(Constants.KEY_USER_BIO, profile.getUserBio());
        user.put(Constants.KEY_EMAIL, profile.getEmail());
        user.put(Constants.KEY_PASSWORD, profile.getPassword());
        user.put(Constants.KEY_USER_AGE, profile.getUserAge());
        user.put(Constants.KEY_FACEBOOK_URL, profile.getFacebookUrl());
        user.put(Constants.KEY_TWITTER_URL, profile.getTwitterUrl());
        user.put(Constants.KEY_INSTAGRAM_URL, profile.getInstagramUrl());
        profileRef.update(user);
    }

    public void clear() {
        listener.forEach(ListenerRegistration::remove);
    }
}
