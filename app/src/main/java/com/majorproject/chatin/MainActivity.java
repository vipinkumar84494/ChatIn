package com.majorproject.chatin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.trusted.Token;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.majorproject.chatin.Fragment.AddPostFragment;
import com.majorproject.chatin.Fragment.ChatListFragment;
import com.majorproject.chatin.Fragment.HomeFragment;
import com.majorproject.chatin.Fragment.NotificationFrag;
import com.majorproject.chatin.Fragment.Profile2Fragment;
import com.majorproject.chatin.Fragment.SearchFragment;
import com.majorproject.chatin.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    // for checking profile or cover photo
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    ProgressDialog pd;
    // permissions constants

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();


        pd = new ProgressDialog(this);

        //Hide Toolbar manually
        //getSupportActionBar().hide();

        setSupportActionBar(binding.toolbar);
        MainActivity.this.setTitle("My Profile");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        binding.toolbar.setVisibility(View.GONE);
        transaction.replace(R.id.content, new HomeFragment());
        transaction.commit();


        binding.readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (i) {
                    case 0:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.content, new HomeFragment());
                        break;
                    case 1:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.content, new NotificationFrag());
                        break;
                    case 2:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.content, new AddPostFragment());
                        break;
                    case 3:
                        binding.toolbar.setVisibility(View.VISIBLE);
                        binding.toolbar.setBackgroundColor(getResources().getColor(R.color.derkGrey));
                        binding.toolbar.setTitle("Search");
                        transaction.replace(R.id.content, new SearchFragment());
                        break;
                    case 4:
                        binding.toolbar.setVisibility(View.VISIBLE);
                        binding.toolbar.setTitle("My Profile");
                        transaction.replace(R.id.content, new Profile2Fragment());
                        break;
                    case 5:
                        binding.toolbar.setVisibility(View.VISIBLE);
                        binding.toolbar.setTitle("Chats");
                        transaction.replace(R.id.content, new ChatListFragment());
                        break;
                }
                transaction.commit();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserStatus() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
//            mUID = user.getUid();
//
//            SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString("Current_USERID", mUID);
//            editor.apply();
//
//            updateToken(FirebaseMessaging.getInstance().getToken());
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

//    public void updateToken(String token){
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token mToken = new Token(token);
//        ref.child(mUID).setValue(mToken);
//    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }
}