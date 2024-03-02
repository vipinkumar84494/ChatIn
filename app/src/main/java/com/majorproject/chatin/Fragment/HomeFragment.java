package com.majorproject.chatin.Fragment;

import android.app.ProgressDialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.majorproject.chatin.Adapter.PostAdapter;
import com.majorproject.chatin.LoginActivity;
import com.majorproject.chatin.Model.Post;
import com.majorproject.chatin.Model.User;
import com.majorproject.chatin.Model.UserStories;
import com.majorproject.chatin.R;
import com.majorproject.chatin.Adapter.StoryAdapter;
import com.majorproject.chatin.Model.Story;
import com.majorproject.chatin.SettingsActivity;
import com.majorproject.chatin.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<Post> postList;
    PostAdapter adapterPosts;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        firebaseAuth = FirebaseAuth.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        binding.dashboardRV.setLayoutManager(layoutManager);

        postList = new ArrayList<>();
        loadPosts();

        return binding.getRoot();
    }

    private void loadPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post modelPost = ds.getValue(Post.class);
                    postList.add(modelPost);
                    adapterPosts = new PostAdapter(getActivity(), postList);
                    binding.dashboardRV.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPosts(String searchQuery){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post modelPost = ds.getValue(Post.class);
                    if(modelPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase())||modelPost.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(modelPost);
                    }
                    adapterPosts = new PostAdapter(getActivity(), postList);
                    binding.dashboardRV.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

        } else {

        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!TextUtils.isEmpty(s)){
                    searchPosts(s);
                } else{
                    loadPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(!TextUtils.isEmpty(s)){
                    searchPosts(s);
                } else{
                    loadPosts();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        else if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }




        return super.onOptionsItemSelected(item);
    }
}


//
//
//public class HomeFragment extends Fragment {
//
//    FragmentHomeBinding binding;
//    ArrayList<Story> storyList;
//    ArrayList<Post> postList;
//    FirebaseAuth auth;
//    FirebaseDatabase database;
//    FirebaseStorage storage;
//    ActivityResultLauncher<String> galleryLauncher;
//    ProgressDialog dialog;
//
//
//    //
//
//    FirebaseUser user;
//    DatabaseReference databaseReference;
//
//    // storage
//    StorageReference storageReference;
//
//    // arrays of permissions to be requested
//    String[] cameraPermissions;
//    String[] storagePermissions;
//
//    public HomeFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//
//
//        dialog = new ProgressDialog(getContext());
//
//        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//        storage = FirebaseStorage.getInstance();
//
//        user = auth.getCurrentUser();
//        databaseReference = database.getReference("Users");
//        storageReference = FirebaseStorage.getInstance().getReference();
//
//        cameraPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        storagePermissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        dialog.setTitle("Story Uploading");
//        dialog.setMessage("Please wait...");
//        dialog.setCancelable(false);
//
//        binding.dashboardRV.showShimmerAdapter();
//
//        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    User user = snapshot.getValue(User.class);
////                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.placeholder).into(binding.profileImage);
////                    Picasso.get().load(user.getImage()).placeholder(R.drawable.placeholder).into(binding.profileImage);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//        //Story Recycler View
//        storyList = new ArrayList<>();
//        StoryAdapter adapter = new StoryAdapter(storyList, getContext());
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
//        binding.storyRV.setLayoutManager(layoutManager);
//        binding.storyRV.setNestedScrollingEnabled(false);
//        binding.storyRV.setAdapter(adapter);
//
//        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    storyList.clear();
//                    for (DataSnapshot storySnapshot : snapshot.getChildren()) {
//                        Story story = new Story();
//                        story.setStoryBy(storySnapshot.getKey());
//                        story.setStoryAt(storySnapshot.child("postedBy").getValue(Long.class));
//
//                        ArrayList<UserStories> stories = new ArrayList<>();
//                        for (DataSnapshot snapshot1 : storySnapshot.child("userStories").getChildren()) {
//                            UserStories userStories = snapshot1.getValue(UserStories.class);
//                            stories.add(userStories);
//                        }
//                        story.setStories(stories);
//                        storyList.add(story);
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        //Dashboard Recycler View
//        postList = new ArrayList<>();
//
//        PostAdapter postAdapter = new PostAdapter(postList, getContext());
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        binding.dashboardRV.setLayoutManager(linearLayoutManager);
//        binding.dashboardRV.addItemDecoration(new DividerItemDecoration(binding.dashboardRV.getContext(), DividerItemDecoration.VERTICAL));
//        binding.dashboardRV.setNestedScrollingEnabled(false);
//
//        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Post post = dataSnapshot.getValue(Post.class);
//                    post.setPostId(dataSnapshot.getKey());
//                    postList.add(post);
//                }
//                binding.dashboardRV.setAdapter(postAdapter);
//                binding.dashboardRV.hideShimmerAdapter();
//                postAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        binding.storyImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                galleryLauncher.launch("image/*");
//            }
//        });
//
//        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
//            @Override
//            public void onActivityResult(Uri result) {
//                if (result != null) {
////                    Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
////                } else {
//                    binding.storyImg.setImageURI(result);
//
//
//                    dialog.show();
//                    final StorageReference reference = storage.getReference().child("stories").child(FirebaseAuth.getInstance().getUid()).child(new Date().getTime() + "");
//                    reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    Story story = new Story();
//                                    story.setStoryAt(new Date().getTime());
//
//                                    database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid()).child("postedBy").setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            UserStories stories = new UserStories(uri.toString(), story.getStoryAt());
//                                            database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid()).child("userStories").push().setValue(stories).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void unused) {
//                                                    dialog.dismiss();
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        });
//        return binding.getRoot();
//    }
//
//    private void checkUserStatus() {
//        FirebaseUser user = auth.getCurrentUser();
//        if (user != null) {
//        } else {
//            startActivity(new Intent(getActivity(), LoginActivity.class));
//            getActivity().finish();
//        }
//    }
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_main, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_logout) {
//            auth.signOut();
//            checkUserStatus();
//        }
//
//
//        return super.onOptionsItemSelected(item);
//    }
//
//
//}