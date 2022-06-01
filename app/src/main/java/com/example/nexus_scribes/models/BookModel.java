package com.example.nexus_scribes.models;

import com.example.nexus_scribes.firestoreData.UploadBook;
import com.example.nexus_scribes.firestoreData.UploadUser;
import com.example.nexus_scribes.utilities.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

public class BookModel {

    private FirebaseFirestore fb;
    private List<ListenerRegistration> listener;
    private String userId;


    public BookModel() {
        fb = FirebaseFirestore.getInstance();
        listener = new ArrayList<>();
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        userId = myAuth.getCurrentUser().getUid();
    }

    public void getBookInformation(Consumer<DocumentSnapshot> dataChangedCallBack,
                                      Consumer<FirebaseFirestoreException> dataErrorCallback) {
        ListenerRegistration listenerRegistration =
                fb.collection(Constants.KEY_COLLECTIONS_BOOKS)
                        .document(userId)
                        .addSnapshotListener((queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                dataErrorCallback.accept(e);
                            }
                            dataChangedCallBack.accept(queryDocumentSnapshots);
                        });
        listener.add(listenerRegistration);
    }

    public void updateBookById(UploadBook book) {
        DocumentReference bookRef =
                fb.collection(Constants.KEY_COLLECTIONS_BOOKS).document(book.getBookId());
        Map<String, Object> books = new HashMap<>();
        books.put(Constants.KEY_USER_IMAGE, book.getImageProfile());
        books.put(Constants.KEY_FULL_NAME, book.getFullName());
        books.put(Constants.KEY_PEN_NAME, book.getPenName());
        books.put(Constants.KEY_BOOK_IMAGE, book.getBookImage());
        books.put(Constants.KEY_BOOK_TITLE, book.getBookTitle());
        books.put(Constants.KEY_BOOK_SYNOPSIS, book.getBookSynopsis());
        books.put(Constants.KEY_USER_BIO, book.getUserBio());

        bookRef.update(books);
    }
}
