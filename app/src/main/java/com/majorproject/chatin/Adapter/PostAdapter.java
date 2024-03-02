package com.majorproject.chatin.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.majorproject.chatin.CommentActivity;
import com.majorproject.chatin.Fragment.AddPostFragment;
import com.majorproject.chatin.Model.Notification;
import com.majorproject.chatin.Model.Post;
import com.majorproject.chatin.Model.User;
import com.majorproject.chatin.PostDetailActivity;
import com.majorproject.chatin.PostLikedByActivity;
import com.majorproject.chatin.R;
import com.majorproject.chatin.ThereProfileActivity;
import com.majorproject.chatin.databinding.DashboardRvDesignBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    Context context;
    List<Post> postList;
    String myUid;

    private DatabaseReference likesRef;
    private DatabaseReference postsRef;
    boolean mProcessLike = false;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv_design, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        final String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uProfession = postList.get(position).getuProfession();
        String uDp = postList.get(position).getuDp();
        final String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescr();
        final String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();
        String pLikes = postList.get(position).getpLikes();
        String pComments = postList.get(position).getpComments();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        // set data
        holder.binding.uNameTv.setText(uName);
        holder.binding.uProfessionTv.setText(uProfession);
        holder.binding.pTimeTv.setText(pTime);
        holder.binding.pTitleTv.setText(pTitle);
        holder.binding.pDescriptionTv.setText(pDescription);
        holder.binding.pLikesTv.setText(pLikes + " Likes");
        holder.binding.pCommentsTv.setText(pComments + " Comments");
        setLikes(holder, pId);

        try {
            Picasso.get().load(uDp).placeholder(R.drawable.placeholder).into(holder.binding.uPictureIv);
        } catch (Exception e) {

        }

        if (pImage.equals("noImage")) {
            holder.binding.pImageIv.setVisibility(View.GONE);
        } else {
            holder.binding.pImageIv.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(pImage).placeholder(R.drawable.placeholder).into(holder.binding.pImageIv);
            } catch (Exception e) {

            }
        }


        holder.binding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreOptions(holder.binding.moreBtn, uid, myUid, pId, pImage);
            }
        });

        holder.binding.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int pLikes = Integer.parseInt(postList.get(position).getpLikes());
                mProcessLike = true;
                final String postIde = postList.get(position).getpId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mProcessLike) {
                            if (snapshot.child(postIde).hasChild(myUid)) {
                                postsRef.child(postIde).child("pLikes").setValue("" + (pLikes - 1));
                                likesRef.child(postIde).child(myUid).removeValue();
                                mProcessLike = false;
                            } else {
                                postsRef.child(postIde).child("pLikes").setValue("" + (pLikes + 1));
                                likesRef.child(postIde).child(myUid).setValue("Liked");
                                mProcessLike = false;

                                addToHisNotifications(""+uid,""+pId,"Liked your post");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.binding.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });

        holder.binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.binding.pImageIv.getDrawable();
                if(bitmapDrawable==null){
                    shareTextOnly(pTitle,pDescription);
                } else{
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(pTitle,pDescription,bitmap);
                }
            }
        });

        holder.binding.uNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            }
        });

        holder.binding.pLikesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostLikedByActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });

    }

    private void addToHisNotifications(String hisUid, String pId, String notification) {
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId",pId);
        hashMap.put("timestamp",timestamp);
        hashMap.put("pUid",pId);
        hashMap.put("notification",notification);
        hashMap.put("sUid",myUid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void shareTextOnly(String pTitle, String pDescription) {
        String shareBody = pTitle+"\n"+pDescription;
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sIntent, "Share Via"));
    }

    private void shareImageAndText(String pTitle, String pDescription, Bitmap bitmap) {
        String shareBody = pTitle+"\n"+pDescription;
        Uri uri = saveImageToShare(bitmap);
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sIntent.setType("image/png");
        context.startActivity(Intent.createChooser(sIntent, "Share Via"));
    }

        private Uri saveImageToShare(Bitmap bitmap){
            File imageFolder = new File(context.getCacheDir(),"images");
            Uri uri = null;
            try{
                imageFolder.mkdirs();
                File file = new File(imageFolder,"shared_image.png");
                FileOutputStream stream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
                stream.flush();
                stream.close();
                uri = FileProvider.getUriForFile(context,"com.majorproject.chatin.fileprovider",file);
            } catch(Exception e){
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return uri;
        }
    private void setLikes(final viewHolder holder, final String postKey) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(myUid)) {
                    holder.binding.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_red, 0, 0, 0);
                    holder.binding.likeBtn.setText("Liked");
                } else {
                    holder.binding.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                    holder.binding.likeBtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void showMoreOptions(ImageView moreBtn, String uid, String myUid, final String pId, String pImage) {
        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);
        if (uid.equals(myUid)) {
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
        }
        popupMenu.getMenu().add(Menu.NONE, 1, 0, "View Detail");

        popupMenu.setOnMenuItemClickListener((menuItem) -> {
            int id = menuItem.getItemId();
            if (id == 0) {
                beginDelete(pId, pImage);
            } else if(id==1){
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
            return false;
        });
        popupMenu.show();
    }

    private void beginDelete(String pId, String pImage) {
        if (pImage.equals("noImage")) {
            deleteWithoutImage(pId);
        } else {
            deleteWithImage(pId, pImage);
        }
    }

    private void deleteWithImage(String pId, String pImage) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteWithoutImage(String pId) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        DashboardRvDesignBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DashboardRvDesignBinding.bind(itemView);
        }
    }

}


//public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {
//
//    ArrayList<Post> list;
//    Context context;
//
//    public PostAdapter(ArrayList<Post> list, Context context) {
//        this.list = list;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv_design, parent, false);
//        return new viewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
//        Post model = list.get(position);
//        Picasso.get().load(model.getPostImage()).placeholder(R.drawable.placeholder).into(holder.binding.postImage);
//        holder.binding.like.setText(model.getPostLike() + "");
//        holder.binding.comment.setText(model.getCommentCount() + "");
//        String description = model.getPostDescription();
//        if (description.equals("")) {
//            holder.binding.postDescription.setVisibility(View.GONE);
//        } else {
//            holder.binding.postDescription.setText(model.getPostDescription());
//            holder.binding.postDescription.setVisibility(View.VISIBLE);
//        }
//
//        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
////                Picasso.get().load(user.getProfile()).placeholder(R.drawable.placeholder).into(holder.binding.profileImage);
//                Picasso.get().load(user.getImage()).placeholder(R.drawable.placeholder).into(holder.binding.profileImage);
//                holder.binding.userName.setText(user.getName());
//                holder.binding.bio.setText(user.getProfession());
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        FirebaseDatabase.getInstance().getReference().child("posts").child(model.getPostId()).child("likes").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_heart, 0, 0, 0);
//                } else {
//                    holder.binding.like.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            FirebaseDatabase.getInstance().getReference().child("posts").child(model.getPostId()).child("likes").child(FirebaseAuth.getInstance().getUid()).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    FirebaseDatabase.getInstance().getReference().child("posts").child(model.getPostId()).child("postLike").setValue(model.getPostLike() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_heart, 0, 0, 0);
//
//                                            Notification notification = new Notification();
//                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
//                                            notification.setNotificationAt(new Date().getTime());
//                                            notification.setPostID(model.getPostId());
//                                            notification.setPostedBy(model.getPostedBy());
//                                            notification.setType("like");
//                                            FirebaseDatabase.getInstance().getReference().child("notification").child(model.getPostedBy()).push().setValue(notification);
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        holder.binding.comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, CommentActivity.class);
//                intent.putExtra("postId", model.getPostId());
//                intent.putExtra("postedBy", model.getPostedBy());
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//            }
//        });
//
//        holder.binding.share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.binding.postImage.getDrawable();
//                Bitmap bitmap = bitmapDrawable.getBitmap();
//                Uri uri = saveImageToShare(bitmap);
//                Intent sIntent = new Intent(Intent.ACTION_SEND);
//                sIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                sIntent.putExtra(Intent.EXTRA_TEXT, holder.binding.postDescription.toString());
//                sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
//                sIntent.setType("image/png");
//                context.startActivity(Intent.createChooser(sIntent, "Shaare Via"));
//            }
//        });
//
//        holder.binding.moreIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseDatabase.getInstance().getReference("posts").child(model.getPostId()).removeValue();
//            }
//        });
//    }
//
//    private Uri saveImageToShare(Bitmap bitmap) {
//        File imageFolder = new File(context.getCacheDir(), "images");
//        Uri uri = null;
//        try {
//            imageFolder.mkdirs();
//            File file = new File(imageFolder, "shared_image.png");
//
//            FileOutputStream stream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
//            stream.flush();
//            stream.close();
//            uri = FileProvider.getUriForFile(context, "com.majorproject.chatin.fileprovider", file);
//        } catch (Exception e) {
//            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        return uri;
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public class viewHolder extends RecyclerView.ViewHolder {
//
//        DashboardRvDesignBinding binding;
//
//        public viewHolder(@NonNull View itemView) {
//            super(itemView);
//            binding = DashboardRvDesignBinding.bind(itemView);
//        }
//    }
//}
