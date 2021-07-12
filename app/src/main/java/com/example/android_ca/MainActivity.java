package com.example.android_ca;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(jpg|png))$)";
    List<String> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String URL = "https://stocksnap.io";

        Button fetch = findViewById(R.id.fetch);
        if (fetch != null){
            fetch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    int count = 0;
                    Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
                    Matcher m = p.matcher(URL);
                    while (m.find()) {
                        String srcResult = m.group(1);
                        Pattern p2 = Pattern.compile(EXTENSION_PATTERN);
                        Matcher img = p2.matcher(srcResult);
                        if (img.find()) {
                            list.add(srcResult);
                            count++;
                        }
                        if (count == 20) break;
                    }
                    startDownloadImage(list);
                }
            });
        }
    }

    protected void startDownloadImage(List<String> imgURLs){
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        List<File> destFile_list = new ArrayList<>();

        for(int i=0; i<20; i++){
            String destFilename = UUID.randomUUID().toString() + imgURLs.get(i).lastIndexOf(".") + i;
            File destFile = new File(dir, destFilename);
            destFile_list.add(destFile);
        }

        int[] viewId_list = {
                R.id.imageView11,R.id.imageView12,R.id.imageView13,R.id.imageView14,R.id.imageView21,
                R.id.imageView22,R.id.imageView23,R.id.imageView24,R.id.imageView31,R.id.imageView32,
                R.id.imageView33,R.id.imageView34,R.id.imageView41,R.id.imageView42,R.id.imageView43,
                R.id.imageView44,R.id.imageView51,R.id.imageView52,R.id.imageView53,R.id.imageView54
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageDownloader imgDL = new ImageDownloader();
                for(int k=0; k<20; k++){
                    if (imgDL.downloadImages(imgURLs.get(k), destFile_list.get(k))){
                        int finalK = k;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = BitmapFactory.decodeFile(destFile_list.get(finalK).getAbsolutePath());
                                ImageView imageView = findViewById(viewId_list[finalK]);
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                }

            }
        }).start();
    }
}
