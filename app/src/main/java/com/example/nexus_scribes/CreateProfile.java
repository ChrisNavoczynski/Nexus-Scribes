package com.example.nexus_scribes;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nexus_scribes.databinding.CreateProfileBinding;
import com.example.nexus_scribes.utilities.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class CreateProfile extends AppCompatActivity {

    private CreateProfileBinding binding;
    private StorageReference storageReference;
    private Uri imageProfile;
    private StorageTask uploadTask;

    private boolean
            isAtLeast8 = false,
            hasUpperCase = false,
            hasNumber = false,
            hasSymbol = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Create Profile");
        binding = CreateProfileBinding.inflate(getLayoutInflater());
        storageReference = FirebaseStorage.getInstance().getReference(Constants.KEY_COLLECTIONS_USERS);
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.logInSwitch.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), LogIn_NewProfile.class)));

        binding.datePicker.setOnClickListener(view -> {
            final Calendar myCal = Calendar.getInstance();
            int myYear = myCal.get(Calendar.YEAR);
            int myMonth = myCal.get(Calendar.MONTH);
            int myDay = myCal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                    datePickerListener, myYear, myMonth,myDay);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });

        binding.profileImage.setOnClickListener(view ->
                pickImage.launch("image/*"));

        binding.profileSubmitButton.setOnClickListener(view ->{
            if (isValidProfile()) {
                createProfile();
            }
        });
        inputChange();
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.toast_background);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void createProfile() {
        final String myEmail = binding.etEmail.getText().toString();
        final String myPassword = binding.etPassword.getText().toString();

        loading(true);
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        myAuth.createUserWithEmailAndPassword(myEmail, myPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        showToast("Success! Profile Created!");
                        String userId = Objects.requireNonNull(myAuth.getCurrentUser()).getUid();
                        DocumentReference documentReference = database
                                .collection(Constants.KEY_COLLECTIONS_USERS)
                                .document(userId);
                        StorageReference fileReference = storageReference.child(binding.etFullName.getText().toString()
                            + "ProfilePic" + "." + getFileExtension(imageProfile));
                        uploadTask = fileReference.putFile(imageProfile)
                                .addOnSuccessListener(taskSnapshot ->
                                storageReference.child(binding.etFullName.getText().toString() + "ProfilePic"
                            + "." + getFileExtension(imageProfile)).getDownloadUrl()
                                        .addOnCompleteListener(task2 -> {
                                    Uri downloadUri = task2.getResult();
                                    UploadUser uploadUser =
                                        new UploadUser(
                                                userId.trim(),
                                                binding.etFullName.getText().toString(),
                                                binding.etBio.getText().toString(),
                                                binding.etPenName.getText().toString(),
                                                downloadUri.toString(),
                                                binding.etEmail.getText().toString(),
                                                binding.etPassword.getText().toString(),
                                                binding.ageDisplay.getText().toString(),
                                                binding.etFacebook.getText().toString(),
                                                binding.etTwitter.getText().toString(),
                                                binding.etInstagram.getText().toString()
                                    );
                            documentReference.set(uploadUser);
                        }));
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                    } else {
                        showToast("Error: This User already has an account");
                        loading(false);
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(cR.getType(uri));
    }

    public static boolean IsEmailValid(CharSequence cEmail) {
        return !TextUtils.isEmpty(cEmail) && Patterns.EMAIL_ADDRESS.matcher(cEmail).matches();
    }

    private final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar myCal = Calendar.getInstance();
            myCal.set(Calendar.YEAR, year);
            myCal.set(Calendar.MONTH, month);
            myCal.set(Calendar.DAY_OF_MONTH, day);
            @SuppressLint("SimpleDateFormat") String dateFormat = new SimpleDateFormat("dd MMM yyyy").format(myCal.getTime());
            binding.dobDisplay.setText(dateFormat);
            binding.ageDisplay.setText(Integer.toString(calculateAge(myCal.getTimeInMillis())));
        }
    };

    int calculateAge(long date) {
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if(today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)){
            age--;
        }
        if (age < 18) {
            binding.dobError.setVisibility(View.VISIBLE);
        } else {
            binding.dobError.setVisibility(View.GONE);
        }
        return age;
    }

    ActivityResultLauncher<String> pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        binding.textAddImage.setVisibility(View.GONE);
                        binding.profileImage.setImageURI(result);
                        imageProfile = result;
                    }
                }
            });


    @SuppressLint("ResourceType")
    private void passWordCheck() {
        String password = binding.etPassword.getText().toString();

        // > 8 characters verification
        if (password.length() >= 8) {
            isAtLeast8 = true;
            binding.cardOne.setCardBackgroundColor(Color.parseColor(getString(R.color.dark_pink)));
        } else {
            binding.cardOne.setCardBackgroundColor(Color.parseColor(getString(R.color.light_gray)));
        }

        // Uppercase verification
        if (password.matches(getString(R.string.caps_verify))) {
            hasUpperCase = true;
            binding.cardTwo.setCardBackgroundColor(Color.parseColor(getString(R.color.dark_pink)));
        } else {
            binding.cardTwo.setCardBackgroundColor(Color.parseColor(getString(R.color.light_gray)));
        }

        // Number verification
        if (password.matches(getString(R.string.numbers_verify))) {
            hasNumber = true;
            binding.cardThree.setCardBackgroundColor(Color.parseColor((getString(R.color.dark_pink))));
        } else {
            binding.cardThree.setCardBackgroundColor(Color.parseColor(getString(R.color.light_gray)));
        }

        // Symbol verification
        if (password.matches(getString(R.string.symbols_verify))) {
            hasSymbol = true;
            binding.cardFour.setCardBackgroundColor(Color.parseColor((getString(R.color.dark_pink))));
        } else {
            binding.cardFour.setCardBackgroundColor(Color.parseColor(getString(R.color.light_gray)));
        }

    }

    private Boolean isValidProfile() {
        if (binding.etFullName.length() == 0) {
            binding.fullNameError.setVisibility(View.VISIBLE);
            return false;
        }
        else if (!IsEmailValid(binding.etEmail.getText().toString())) {
            binding.emailError.setVisibility(View.VISIBLE);
            return false;
        }
        else if (binding.etPassword.length() == 0) {
            binding.passwordError.setVisibility(View.VISIBLE);
            return false;
        }
        else if (binding.etBio.length() == 0) {
            binding.bioError.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            binding.imageError.setVisibility(View.GONE);
            binding.fullNameError.setVisibility(View.GONE);
            binding.emailError.setVisibility(View.GONE);
            binding.dobError.setVisibility(View.GONE);
            binding.passwordError.setVisibility(View.GONE);
            binding.bioError.setVisibility(View.GONE);
            return true;
        }
    }

    private void inputChange() {
        binding.etFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passWordCheck();
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passWordCheck();
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passWordCheck();
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void loading(Boolean isLoading) {
        if(isLoading) {
            binding.profileSubmitButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.profileSubmitButton.setVisibility(View.VISIBLE);
        }
    }
}