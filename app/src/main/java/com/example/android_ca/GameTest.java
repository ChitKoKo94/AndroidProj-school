package com.example.android_ca;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class GameTest extends AppCompatActivity {

    private List<Bitmap> selectedImgs = new ArrayList<>();
    private List<Bitmap> duplicatedImgs = new ArrayList<>();
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_test);

        getSelectedImgs();
        duplicateImgs();

        int[] viewId_list = {
                R.id.imageView11,R.id.imageView12,R.id.imageView13,R.id.imageView14,R.id.imageView21,
                R.id.imageView22,R.id.imageView23,R.id.imageView24,R.id.imageView31,R.id.imageView32,
                R.id.imageView33,R.id.imageView34
        };

        for(int id: viewId_list)
        {
            ImageView v = (ImageView)findViewById(id);
            v.setImageBitmap(duplicatedImgs.get(count));
            count ++;
        }

    }

    private void getSelectedImgs(){
        for (int i = 0; i < 6; i++) {
            String name = "image" + i;
            FileInputStream fileInputStream;
            Bitmap bitmap = null;
            try {
                fileInputStream = getApplicationContext().openFileInput(name);
                bitmap = BitmapFactory.decodeStream(fileInputStream);
                selectedImgs.add(bitmap);
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void duplicateImgs()
    {
        for(Bitmap img:selectedImgs){
            Bitmap copiedimg = img;
            duplicatedImgs.add(img);
            duplicatedImgs.add(copiedimg);
        }
    }


}