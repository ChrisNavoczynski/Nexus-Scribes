package com.example.nexus_scribes;

import com.example.nexus_scribes.firestore.UploadUser;
import com.example.nexus_scribes.models.UserProfileModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.function.Consumer;

public class UserProfileViewModel {
    private UserProfileModel model;

    public UserProfileViewModel() {
        model = new UserProfileModel();
    }

    public void getProfileInfo(Consumer<UploadUser> responseCallback) {
        model.getProfileInformation(
                (DocumentSnapshot queryDocumentSnapshot) -> {
                    if (queryDocumentSnapshot != null) {
                        UploadUser profileInfo = queryDocumentSnapshot.toObject(UploadUser.class);
                        responseCallback.accept(profileInfo);
                    }
                },
                (databaseError -> System.out.println("Error reading profile information: "
                        + databaseError))
        );
    }
}
