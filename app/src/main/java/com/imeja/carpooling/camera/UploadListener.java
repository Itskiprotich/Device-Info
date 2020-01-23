package com.imeja.carpooling.camera;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public interface UploadListener {

  void failed();

  void success(Uri taskSnapshot);

  void completed(Task<Uri> task);

}
