package com.mylocalshop.post.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mylocalshop.post.activity.PostProductActivity;
import com.mylocalshop.R;

/**
 * Service to handle uploading files to Firebase Storage.
 */
public class ImageUploadService extends Service {

    private static final String TAG = "ImageUploadService";

    private StorageReference mStorageRef;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static String getStringValue(int resId){
        return context.getString(resId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:");
        if (getStringValue(R.string.action_upload).equals(intent.getAction())) {
            Uri fileUri = intent.getParcelableExtra(getStringValue(R.string.file_uri));
            uploadFromUri(fileUri);
        }

        return START_REDELIVER_INTENT;
    }



    private void uploadFromUri(final Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        final StorageReference photoRef = mStorageRef.child("photos")
                .child(fileUri.getLastPathSegment());

        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
        photoRef.putFile(fileUri).
                addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                })
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        Log.d(TAG, "continueWithTask " );

                        return photoRef.getDownloadUrl();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri downloadUri) {
                        Log.d(TAG, "uploadFromUri: upload success " +downloadUri);
                        callPostProductActivity(downloadUri);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        broadcastUploadFinished(null, fileUri);
                        showUploadFinishedNotification(null, fileUri);
                    }
                });

        Intent postProduct = new Intent(this, PostProductActivity.class);
//        postProduct.putExtra(this.EXTRA_FILE_URI, fileUri.toString());
        startActivity(postProduct);
    }
    // [END upload_from_uri]

    /**
     * Broadcast finished upload (success or failure).
     * @return true if a running receiver received the broadcast.
     */
    private boolean broadcastUploadFinished(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        boolean success = downloadUrl != null;
        String action = success ? getStringValue(R.string.upload_completed) :
                getStringValue(R.string.upload_error);
        Intent broadcast = new Intent(action).putExtra(getStringValue(R.string.file_uri), downloadUrl);

        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);

    }


    private void callPostProductActivity(@Nullable Uri downloadUrl){

        Intent postProduct = new Intent(this, PostProductActivity.class);
        postProduct.putExtra(getStringValue(R.string.file_uri), downloadUrl);
        startActivity(postProduct);
    }

    /**
     * Show a notification for a finished upload.
     */
    private void showUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        boolean success = downloadUrl != null;
    }



}
