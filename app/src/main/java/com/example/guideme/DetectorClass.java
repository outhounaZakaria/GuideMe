package com.example.guideme;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.example.guideme.ml.ModelCouleur;
import com.example.guideme.ml.ModelMonnaie;
import com.example.guideme.ml.ModelProduit;
import com.example.guideme.ml.ModelVetement;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class DetectorClass {
    public String detector(Bitmap bitmap, String flag, Context context){
        String result = null;
        String list[] = null;
        switch (flag){
            case "monnaie":
                list = getArrayFromFile("labelMonnaie", context);
                bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
                try {
                    ModelMonnaie model = ModelMonnaie.newInstance(context);

                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                    TensorImage tensorImage = new TensorImage(DataType.UINT8);
                    tensorImage.load(bitmap);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();
                    inputFeature0.loadBuffer(byteBuffer);

                    ModelMonnaie.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    model.close();

                    result = list[getMAx(outputFeature0.getFloatArray(), list.length)];
                } catch (IOException e) {
                    Log.d("TAG", "detector: " + e);
                }
                break;
            case "vetement":
                list = getArrayFromFile("labelVetement", context);
                bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
                try {
                    ModelVetement model = ModelVetement.newInstance(context);
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                    TensorImage tensorImage = new TensorImage(DataType.UINT8);
                    tensorImage.load(bitmap);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();
                    inputFeature0.loadBuffer(byteBuffer);

                    ModelVetement.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    model.close();

                    result = list[getMAx(outputFeature0.getFloatArray(), list.length)];
                } catch (IOException e) {
                    Log.d("TAG", "detector: " + e);
                }
                break;
            case "color":
                list = getArrayFromFile("labelCouleur", context);
                bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
                try {
                    ModelCouleur model = ModelCouleur.newInstance(context);

                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                    TensorImage tensorImage = new TensorImage(DataType.UINT8);
                    tensorImage.load(bitmap);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();
                    inputFeature0.loadBuffer(byteBuffer);

                    ModelCouleur.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    model.close();

                    result = list[getMAx(outputFeature0.getFloatArray(), list.length)];
                } catch (IOException e) {
                    Log.d("TAG", "detector: " + e);
                }
                break;
            case "produit":
                list = getArrayFromFile("labelProduit", context);
                bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
                try {
                    ModelProduit model = ModelProduit.newInstance(context);
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                    TensorImage tensorImage = new TensorImage(DataType.UINT8);
                    tensorImage.load(bitmap);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();
                    inputFeature0.loadBuffer(byteBuffer);
                    ModelProduit.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    model.close();

                    result = list[getMAx(outputFeature0.getFloatArray(), list.length)];
                } catch (IOException e) {
                    Log.d("TAG", "detector: " + e);
                }
                break;
        }

        return result;
    }

    private int getMAx(float[] arr, int length){
        int ind = 0;
        float min = 0.0f;

        for(int i=0; i<length;i++){
            if(arr[i] > min){
                ind = i;
                min = arr[i];
            }
        }
        return ind;
    }

    private String[] getArrayFromFile(String labelName, Context context){
        String list[] = null;
        String string = "";
        try {
            InputStream inputStream = context.getAssets().open( labelName + ".txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            string = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list = string.split("\n");
        return list;
    }

    public String[] getTextFromImage(Bitmap bitmap, Context context){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        StringBuilder stringBuilder = new StringBuilder();
        String language = null;
        if(!textRecognizer.isOperational()){
            Log.d("TAG", "getTextFromImage: Erreur");
        }
        else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frame);

            for(int i=0; i<textBlockSparseArray.size(); i++){
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                language = textBlock.getLanguage();
                stringBuilder.append(" ");

            }
        }
        String[] result = {stringBuilder.toString(), language};
        return result;
    }
}
