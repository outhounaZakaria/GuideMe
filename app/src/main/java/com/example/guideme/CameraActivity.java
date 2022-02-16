package com.example.guideme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class CameraActivity extends AppCompatActivity {
    Bitmap bitmap;
    ImageView imageView;
    DetectorClass detector;
    TextToSpeech textToSpeech, speechEN, speechFR;
    String flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        detector = new DetectorClass();

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int lang = textToSpeech.setLanguage(Locale.forLanguageTag("ar-iq"));
                }
            }
        });

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

        Bundle extras = this.getIntent().getExtras();
        flag = extras.getString("flag");

        imageView = findViewById(R.id.imageView);

        // lancer la caméra
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            if(flag.equals("textRecognizer")){
                String[] result = detector.getTextFromImage(bitmap, getApplicationContext());
                switch (result[1]){
                    case "fr":
                        speechFR.speak(result[0], speechFR.QUEUE_FLUSH, null);
                        break;
                    case "en":
                        speechEN.speak(result[0], speechEN.QUEUE_FLUSH, null);
                        break;
                }
            }
            else
                textToSpeech.speak(detector.detector(bitmap, flag, getApplicationContext()), textToSpeech.QUEUE_FLUSH, null);
        }
    }
}