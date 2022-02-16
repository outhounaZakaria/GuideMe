package com.example.guideme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int lang = textToSpeech.setLanguage(Locale.forLanguageTag("ar-iq"));
                }
            }
        });

        /**
         * OnClickListener : pour reconnaitre chaque zone de l'ecran au démarrage
         */
        findViewById(R.id.textRecognizer).setOnClickListener(v -> {
            long[] tabVibration = {0, 1000};
            Vibrator vibrator = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(tabVibration, -1);
            textToSpeech.speak("خدمة التعرف على النص", textToSpeech.QUEUE_FLUSH, null);
        });

        findViewById(R.id.vetement).setOnClickListener(v -> {
            long[] tabVibration = {0, 500};
            Vibrator vibrator = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(tabVibration, -1);
            textToSpeech.speak("خدمة الكشف عن الملابس", textToSpeech.QUEUE_FLUSH, null);
        });

        findViewById(R.id.color).setOnClickListener(v -> {
            long[] tabVibration = {0, 500,100, 500};
            Vibrator vibrator = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(tabVibration, -1);
            textToSpeech.speak("خدمة الكشف عن اللون", textToSpeech.QUEUE_FLUSH, null);
        });

        findViewById(R.id.produit).setOnClickListener(v -> {
            long[] tabVibration = {0, 500,100, 500,100, 500};
            Vibrator vibrator = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(tabVibration, -1);
            textToSpeech.speak("خدمة الكشف عن الطعام", textToSpeech.QUEUE_FLUSH, null);
        });

        findViewById(R.id.monnaie).setOnClickListener(v -> {
            long[] tabVibration = {0, 500,100, 500,100, 500,100, 500};
            Vibrator vibrator = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(tabVibration, -1);
            textToSpeech.speak("خدمة الكشف عن الأموال", textToSpeech.QUEUE_FLUSH, null);
        });


        /**
         * OnLongClickListener :
         */
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        findViewById(R.id.monnaie).setOnLongClickListener(v ->{
            long[] tabVibration = {0, 1000,100, 1000};
            Vibrator vibrator = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(tabVibration, -1);
            intent.putExtra("flag", "monnaie");
            startActivity(intent);
            return false;
        });

        findViewById(R.id.produit).setOnLongClickListener(v ->{
            long[] tabVibration = {0, 1000,100, 1000};
            Vibrator vibrator = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(tabVibration, -1);
            intent.putExtra("flag", "produit");
            startActivity(intent);
            return false;
        });

        findViewById(R.id.color).setOnLongClickListener(v ->{
            long[] tabVibration = {0, 1000,100, 1000};
            Vibrator vibrator = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(tabVibration, -1);
            intent.putExtra("flag", "color");
            startActivity(intent);
            return false;
        });

        findViewById(R.id.vetement).setOnLongClickListener(v ->{
            long[] tabVibration = {0, 1000,100, 1000};
            Vibrator vibrator = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(tabVibration, -1);
            intent.putExtra("flag", "vetement");
            startActivity(intent);
            return false;
        });

        findViewById(R.id.textRecognizer).setOnLongClickListener(v ->{
            long[] tabVibration = {0, 1000,100, 1000};
            Vibrator vibrator = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(tabVibration, -1);
            intent.putExtra("flag", "textRecognizer");
            startActivity(intent);
            return false;
        });

    }
}