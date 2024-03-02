package com.majorproject.chatin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.majorproject.chatin.Adapter.CommentAdapter;
import com.majorproject.chatin.Model.Comment;
import com.majorproject.chatin.Model.Notification;
import com.majorproject.chatin.Model.Post;
import com.majorproject.chatin.Model.User;
import com.majorproject.chatin.databinding.ActivityCommentBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    ActivityCommentBinding binding;
    Intent intent;
    String postId;
    String postedBy;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<Comment> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        intent = getIntent();
//        setSupportActionBar(binding.toolbar2);
//        CommentActivity.this.setTitle("Comments");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//
//        postId = intent.getStringExtra("postId");
//        postedBy = intent.getStringExtra("postedBy");
//
////        database.getReference().child("posts").child(postId).addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                Post post = snapshot.getValue(Post.class);
////                Picasso.get().load(post.getPostImage()).placeholder(R.drawable.placeholder).into(binding.postImage);
////                binding.description.setText(post.getPostDescription());
////                binding.like.setText(post.getPostLike() + "");
////                binding.comment.setText(post.getCommentCount() + "");
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////
////            }
////        });
//
//        database.getReference().child("Users").child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
////                Picasso.get().load(user.getProfile()).placeholder(R.drawable.placeholder).into(binding.profileImage);
//                Picasso.get().load(user.getImage()).placeholder(R.drawable.placeholder).into(binding.profileImage);
//                binding.name.setText(user.getName());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        binding.commentPostBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Comment comment = new Comment();
//                comment.setCommentBody(binding.commentET.getText().toString());
//                comment.setCommentedAt(new Date().getTime());
//                comment.setCommentedBy(FirebaseAuth.getInstance().getUid());
//
//                database.getReference().child("posts").child(postId).child("comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        database.getReference().child("posts").child(postId).child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                int commentCount = 0;
//                                if (snapshot.exists()) {
//                                    commentCount = snapshot.getValue(Integer.class);
//                                    database.getReference().child("posts").child(postId).child("commentCount").setValue(commentCount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            binding.commentET.setText("");
//                                            Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(CommentActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                                            Notification notification = new Notification();
//                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
//                                            notification.setNotificationAt(new Date().getTime());
//                                            notification.setPostID(postId);
//                                            notification.setPostedBy(postedBy);
//                                            notification.setType("comment");
//
//                                            FirebaseDatabase.getInstance().getReference().child("notification").child(postedBy).push().setValue(notification);
//
//
//                                        }
//                                    });
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(CommentActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//
//        CommentAdapter adapter = new CommentAdapter(this, list);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        binding.commentRv.setLayoutManager(layoutManager);
//        binding.commentRv.setAdapter(adapter);
//
//        database.getReference().child("posts").child(postId).child("comments").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                list.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Comment comment = dataSnapshot.getValue(Comment.class);
//                    list.add(comment);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        binding.share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.postImage.getDrawable();
//                Bitmap bitmap = bitmapDrawable.getBitmap();
//                Uri uri = saveImageToShare(bitmap);
//                Intent sIntent = new Intent(Intent.ACTION_SEND);
//                sIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                sIntent.putExtra(Intent.EXTRA_TEXT, binding.description.toString());
//                sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
//                sIntent.setType("image/png");
//                startActivity(Intent.createChooser(sIntent, "Shaare Via"));
//            }
//        });
//
//
    }
//
//    private Uri saveImageToShare(Bitmap bitmap) {
//        File imageFolder = new File(this.getCacheDir(), "images");
//        Uri uri = null;
//        try {
//            imageFolder.mkdirs();
//            File file = new File(imageFolder, "shared_image.png");
//
//            FileOutputStream stream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
//            stream.flush();
//            stream.close();
//            uri = FileProvider.getUriForFile(this, "com.majorproject.chatin.fileprovider", file);
//        } catch (Exception e) {
//            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        return uri;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        finish();
//        return super.onOptionsItemSelected(item);
//    }
}