/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.classification;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tensorflow.lite.examples.classification.WorkoutData.WorkoutData;
import org.tensorflow.lite.examples.classification.env.ImageUtils;
import org.tensorflow.lite.examples.classification.env.Logger;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Device;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Model;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Recognition;

public abstract class CameraActivity extends AppCompatActivity
    implements OnImageAvailableListener,
        Camera.PreviewCallback,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {
    private static final Logger LOGGER = new Logger();

    private static final int PERMISSIONS_REQUEST = 1;

    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    protected int previewWidth = 0;
    protected int previewHeight = 0;
    private Handler handler;
    private HandlerThread handlerThread;
    private boolean useCamera2API;
    private boolean isProcessingFrame = false;
    private byte[][] yuvBytes = new byte[3][];
    private int[] rgbBytes = null;
    private int yRowStride;
    private Runnable postInferenceCallback;
    private Runnable imageConverter;
    private LinearLayout bottomSheetLayout;
    private LinearLayout gestureLayout;
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    /*
    protected TextView recognitionTextView,
        recognition1TextView,
        recognition2TextView,
        recognitionValueTextView,
        recognition1ValueTextView,
        recognition2ValueTextView;
     */
    protected TextView frameValueTextView,
        cropValueTextView,
        cameraResolutionTextView,
        rotationTextView,
        inferenceTimeTextView;
    protected ImageView bottomSheetArrowImageView;
    private ImageView plusImageView, minusImageView;
    private Spinner modelSpinner;
    private Spinner deviceSpinner;
    private TextView threadsTextView;

    String str_model = null;
    private Model model = Model.QUANTIZED_EFFICIENTNET;
    private Device device = Device.CPU;
    private int numThreads = -1;
    MediaPlayer mediaPlayer;


    // record
    String email;
    int exercise;

    private int state_stand;
    private int wrong_state_stand;

    private int count;
    private int wrong_count;
    private int max_count;

    private FirebaseDatabase database;
    private DatabaseReference refUser;
    //------------------------------
    private static final String DEFAULT_PATTERN = "%d%%";

    CircleProgressBar circleProgressBar;
    //------------------------------



    int[] count_media_array = { R.raw.count_10, R.raw.count_1, R.raw.count_2, R.raw.count_3, R.raw.count_4, R.raw.count_5, R.raw.count_6, R.raw.count_7, R.raw.count_8, R.raw.count_9};
    public String getStr_model()
    {
      return str_model;
    }

    public void speakCount(int i)
    {
      mediaPlayer = MediaPlayer.create(this, count_media_array[i]);
      mediaPlayer.start();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        LOGGER.d("onCreate " + this);
        super.onCreate(null);

        initializeDB();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.tfe_ic_activity_camera);

        if (hasPermission())
        {
            setFragment();
        }
        else
        {
            requestPermission();
        }

        //recognitionTextView = findViewById(R.id.detected_item);
        //recognitionValueTextView = findViewById(R.id.detected_item_value);

        Intent intent = getIntent();
        str_model = intent.getExtras().getString("model");


        max_count = 10;
        state_stand = 0;
        wrong_state_stand = 0;
        count = 0;
        wrong_count = 0;

        //
        circleProgressBar=findViewById(R.id.cpb_circlebar);
        circleProgressBar.setMax(this.max_count);
        circleProgressBar.setProgress(0);  // 해당 퍼센트를 적용
        circleProgressBar.setProgressTextColor(Color.parseColor("#55DDDD"));
        circleProgressBar.setProgressFormatter((progress, max) -> {
            final String DEFAULT_PATTERN = "%d/%d";
            return String.format(DEFAULT_PATTERN, progress, max);
        });

        //
        // 시작합니다.
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.start_0);
        mediaPlayer.start();

        switch(str_model)
        {
        case "sit":
          setModel(Model.valueOf("Float_EfficientNet".toUpperCase()));
          exercise = 0;
          break;
        case "stand":
          setModel(Model.valueOf("Float_MobileNet".toUpperCase()));
          exercise = 1;
          break;
        case "lay":
          setModel(Model.valueOf("Quantized_EfficientNet".toUpperCase()));
          exercise = 2;
          break;
        case "tool":
          setModel(Model.valueOf("Quantized_MobileNet".toUpperCase()));
          exercise = 3;
          break;
        default:
      }

      /*
      threadsTextView = findViewById(R.id.threads);
      plusImageView = findViewById(R.id.plus);
      minusImageView = findViewById(R.id.minus);
      modelSpinner = findViewById(R.id.model_spinner);
      deviceSpinner = findViewById(R.id.device_spinner);
      bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
      gestureLayout = findViewById(R.id.gesture_layout);
      sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
      bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow);
       */

      /*
      ViewTreeObserver vto = gestureLayout.getViewTreeObserver();
      vto.addOnGlobalLayoutListener(
          new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
              if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                gestureLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
              } else {
                gestureLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
              }
              //                int width = bottomSheetLayout.getMeasuredWidth();
              int height = gestureLayout.getMeasuredHeight();

              sheetBehavior.setPeekHeight(height);
            }
          });

      sheetBehavior.setHideable(false);

      sheetBehavior.setBottomSheetCallback(
          new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
              switch (newState) {
                case BottomSheetBehavior.STATE_HIDDEN:
                  break;
                case BottomSheetBehavior.STATE_EXPANDED:
                  {
                    bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_down);
                  }
                  break;
                case BottomSheetBehavior.STATE_COLLAPSED:
                  {
                    bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                  }
                  break;
                case BottomSheetBehavior.STATE_DRAGGING:
                  break;
                case BottomSheetBehavior.STATE_SETTLING:
                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                  break;
              }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
          });

      recognitionTextView = findViewById(R.id.detected_item);
      recognitionValueTextView = findViewById(R.id.detected_item_value);
      recognition1TextView = findViewById(R.id.detected_item1);
      recognition1ValueTextView = findViewById(R.id.detected_item1_value);
      recognition2TextView = findViewById(R.id.detected_item2);
      recognition2ValueTextView = findViewById(R.id.detected_item2_value);

      frameValueTextView = findViewById(R.id.frame_info);
      cropValueTextView = findViewById(R.id.crop_info);
      cameraResolutionTextView = findViewById(R.id.view_info);
      rotationTextView = findViewById(R.id.rotation_info);
      inferenceTimeTextView = findViewById(R.id.inference_info);

      modelSpinner.setOnItemSelectedListener(this);
      deviceSpinner.setOnItemSelectedListener(this);

      plusImageView.setOnClickListener(this);
      minusImageView.setOnClickListener(this);

      model = Model.valueOf(modelSpinner.getSelectedItem().toString().toUpperCase());
      device = Device.valueOf(deviceSpinner.getSelectedItem().toString());
      numThreads = Integer.parseInt(threadsTextView.getText().toString().trim());
       */
    }

    protected int[] getRgbBytes() {
      imageConverter.run();
      return rgbBytes;
    }

    protected int getLuminanceStride() {
      return yRowStride;
    }

    protected byte[] getLuminance() {
      return yuvBytes[0];
    }

    /** Callback for android.hardware.Camera API */
    @Override
    public void onPreviewFrame(final byte[] bytes, final Camera camera) {
      if (isProcessingFrame) {
        LOGGER.w("Dropping frame!");
        return;
      }

      try {
        // Initialize the storage bitmaps once when the resolution is known.
        if (rgbBytes == null) {
          Camera.Size previewSize = camera.getParameters().getPreviewSize();
          previewHeight = previewSize.height;
          previewWidth = previewSize.width;
          rgbBytes = new int[previewWidth * previewHeight];
          onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
        }
      } catch (final Exception e) {
        LOGGER.e(e, "Exception!");
        return;
      }

      isProcessingFrame = true;
      yuvBytes[0] = bytes;
      yRowStride = previewWidth;

      imageConverter =
          new Runnable() {
            @Override
            public void run() {
              ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
            }
          };

      postInferenceCallback =
          new Runnable() {
            @Override
            public void run() {
              camera.addCallbackBuffer(bytes);
              isProcessingFrame = false;
            }
          };
      processImage();
    }

    /** Callback for Camera2 API */
    @Override
    public void onImageAvailable(final ImageReader reader) {
      // We need wait until we have some size from onPreviewSizeChosen
      if (previewWidth == 0 || previewHeight == 0) {
        return;
      }
      if (rgbBytes == null) {
        rgbBytes = new int[previewWidth * previewHeight];
      }
      try {
        final Image image = reader.acquireLatestImage();

        if (image == null) {
          return;
        }

        if (isProcessingFrame) {
          image.close();
          return;
        }
        isProcessingFrame = true;
        Trace.beginSection("imageAvailable");
        final Plane[] planes = image.getPlanes();
        fillBytes(planes, yuvBytes);
        yRowStride = planes[0].getRowStride();
        final int uvRowStride = planes[1].getRowStride();
        final int uvPixelStride = planes[1].getPixelStride();

        imageConverter =
            new Runnable() {
              @Override
              public void run() {
                ImageUtils.convertYUV420ToARGB8888(
                    yuvBytes[0],
                    yuvBytes[1],
                    yuvBytes[2],
                    previewWidth,
                    previewHeight,
                    yRowStride,
                    uvRowStride,
                    uvPixelStride,
                    rgbBytes);
              }
            };

        postInferenceCallback =
            new Runnable() {
              @Override
              public void run() {
                image.close();
                isProcessingFrame = false;
              }
            };

        processImage();
      } catch (final Exception e) {
        LOGGER.e(e, "Exception!");
        Trace.endSection();
        return;
      }
      Trace.endSection();
    }

    @Override
    public synchronized void onStart() {
      LOGGER.d("onStart " + this);
      super.onStart();
    }

    @Override
    public synchronized void onResume() {
      LOGGER.d("onResume " + this);
      super.onResume();

      handlerThread = new HandlerThread("inference");
      handlerThread.start();
      handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public synchronized void onPause() {
      LOGGER.d("onPause " + this);

      handlerThread.quitSafely();
      try {
        handlerThread.join();
        handlerThread = null;
        handler = null;
      } catch (final InterruptedException e) {
        LOGGER.e(e, "Exception!");
      }

      super.onPause();
    }

    @Override
    public synchronized void onStop() {
      LOGGER.d("onStop " + this);
      super.onStop();
    }

    @Override
    public synchronized void onDestroy() {
      LOGGER.d("onDestroy " + this);
      super.onDestroy();
    }

    protected synchronized void runInBackground(final Runnable r) {
      if (handler != null) {
        handler.post(r);
      }
    }

    @Override
    public void onRequestPermissionsResult(
        final int requestCode, final String[] permissions, final int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      if (requestCode == PERMISSIONS_REQUEST) {
        if (allPermissionsGranted(grantResults)) {
          setFragment();
        } else {
          requestPermission();
        }
      }
    }

    private static boolean allPermissionsGranted(final int[] grantResults) {
      for (int result : grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          return false;
        }
      }
      return true;
    }

    private boolean hasPermission() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
      } else {
        return true;
      }
    }

    private void requestPermission() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
          Toast.makeText(
                  CameraActivity.this,
                  "Camera permission is required for this demo",
                  Toast.LENGTH_LONG)
              .show();
        }
        requestPermissions(new String[] {PERMISSION_CAMERA}, PERMISSIONS_REQUEST);
      }
    }

    // Returns true if the device supports the required hardware level, or better.
    private boolean isHardwareLevelSupported(
        CameraCharacteristics characteristics, int requiredLevel) {
      int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
      if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
        return requiredLevel == deviceLevel;
      }
      // deviceLevel is not LEGACY, can use numerical sort
      return requiredLevel <= deviceLevel;
    }

    private String chooseCamera() {
      final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
      try {
        for (final String cameraId : manager.getCameraIdList()) {
          final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

          // We don't use a front facing camera in this sample.
          final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
          if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
            continue;
          }

          final StreamConfigurationMap map =
              characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

          if (map == null) {
            continue;
          }

          // Fallback to camera1 API for internal cameras that don't have full support.
          // This should help with legacy situations where using the camera2 API causes
          // distorted or otherwise broken previews.
          useCamera2API =
              (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                  || isHardwareLevelSupported(
                      characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
          LOGGER.i("Camera API lv2?: %s", useCamera2API);
          return cameraId;
        }
      } catch (CameraAccessException e) {
        LOGGER.e(e, "Not allowed to access camera");
      }

      return null;
    }

    protected void setFragment() {
      String cameraId = chooseCamera();

      Fragment fragment;
      if (useCamera2API) {
        CameraConnectionFragment camera2Fragment =
            CameraConnectionFragment.newInstance(
                new CameraConnectionFragment.ConnectionCallback() {
                  @Override
                  public void onPreviewSizeChosen(final Size size, final int rotation) {
                    previewHeight = size.getHeight();
                    previewWidth = size.getWidth();
                    CameraActivity.this.onPreviewSizeChosen(size, rotation);
                  }
                },
                this,
                getLayoutId(),
                getDesiredPreviewFrameSize());

        camera2Fragment.setCamera(cameraId);
        fragment = camera2Fragment;
      } else {
        fragment =
            new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
      }

      getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
      // Because of the variable row stride it's not possible to know in
      // advance the actual necessary dimensions of the yuv planes.
      for (int i = 0; i < planes.length; ++i) {
        final ByteBuffer buffer = planes[i].getBuffer();
        if (yuvBytes[i] == null) {
          LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
          yuvBytes[i] = new byte[buffer.capacity()];
        }
        buffer.get(yuvBytes[i]);
      }
    }

    protected void readyForNextImage() {
      if (postInferenceCallback != null) {
        postInferenceCallback.run();
      }
    }

    protected int getScreenOrientation() {
      switch (getWindowManager().getDefaultDisplay().getRotation()) {
        case Surface.ROTATION_270:
          return 270;
        case Surface.ROTATION_180:
          return 180;
        case Surface.ROTATION_90:
          return 90;
        default:
          return 0;
      }
    }

    @UiThread
    protected void showResultsInBottomSheet(List<Recognition> results)
    {
    }

    @UiThread
    protected void showResultStand(List<Recognition> results) {
        if (results != null && results.size() >= 3)
        {
            Recognition recognition = results.get(0);
            if (recognition != null)
            {
                if (recognition.getTitle() != null && recognition.getConfidence() != null)
                {
                    //recognitionTextView.setText(recognition.getTitle() + "\n" + String.format("%.2f", (100 * recognition.getConfidence())) + "%");
                    //recognitionValueTextView.setText(Integer.toString(count) + "초");
                }
                if (recognition.getTitle().equals("0 Stand") && recognition.getConfidence() > 0.7)
                {
                    state_stand = 0;
                }
                else if (recognition.getTitle().equals("1 Up") && recognition.getConfidence() > 0.6)
                {
                    //if(state_stand == 1)
                    //{
                        count++;
                        //recognitionValueTextView.setText(Integer.toString(count) + "초");
                        speakCount((count)%10 );
                        if(count >= max_count)
                        {
                            mediaPlayer = MediaPlayer.create(this, R.raw.end);
                            mediaPlayer.start();
                            state_stand = 0;
                            count = 0;
                            finishWorkout();
                        }
                    //}
                    state_stand = 1;
                }
                else if(recognition.getTitle().equals("2 WrongArm") && recognition.getConfidence() > 0.6)
                {
                    mediaPlayer = MediaPlayer.create(this, R.raw.arm_up);
                    mediaPlayer.start();
                    state_stand = 2;
                    wrong_count++;
                }
                else if(recognition.getTitle().equals("3 WrongLeg") && recognition.getConfidence() > 0.6)
                {
                    mediaPlayer = MediaPlayer.create(this, R.raw.leg_up);
                    mediaPlayer.start();
                    state_stand = 3;
                    wrong_count++;
                }
                else
                {
                    // say something
                }
                circleProgressBar.setProgress(count);
            }
        }
    }

    @UiThread
    protected void showResultSit(List<Recognition> results) {
        if (results != null && results.size() >= 3)
        {
            Recognition recognition = results.get(0);
            if (recognition != null)
            {
                if (recognition.getTitle() != null && recognition.getConfidence() != null)
                {
                }
                if (recognition.getTitle().equals("0 Stand") && recognition.getConfidence() > 0.9)
                {
                    state_stand = 0;
                    wrong_state_stand = 0;
                }
                else if (recognition.getTitle().equals("1 Up") && recognition.getConfidence() > 0.7)
                {
                    if(state_stand == 0)
                    {
                        count++;
                        //recognitionValueTextView.setText(Integer.toString(count) + "초");
                        speakCount((count)%10 );
                        if(count >= max_count)
                        {
                            mediaPlayer = MediaPlayer.create(this, R.raw.end);
                            mediaPlayer.start();
                            state_stand = 0;
                            count = 0;
                            finishWorkout();
                        }
                        wrong_state_stand = 0;
                    }
                    state_stand = 1;
                }
                else if(recognition.getTitle().equals("2 WrongArm") && recognition.getConfidence() > 0.5)
                {
                    wrong_state_stand++;
                    if(wrong_state_stand > 3)
                    {
                        mediaPlayer = MediaPlayer.create(this, R.raw.arm_up);
                        mediaPlayer.start();
                        wrong_state_stand = 0;
                    }
                    wrong_count++;
                }
                else if(recognition.getTitle().equals("3 WrongLeg") && recognition.getConfidence() > 0.6)
                {
                    wrong_state_stand++;
                    if(wrong_state_stand > 3)
                    {
                        mediaPlayer = MediaPlayer.create(this, R.raw.arm_up);
                        mediaPlayer.start();
                        wrong_state_stand = 0;
                    }
                    wrong_count++;
                }
                else
                {
                    // say something
                }
                circleProgressBar.setProgress(count);
            }
        }
    }


    protected void showFrameInfo(String frameInfo) {
      frameValueTextView.setText(frameInfo);
    }

    protected void showCropInfo(String cropInfo) {
      cropValueTextView.setText(cropInfo);
    }

    protected void showCameraResolution(String cameraInfo) {
      cameraResolutionTextView.setText(cameraInfo);
    }

    protected void showRotationInfo(String rotation) {
      rotationTextView.setText(rotation);
    }

    protected void showInference(String inferenceTime) {
      inferenceTimeTextView.setText(inferenceTime);
    }

    protected Model getModel() {
      return model;
    }

    private void setModel(Model model) {
      if (this.model != model) {
        LOGGER.d("Updating  model: " + model);
        this.model = model;
        onInferenceConfigurationChanged();
      }
    }

    protected Device getDevice() {
      return device;
    }

    private void setDevice(Device device) {
      if (this.device != device) {
        LOGGER.d("Updating  device: " + device);
        this.device = device;
        final boolean threadsEnabled = device == Device.CPU;
        plusImageView.setEnabled(threadsEnabled);
        minusImageView.setEnabled(threadsEnabled);
        threadsTextView.setText(threadsEnabled ? String.valueOf(numThreads) : "N/A");
        onInferenceConfigurationChanged();
      }
    }

    protected int getNumThreads() {
      return numThreads;
    }

    private void setNumThreads(int numThreads) {
      if (this.numThreads != numThreads) {
        LOGGER.d("Updating  numThreads: " + numThreads);
        this.numThreads = numThreads;
        onInferenceConfigurationChanged();
      }
    }

    protected abstract void processImage();

    protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

    protected abstract int getLayoutId();

    protected abstract Size getDesiredPreviewFrameSize();

    protected abstract void onInferenceConfigurationChanged();

    @Override
    public void onClick(View v) {
      if (v.getId() == R.id.plus) {
        String threads = threadsTextView.getText().toString().trim();
        int numThreads = Integer.parseInt(threads);
        if (numThreads >= 9) return;
        setNumThreads(++numThreads);
        threadsTextView.setText(String.valueOf(numThreads));
      } else if (v.getId() == R.id.minus) {
        String threads = threadsTextView.getText().toString().trim();
        int numThreads = Integer.parseInt(threads);
        if (numThreads == 1) {
          return;
        }
        setNumThreads(--numThreads);
        threadsTextView.setText(String.valueOf(numThreads));
      }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
      if (parent == modelSpinner) {
        setModel(Model.valueOf(parent.getItemAtPosition(pos).toString().toUpperCase()));
      } else if (parent == deviceSpinner) {
        setDevice(Device.valueOf(parent.getItemAtPosition(pos).toString()));
      }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
      // Do nothing.
    }

    public void initializeDB()
    {
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if (User!=null)
        {
            email = User.getEmail();
            email = email.split("@")[0];
            email = email.substring(3);
        }
        else
            email = "NULL";

        database = FirebaseDatabase.getInstance();
        refUser = database.getReference("User/" + email);
    }

    public void finishWorkout()
    {
        // record DB
        DatabaseReference refWorkout = refUser.child("WorkOut");
        Map<String, Object> workout_list = new HashMap<>();

        SimpleDateFormat time_format = new SimpleDateFormat ( "yyyy-MM-dd_HH:mm:ss");
        Calendar time = Calendar.getInstance();
        String date_time = time_format.format(time.getTime());

        workout_list.put(date_time + "_" + exercise + "_" + Integer.toString(max_count) + "_" + Integer.toString(wrong_count), new WorkoutData(date_time, exercise, max_count, wrong_count));
        refWorkout.updateChildren(workout_list);
        finish();
    }
}
