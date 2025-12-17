package com.example.laboratoriumcomputer.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.laboratoriumcomputer.R;
import com.example.laboratoriumcomputer.models.Equipment;
import com.example.laboratoriumcomputer.models.History;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HistoryNotificationService extends Service {

    private static final String TAG = "HistoryService";
    private static final String CHANNEL_ID = "history_update_channel";
    private static final int NOTIFICATION_ID = 1;

    private ChildEventListener childEventListener;
    private DatabaseReference historyRef;
    private DatabaseReference equipmentRef;
    private String lastKnownKey;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        historyRef = FirebaseDatabase.getInstance().getReference("history");
        equipmentRef = FirebaseDatabase.getInstance().getReference("equipment");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("LabNLoan Service")
                .setContentText("Listening for history updates.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        startForeground(NOTIFICATION_ID, notification);

        listenForHistoryUpdates();

        return START_STICKY;
    }

    private void listenForHistoryUpdates() {
        Query lastKeyQuery = historyRef.orderByKey().limitToLast(1);
        lastKeyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    lastKnownKey = childSnapshot.getKey();
                }
                attachChildEventListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to get last key", error.toException());
            }
        });
    }

    private void attachChildEventListener() {
        if (childEventListener != null) {
            historyRef.removeEventListener(childEventListener);
        }

        Query query = lastKnownKey != null ? historyRef.orderByKey().startAfter(lastKnownKey) : historyRef.orderByKey();

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                lastKnownKey = snapshot.getKey();
                History history = snapshot.getValue(History.class);
                if (history != null) {
                    fetchEquipmentAndNotify(history);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Child event listener cancelled", error.toException());
            }
        };
        query.addChildEventListener(childEventListener);
    }

    private void fetchEquipmentAndNotify(History history) {
        if (history.getSerialNumber() == null || history.getSerialNumber().isEmpty()) return;

        equipmentRef.child(history.getSerialNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Equipment equipment = snapshot.getValue(Equipment.class);
                sendNotification(history, equipment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch equipment details", error.toException());
                // Send notification with partial data if equipment lookup fails
                sendNotification(history, null);
            }
        });
    }

    private void sendNotification(History history, @Nullable Equipment equipment) {
        String title = "New Notification: " + history.getBorrowReturn();
        String body;

        if (equipment != null) {
            // Format: borrowerName has borrowed/returned serialNumber name, type
            body = history.getBorrowerName() + " has " + history.getBorrowReturn().toLowerCase() + " " +
                   history.getSerialNumber() + " " + equipment.getName() + ", " + equipment.getType();
        } else {
            // Fallback if equipment details could not be found
            body = history.getBorrowerName() + " has " + history.getBorrowReturn().toLowerCase() + " equipment " + history.getSerialNumber();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) 
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body)) // Allows for longer text
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "History Updates";
            String description = "Notifications for new borrowing/returning history";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (childEventListener != null) {
            historyRef.removeEventListener(childEventListener);
        }
    }
}
