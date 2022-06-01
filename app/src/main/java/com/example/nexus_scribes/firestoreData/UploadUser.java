package com.example.nexus_scribes.firestoreData;


import java.io.Serializable;

public class UploadUser implements Serializable {

    private String UserId;
    public String fullName;
    public String userBio;
    public String imageProfile;
    public String penName;
    private String email;
    private String password;
    private String userAge;
    private String facebookUrl;
    private String twitterUrl;
    private String instagramUrl;

    public UploadUser() {
        // empty constructor
    }

    public UploadUser(String UserId, String fullName, String userBio, String penName,
                      String imageProfile, String email, String password, String userAge,
                      String facebookUrl, String twitterUrl, String instagramUrl) {
        this.UserId = UserId;
        this.fullName = fullName;
        this.userBio = userBio;
        this.imageProfile = imageProfile;
        this.penName = penName;
        this.email = email;
        this.password = password;
        this.userAge = userAge;
        this.facebookUrl = facebookUrl;
        this.twitterUrl = twitterUrl;
        this.instagramUrl = instagramUrl;
    }

    public String getUserId() {
        return UserId;
    }
    public void setUserId(String userId) {UserId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) {this.fullName = fullName;}

    public String getPenName() { return penName; }
    public void setPenName(String penName) {this.penName = penName;}

    public String getUserBio() { return userBio; }
    public void setUserBio(String userBio) {this.userBio = userBio;}

    public String getImageProfile() { return imageProfile; }
    public void setImageProfile(String imageProfile) {this.imageProfile = imageProfile;}

    public String getEmail() { return email; }
    public void setEmail(String email) {this.email = email;}

    public String getPassword() { return password; }
    public void setPassword(String password) {this.password = password;}

    public String getUserAge() { return userAge; }
    public void setUserAge(String userAge) {this.userAge = userAge; }

    public String getFacebookUrl() { return facebookUrl; }
    public void setFacebookUrl(String facebookUrl) {this.facebookUrl = facebookUrl; }

    public String getTwitterUrl() { return twitterUrl; }
    public void setTwitterUrl(String twitterUrl) {this.twitterUrl = twitterUrl; }

    public String getInstagramUrl() { return instagramUrl; }
    public void setInstagramUrl(String instagramUrl) {this.instagramUrl = instagramUrl;}

}
