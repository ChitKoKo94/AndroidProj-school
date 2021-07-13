package com.example.android_ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class GameActivity extends AppCompatActivity {

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridView = findViewById(R.id.grid_view);
        Bitmap hiddenPicture = BitmapFactory.decodeFile("\\app\\src\\main\\res\\drawable");
        Bitmap[] hiddenPictures = {hiddenPicture, hiddenPicture, hiddenPicture, hiddenPicture
                , hiddenPicture, hiddenPicture, hiddenPicture, hiddenPicture
                , hiddenPicture, hiddenPicture, hiddenPicture, hiddenPicture};

//        MyAdapter adapter = new MyAdapter(this, hiddenPictures);
//        gridView.setAdapter(adapter);

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                MyAdapter adapter = (MyAdapter) gridView.getAdapter();
//
//            }
//        });
    }
}