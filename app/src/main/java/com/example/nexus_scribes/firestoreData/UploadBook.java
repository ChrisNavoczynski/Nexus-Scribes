package com.example.nexus_scribes.firestoreData;

import java.io.Serializable;

public class UploadBook implements Serializable {

    public String bookId;
    public String fullName;
    public String penName;
    public String bookImage;
    public String bookTitle;
    public String bookSynopsis;

    public String imageProfile;
    public String userBio;

    public UploadBook() {
        // Empty Constructor
    }

    public UploadBook(String bookId, String fullName, String penName, String bookImage,
                      String bookTitle, String bookSynopsis, String imageProfile, String userBio) {
        this.bookId = bookId;
        this.fullName = fullName;
        this.penName = penName;
        this.bookImage = bookImage;
        this.bookTitle = bookTitle;
        this.bookSynopsis = bookSynopsis;
        this.imageProfile = imageProfile;
        this.userBio = userBio;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPenName() {
        return penName;
    }

    public void setPenName(String penName) {
        this.penName = penName;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookSynopsis() {
        return bookSynopsis;
    }

    public void setBookSynopsis(String bookSynopsis) {
        this.bookSynopsis = bookSynopsis;
    }

    public String getImageProfile() { return imageProfile; }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }
}
