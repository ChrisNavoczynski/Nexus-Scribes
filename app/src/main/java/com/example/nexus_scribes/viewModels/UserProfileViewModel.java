package com.example.nexus_scribes.viewModels;

import com.example.nexus_scribes.firestoreData.UploadUser;
import com.example.nexus_scribes.models.UserProfileModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.function.Consumer;

public class UserProfileViewModel {
    private final UserProfileModel model;

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

    public void updateProfile(UploadUser profile) {
        model.updateProfileById(profile);
    }

    public void clear() { model.clear(); }

}
