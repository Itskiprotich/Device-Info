package com.imeja.carpooling.storage;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imeja.carpooling.camera.UploadListener;
import com.imeja.carpooling.model.RealmUtils;

import java.util.Random;

public class StorageController {

    private static String TAG = StorageController.class.getSimpleName();

    public static void storeImage(boolean profile, byte[] data, final UploadListener uploadListener, boolean back) {
        String number = RealmUtils.getPhoneNumber();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Random random=new Random();
        int jeff=random.nextInt(1000000)+10000;
        final StorageReference imagesRef = storageRef.child(number + "/" + (profile ? "profile.png" : back ? jeff+".png" : jeff+".png"));

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                uploadListener.completed(task);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "onFailure: exception is " + exception.getMessage());
                uploadListener.failed();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                uploadListener.success(uri);
            }
        });
    }
}
