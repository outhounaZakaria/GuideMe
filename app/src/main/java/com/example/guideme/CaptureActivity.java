package com.example.guideme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CaptureActivity extends AppCompatActivity {

    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String [] REQUIRED_PERMISSIONS = new String []{Manifest.permission.CAMERA};


    PreviewView mPreviewView;
    TextView tvResults;
    DetectorClass detector;
    TextToSpeech textToSpeech , speechFR , speechEN;
    String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        speechEN = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int lang = speechEN.setLanguage(Locale.ENGLISH);
                }
            }
        });

        speechFR = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int lang = speechFR.setLanguage(Locale.FRENCH);
                }
            }
        });
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int lang = textToSpeech.setLanguage(Locale.forLanguageTag("ar-iq"));
                }
            }
        });

        Bundle extras = this.getIntent().getExtras();
        flag = extras.getString("flag");


        mPreviewView = findViewById(R.id.viewFinder);

        detector = new DetectorClass();

        if (allPermissionsGranted()){
            startCamera();
        }
        else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS);
        }


        findViewById(R.id.lancerDetection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( ( (Globale) getApplication()).getLancerDetection()){
                    textToSpeech.speak("توقيف خدمة الكشف", textToSpeech.QUEUE_FLUSH, null);
                }else{
                    textToSpeech.speak("تشغيل خدمة الكشف", textToSpeech.QUEUE_FLUSH, null);
                }
                ((Globale) getApplication()).setLancerDetection( !( (Globale) getApplication()).getLancerDetection());
            }
        });

    }

    private void startCamera(){
        ListenableFuture<ProcessCameraProvider>
                cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                            bindPreview(cameraProvider);
                        } catch (ExecutionException | InterruptedException e) { }
                    }
                },
                ActivityCompat.getMainExecutor(this)
        );
    }

    void bindPreview(ProcessCameraProvider cameraProvider) {
        ImageCapture.Builder builder = new ImageCapture.Builder();
        ImageCapture imageCapture = builder.build();

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(
                CameraSelector.LENS_FACING_BACK).build();
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ActivityCompat.getMainExecutor(this),
                new ImageAnalysis.Analyzer() {
                    int framNumb = 0 ;
                    @Override
                    public void analyze(@NonNull ImageProxy image) {

                        while (framNumb < 50){
                            framNumb ++ ;
                            image.close();
                            return ;
                        }
                        @SuppressLint("UnsafeOptInUsageError") Image img =  image.getImage();
                        Bitmap bitmap = Utils.toBitmap(img);
                        if(flag.equals("textRecognizer")){
                            String[] result = detector.getTextFromImage(bitmap, getApplicationContext());
                            if(result[0].equals("")){
                                speechFR.speak("rien n'est détecté", speechFR.QUEUE_FLUSH, null);
                            }
                            else{
                                switch (result[1]){
                                    case "fr":
                                        speechFR.speak(result[0], speechFR.QUEUE_ADD, null);
                                        break;
                                    case "en":
                                        speechEN.speak(result[0], speechEN.QUEUE_ADD, null);
                                        break;
                                }
                            }
                        }
                        else
                            if ( ( (Globale) getApplication()).getLancerDetection()){
                                textToSpeech.speak(detector.detector(bitmap, flag, getApplicationContext()), textToSpeech.QUEUE_FLUSH, null);
                            }

                        framNumb = 0 ;
                        image.close();
                    }
                });
        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector,
                preview, imageAnalysis, imageCapture);
    }

    private boolean allPermissionsGranted(){
        for(String permission: REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(
                    this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            }
            else{
                Toast.makeText(this, "Please, give access to camera",
                        Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }
}