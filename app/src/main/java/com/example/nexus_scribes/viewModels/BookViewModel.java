package com.example.nexus_scribes.viewModels;

import com.example.nexus_scribes.firestoreData.UploadBook;
import com.example.nexus_scribes.models.BookModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.function.Consumer;

public class BookViewModel {
    private final BookModel model;

    public BookViewModel() {
        model = new BookModel();
    }

    public void getBookInfo(Consumer<UploadBook> responseCallback) {
        model.getBookInformation(
                (DocumentSnapshot queryDocumentSnapshot) -> {
                    if (queryDocumentSnapshot != null) {
                        UploadBook bookInfo = queryDocumentSnapshot.toObject(UploadBook.class);
                        responseCallback.accept(bookInfo);
                    }
                },
                (databaseError -> System.out.println("Error reading book information: "
                        + databaseError))
        );
    }
}

