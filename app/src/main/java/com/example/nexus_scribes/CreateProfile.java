package com.example.nexus_scribes;

import static com.example.nexus_scribes.R.color.dark_mod_blue;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nexus_scribes.databinding.CreateProfileBinding;
import com.example.nexus_scribes.utilities.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


public class CreateProfile extends AppCompatActivity {

    private CreateProfileBinding binding;
    private String encodedImage;

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

        binding.profileImage.setOnClickListener(view -> {
            Intent intent = (new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

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
                        String userID = Objects.requireNonNull(myAuth.getCurrentUser()).getUid();
                        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTIONS_USERS)
                                .document(userID);
                        HashMap<String, Object> user = new HashMap<>();
                        user.put(Constants.KEY_FULL_NAME, binding.etFullName.getText().toString());
                        user.put(Constants.KEY_EMAIL, binding.etEmail.getText().toString());
                        user.put(Constants.KEY_USER_AGE, binding.ageDisplay.getText().toString());
                        user.put(Constants.KEY_PASSWORD, binding.etPassword.getText().toString());
                        user.put(Constants.KEY_USER_BIO, binding.etBio.getText().toString());
                        user.put(Constants.KEY_PEN_NAME, binding.etPenName.getText().toString());
                        user.put(Constants.KEY_TWITTER_URL, binding.etTwitter.getText().toString());
                        user.put(Constants.KEY_FACEBOOK_URL, binding.etFacebook.getText().toString());
                        user.put(Constants.KEY_INSTAGRAM_URL, binding.etInstagram.getText().toString());
                        user.put(Constants.KEY_USER_IMAGE, encodedImage);
                        documentReference.set(user);
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                    } else {
                        showToast("Error: This User already has an account");
                        loading(false);
                    }
                });
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

    private String encodedImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUrl = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUrl);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.profileImage.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodedImage(bitmap);
                        } catch (FileNotFoundException err) {
                            err.printStackTrace();
                        }
                    }
                }
            }
    );

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
        if (encodedImage == null) {
            binding.imageError.setVisibility(View.VISIBLE);
            return false;
        }
        else if (binding.etFullName.length() == 0) {
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