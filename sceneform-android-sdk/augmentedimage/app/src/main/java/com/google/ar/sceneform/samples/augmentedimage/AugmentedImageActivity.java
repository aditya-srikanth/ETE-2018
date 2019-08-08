/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.sceneform.samples.augmentedimage;

import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.samples.restapi.ApiEndpoints;
import com.google.ar.sceneform.samples.restapi.RestApi;
import com.google.ar.sceneform.ux.ArFragment;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * This application demonstrates using augmented images to place anchor nodes. app to include image
 * tracking functionality.
 */
public class AugmentedImageActivity extends AppCompatActivity {
  public static final int RequestPermissionCode = 1;
  private int numQueries = 0;
  private ArFragment arFragment;
  private TextView distanceAndNumber;
  private static final String TAG = AugmentedImageActivity.class.getSimpleName();

  // Augmented image and its associated center pose anchor, keyed by the augmented image in
  // the database.
  //private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
    //fitToScanView = findViewById(R.id.image_view_fit_to_scan);
    distanceAndNumber = findViewById(R.id.status);
    arFragment.getArSceneView().getScene().setOnUpdateListener(this::onUpdateFrame);
  }

  /**
   * Registered with the Sceneform Scene object, this method is called at the start of each frame.
   *
   * @param frameTime - time since last frame.
   */
  private void onUpdateFrame(FrameTime frameTime) {
    // Always call the fragment's onUpdate.
    arFragment.onUpdate(frameTime);

    Frame frame = arFragment.getArSceneView().getArFrame();
    try {
        Image image = frame.acquireCameraImage();
        if(image.getFormat() != ImageFormat.JPEG){
            
        }
        String AbsolutePath = Environment.getExternalStorageDirectory().getAbsolutePath()+System.currentTimeMillis()
        numQueries++;
        Retrofit retrofit = RestApi.getRetrofit();
        ApiEndpoints apiEndpoints = retrofit.create(ApiEndpoints.class);

        String features = "Categories,Description,Color";

        Call<ResponseBody> call = apiEndpoints.sendImage(image, features);



        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        String result = response.body().string();
                        Log.e(TAG, result);
                        if (result.toLowerCase().contains("door")){
                            Toast.makeText(getApplicationContext(), "Door Detected",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Request fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
    catch (NotYetAvailableException e){
        Log.i(TAG,"ERROR:  "+ e);
    }
    // If there is no frame or ARCore is not tracking yet, just return.
    if (frame == null || frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
      return;
    }

    Collection<Plane> updateAugmentedPlane = frame.getUpdatedTrackables(Plane.class);
    distanceAndNumber.setText("Number of planes:" + updateAugmentedPlane.size() );
    for(Plane plane: updateAugmentedPlane){
        Pose centerPose = plane.getCenterPose();
        Pose cameraPose = frame.getAndroidSensorPose();
        float centerTx = centerPose.tx();
        float centerTy = centerPose.ty();
        float centerTz = centerPose.tz();
        float cameraTx= cameraPose.tx();
        float cameraTy= cameraPose.ty();
        float cameraTz= cameraPose.tz();
        float dx = centerTx - cameraTx;
        float dy = centerTy - cameraTy;
        float dz = centerTz - cameraTz;
        float euclidDist = (float) Math.sqrt(dx*dx+dy*dy+dz*dz);
        distanceAndNumber.append("Euclid dist: " + euclidDist);
        Log.i(TAG,""+euclidDist + "Number of planes" + updateAugmentedPlane.size());
    }
  }


    private void requestPermission() {
        ActivityCompat.requestPermissions(AugmentedImageActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, RequestPermissionCode);
    }

    /**
     *
     * @param  requestCode the request code which is sent when the requestPermissions is called
     * @param permissions the list of permissions granted
     * @param grantResults the results of the permission.
     *
     * This method is used to interpret the result of the requestPermissions.
     *
     * */

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    /**
     *
     * @return the result of the permission's activation
     *
     * this method checks whether the requested permissions are activated or not.
     *
     * */

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}
