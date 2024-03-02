package com.majorproject.chatin;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.majorproject.chatin.Model.User;
import com.majorproject.chatin.databinding.ActivitySignUpBinding;

import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Create Account");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        pd = new ProgressDialog(this);
        pd.setMessage("Registering User...");

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                pd = new ProgressDialog(SignUpActivity.this);
//                pd.setMessage("Please wait...");
                String email = binding.emailET.getText().toString().trim(), password = binding.passwordET.getText().toString().trim(), name = binding.nameET.getText().toString().trim(), profession = binding.professionET.getText().toString().trim();
                if (name.isEmpty()) {
                    binding.nameET.setError("Enter Your Name");
                    binding.nameET.setFocusable(true);
                } else if (profession.isEmpty()) {
                    binding.professionET.setError("Enter Your Profession");
                    binding.professionET.setFocusable(true);
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //invalid email pattern set error
                    binding.emailET.setError("Invalid Email");
                    binding.emailET.setFocusable(true);
                } else if (password.length() < 6) {
                    //set error and focus to password edittext
                    binding.passwordET.setError("Password length at least 6 characters");
                    binding.passwordET.setFocusable(true);
                } else {
                    pd.show();
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                pd.dismiss();
                                FirebaseUser user = auth.getCurrentUser();
                                //Get user email and uid from auth
                                String email = user.getEmail();
                                String uid = user.getUid();
                                // When user is registered, store user info in firebase realtime database
                                // using Hashmap
                                HashMap<Object, String> hashMap = new HashMap<>();
                                // put info in hashmap
                                hashMap.put("email", email);
                                hashMap.put("uid", uid);
                                hashMap.put("name", "");
                                hashMap.put("onlineStatus", "online");
                                hashMap.put("typingTo", "noOne");
                                hashMap.put("profession", "");
                                hashMap.put("phone", "");
                                hashMap.put("image", "");
                                hashMap.put("cover", "");

                                // firebase database instance
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                // path to store user data named "Users"
                                DatabaseReference reference = database.getReference("Users");
                                // put data within hashmap in database
                                reference.child(uid).setValue(hashMap);
//                            User user = new User(binding.nameET.getText().toString(), binding.professionET.getText().toString(), email, password);
//                            String id = task.getResult().getUser().getUid();
//                            database.getReference().child("Users").child(id).setValue(user);
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                finish();
                            } else {
                                pd.dismiss();
                                Toast.makeText(SignUpActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(SignUpActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        binding.goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
//            reload();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
