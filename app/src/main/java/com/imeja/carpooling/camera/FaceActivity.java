package com.imeja.carpooling.camera;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.Task;
import com.imeja.carpooling.R;
import com.imeja.carpooling.camera.common.CameraSource;
import com.imeja.carpooling.camera.common.CameraSourcePreview;
import com.imeja.carpooling.camera.common.GraphicOverlay;
import com.imeja.carpooling.camera.facedetection.FaceDetectionProcessor;
import com.imeja.carpooling.model.AppUtils;
import com.imeja.carpooling.model.RealmUtils;
import com.imeja.carpooling.storage.StorageController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class FaceActivity extends AppCompatActivity
  implements ActivityCompat.OnRequestPermissionsResultCallback,
  Camera.PictureCallback, Camera.ShutterCallback, UploadListener {

  public static final int FACE = 1234;
  public static final String PROFILE_URI = "profile uri";
  private static final String FACE_DETECTION = "Face Detection";
  private static final String TAG = "LivePreviewActivity";
  private static final int PERMISSION_REQUESTS = 1022;
  public static boolean manyFaces = false;
  public static boolean faceDetected = false;
  private static ProgressDialog progressDialog;
  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private String selectedModel = FACE_DETECTION;
  private AppCompatImageButton imageButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    View decorView = getWindow().getDecorView();
// Hide the status bar.
    int uiOptions = 0;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
      uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
      decorView.setSystemUiVisibility(uiOptions);
    }
    setContentView(R.layout.activity_face);
    preview = findViewById(R.id.firePreview);
    imageButton = findViewById(R.id.capture);
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Saving your profile image");
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setCancelable(false);

    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = findViewById(R.id.fireFaceOverlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

    if (allPermissionsGranted()) {
      createCameraSource();
    } else {
      getRuntimePermissions();
    }
  }

  private void createCameraSource() {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

    cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
    cameraSource.setMachineLearningFrameProcessor(new FaceDetectionProcessor());
    imageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        capture(v);
      }
    });
  }

  /*

   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  public void capture(View view) {
    if (!faceDetected) {
      AppUtils.showFaceDialog(this, false);
    } else if (manyFaces) {
      AppUtils.showFaceDialog(this, true);
    } else {
      showDialog();
      cameraSource.capture(this, this);
    }

  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    if (allPermissionsGranted())
      startCameraSource();
  }

  /*
   * Stops the camera.
   */
  @Override
  protected void onPause() {
    super.onPause();
    if (preview != null)
      preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
  }


  private boolean allPermissionsGranted() {
    return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
  }

  private void getRuntimePermissions() {
    ActivityCompat.requestPermissions(
      this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUESTS);
  }

  @Override
  public void onRequestPermissionsResult(
          int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    Log.i(TAG, "Permission granted!");
    if (allPermissionsGranted()) {
      createCameraSource();
    } else {
      AppUtils.showAccessDialog(this, "Camera", "Access to your front camera is required in order to take a selfie"
        , new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(FaceActivity.this, Manifest.permission.CAMERA)) {
              getRuntimePermissions();
            } else {
              Intent intent = new Intent();
              intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
              Uri uri = Uri.fromParts("package", FaceActivity.this.getPackageName(), null);
              intent.setData(uri);
              FaceActivity.this.startActivity(intent);
              finish();
            }
            dialog.dismiss();
          }
        }, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            finish();
          }
        });
    }
  }

  private void showDialog() {
    progressDialog.show();
  }

  private void dismissDialog() {
    progressDialog.dismiss();
  }

  @Override
  public void onPictureTaken(byte[] data, Camera camera) {
    Log.e(TAG, "onPictureTaken: data size is " + data.length);

    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
    Matrix rotateRight = new Matrix();
    rotateRight.preRotate(180);

    float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
    rotateRight = new Matrix();
    Matrix matrixMirrorY = new Matrix();
    matrixMirrorY.setValues(mirrorY);

    rotateRight.postConcat(matrixMirrorY);

    rotateRight.preRotate(360);


    final Bitmap rImg = Bitmap.createBitmap(bitmap, 0, 0,
      bitmap.getWidth(), bitmap.getHeight(), rotateRight, true);
    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
    rImg.compress(Bitmap.CompressFormat.PNG, 100, stream2);
    StorageController.storeImage(true, stream2.toByteArray(), this, false);
  }

  @Override
  public void onShutter() {

  }

  @Override
  public void failed() {
    AppUtils.showUploadErrorDialog(this);
  }

  @Override
  public void success(Uri taskSnapshot) {
    Log.e(TAG, "success: uri is " + taskSnapshot);
    RealmUtils.setProfileURL(taskSnapshot.toString());
    setResult(RESULT_OK, null);
    finish();
  }

  @Override
  public void completed(Task<Uri> task) {
    if (task.isSuccessful()) {
      Log.e(TAG, "success: url is " + task.getResult());
    } else {
      Log.e(TAG, "success: exception is " + task.getException().getLocalizedMessage());
    }
  }

}
