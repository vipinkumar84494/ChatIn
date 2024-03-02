package com.majorproject.chatin.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.majorproject.chatin.Adapter.NotificationAdapter;
import com.majorproject.chatin.Model.Notification;
import com.majorproject.chatin.R;

import java.util.ArrayList;


public class NotificationFrag extends Fragment {

    RecyclerView notificationsRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Notification> notificationsList;
    private NotificationAdapter adapterNotification;

    public NotificationFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification2, container, false);
        notificationsRv = view.findViewById(R.id.notificationRV);
        firebaseAuth = FirebaseAuth.getInstance();
        getAllNotifications();
        return view;
    }

    private void getAllNotifications() {
        notificationsList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Notification model = ds.getValue(Notification.class);
                    notificationsList.add(model);
                }
                adapterNotification = new NotificationAdapter(getActivity(), notificationsList);
                notificationsRv.setAdapter(adapterNotification);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}