package com.imeja.carpooling.auth.documents;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imeja.carpooling.R;
import com.imeja.carpooling.auth.profile.ProfileActivity;
import com.imeja.carpooling.auth.terms.TermsActivity;
import com.imeja.carpooling.camera.BackCameraActivity;
import com.imeja.carpooling.model.RealmUtils;
import com.imeja.carpooling.net.Endpoints;
import com.imeja.carpooling.trustmanager.HttpsTrustManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DocumentsActivity extends AppCompatActivity {
    private static final int VEHICLE_GALLERY_CODE = 120;
    private static final int VEHICLE_CAMERA_CODE = 121;
    private static final int LICENCE_GALLERY_CODE = 320;
    private static final int LICENCE_CAMERA_CODE = 321;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 220;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 221;
    private Uri filePath;
    AlertDialog.Builder window;
    final String[] Options = {"Camera", "Gallery"};
    CoordinatorLayout coordinatorLayout;
    ProgressDialog pDialog;
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseAuth mAuth;
    FloatingActionButton floatingActionButton;
    String phone;

    String licence, registration;

    String documents;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            phone = currentUser.getPhoneNumber();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Driving Documents");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        documents = getIntent().getStringExtra("documents");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        floatingActionButton = findViewById(R.id.next);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDocuments();
            }
        });
    }

    public void showDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setMessage("Please Wait...");
        pDialog.show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void DismissDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private void saveDocuments() {
        licence = RealmUtils.getLicence();
        registration = RealmUtils.getRegistration();
        if (licence == null || registration == null) {

            Snackbar.make(coordinatorLayout, "Capture Documents", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_refresh, null).show();
        } else {
            showDialog();
            HttpsTrustManager.allowAllSSL();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.DOCUMENTS_UPLOAD_URL,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            DismissDialog();
                            if (response.equalsIgnoreCase("Success! Record saved successfully!")) {
                                if (documents != null) {

                                    DocumentsActivity.this.finish();

                                } else {

                                    startActivity(new Intent(DocumentsActivity.this, ProfileActivity.class));
                                    DocumentsActivity.this.finish();
                                }
                            } else {
                                if (response.contains("PDOException")) {
                                    Snackbar.make(coordinatorLayout, "Error encountered", Snackbar.LENGTH_LONG)
                                            .setAction(R.string.btn_refresh, null).show();

                                } else {
                                    Snackbar.make(coordinatorLayout, response, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.btn_refresh, null).show();
                                }
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            DismissDialog();

                            Snackbar.make(coordinatorLayout, R.string.error_occured, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.btn_refresh, null).show();
                        }

                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request

                    params.put("phone", RealmUtils.getPhoneNumber());
                    params.put("licence", licence);
                    params.put("registration", registration);

                    //returning parameter.
                    return params;
                }
            };

            //Adding the string request to the queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);
            stringRequest.setShouldCache(false);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }

                break;

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }

    public void onVehicleRegistrationClick(View view) {
        BackCameraActivity.VEHICLE_REGISTRATION = true;
        BackCameraActivity.NA_ID_BACK = true;
        startActivityForResult(new Intent(DocumentsActivity.this, BackCameraActivity.class), BackCameraActivity.NA_ID);
/*
        window = new AlertDialog.Builder(this);
        window.setTitle("Vehicle Registration");
        window.setItems(Options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //first option clicked, do this...
                    selectCamera(VEHICLE_CAMERA_CODE);

                } else if (which == 1) {
                    //second option clicked, do this...
                    pickGallery(VEHICLE_GALLERY_CODE);

                } else {
                    //theres an error in what was selected
                    //Toast.makeText(getApplicationContext(), "Hmmm I messed up. I detected that you clicked on : " + which + "?", Toast.LENGTH_LONG).show();
                }
            }
        });

        window.show();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case VEHICLE_GALLERY_CODE:

                    filePath = data.getData();
                    uploadFirebase("Vehicle");
                    break;
                case VEHICLE_CAMERA_CODE:
                    filePath = (Uri) data.getExtras().get("data");
                    uploadFirebase("Vehicle");

                    break;
                case LICENCE_GALLERY_CODE:
                    filePath = data.getData();
                    uploadFirebase("Licence");

                    break;
                case LICENCE_CAMERA_CODE:
                    filePath = (Uri) data.getExtras().get("data");
                    uploadFirebase("Licence");

                    break;

            }
    }

    private void uploadFirebase(String vehicle) {

        if (filePath != null) {

            if (vehicle.equalsIgnoreCase("Vehicle")) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
                ref.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri uri = task.getResult();
                        registration = uri.toString();
                    }
                })
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progressDialog.dismiss();
                                Toast.makeText(DocumentsActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(DocumentsActivity
                                .this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (vehicle.equalsIgnoreCase("Licence")) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
                ref.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri uri = task.getResult();
                        licence = uri.toString();
                    }
                })
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progressDialog.dismiss();
                                Toast.makeText(DocumentsActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(DocumentsActivity
                                .this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }


    private void pickGallery(int vehicleGalleryCode) {
        if (ContextCompat.checkSelfPermission(DocumentsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(DocumentsActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, vehicleGalleryCode);
    }

    private void selectCamera(int vehicleCameraCode) {
        if (ContextCompat.checkSelfPermission(DocumentsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(DocumentsActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, vehicleCameraCode);
    }

    public void onDriverLicenseClick(View view) {
        BackCameraActivity.DRIVER_LICENCE = true;
        BackCameraActivity.NA_ID_BACK = true;
        startActivityForResult(new Intent(DocumentsActivity.this, BackCameraActivity.class), BackCameraActivity.NA_ID);

      /*  window = new AlertDialog.Builder(this);
        window.setTitle("Driver Licence");
        window.setItems(Options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //first option clicked, do this...
                    selectCamera(LICENCE_CAMERA_CODE);

                } else if (which == 1) {
                    //second option clicked, do this...
                    pickGallery(LICENCE_GALLERY_CODE);

                } else {
                    //theres an error in what was selected
                    //Toast.makeText(getApplicationContext(), "Hmmm I messed up. I detected that you clicked on : " + which + "?", Toast.LENGTH_LONG).show();
                }
            }
        });

        window.show();*/
    }
}
