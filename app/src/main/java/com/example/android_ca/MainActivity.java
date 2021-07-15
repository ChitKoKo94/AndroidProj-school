package com.example.android_ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    protected List<String> img_list = new ArrayList<>();
    protected Thread bkgThread;
    private Intent musicIntent;




    int[] viewId_list = {
            R.id.imageView11,R.id.imageView12,R.id.imageView13,R.id.imageView14,R.id.imageView21,
            R.id.imageView22,R.id.imageView23,R.id.imageView24,R.id.imageView31,R.id.imageView32,
            R.id.imageView33,R.id.imageView34,R.id.imageView41,R.id.imageView42,R.id.imageView43,
            R.id.imageView44,R.id.imageView51,R.id.imageView52,R.id.imageView53,R.id.imageView54
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //background music
        musicIntent = new Intent(getApplicationContext(), MusicService.class);
        startService(new Intent(getApplicationContext(), MusicService.class));


        Button b1=findViewById(R.id.tag_flower);
        Button b2=findViewById(R.id.tag_love);
        Button b3=findViewById(R.id.tag_biz);
        Button b4=findViewById(R.id.tag_travel);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="https://stocksnap.io//search/flower";
                EditText req_url=findViewById(R.id.url);
                req_url.setText(url);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="https://stocksnap.io//search/love";
                EditText req_url=findViewById(R.id.url);
                req_url.setText(url);

            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="https://stocksnap.io//search/business";
                EditText req_url=findViewById(R.id.url);
                req_url.setText(url);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="https://stocksnap.io//search/travel";
                EditText req_url=findViewById(R.id.url);
                req_url.setText(url);
            }
        });



        Button fetch = findViewById(R.id.fetch);
        if (fetch != null){

            fetch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){

                    img_list.clear();
                    EditText req_url = findViewById(R.id.url);
                    String url = req_url.getText().toString();
                    if (bkgThread != null)
                        bkgThread.interrupt();
                    {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(int p=0; p<20; p++) {


                                    ImageView imageView = findViewById(viewId_list[p]);
                                    imageView.setImageDrawable(getDrawable(R.drawable.sample));
                                }
                            }
                        });

                    }
                    bkgThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                Document document = Jsoup.connect(url).get();
                                Elements tags = document.getElementsByTag("img");
                                String src;
                                for(int i=0; i<tags.size(); i++){



                                    src = tags.get(i).attr("src");
                                    if (src.contains(".png") || src.contains(".jpg")){
                                        img_list.add(src);
                                    }
                                    if (img_list.size() == 20) break;
                                }
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                            startDownloadImage(img_list);
                        }
                    });
                    bkgThread.start();
                }
            });
        }
    }

    protected void startDownloadImage(List<String> imglist){
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        List<File> destFile_list = new ArrayList<>();
        TextView status = findViewById(R.id.protext);
        ProgressBar probar = findViewById(R.id.probar);

        for(int i=0; i<20; i++){

            String destFilename = UUID.randomUUID().toString() + imglist.get(i).lastIndexOf(".") + i;
            File destFile = new File(dir, destFilename);
            destFile_list.add(destFile);
        }

        ImageDownloader imgDL = new ImageDownloader();
        for (int k = 0; k < 20; k++) {
            if(bkgThread.isInterrupted())
                return;


            if (imgDL.downloadImages(imglist.get(k), destFile_list.get(k))) {
                int finalK = k;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {



                        Bitmap bitmap = BitmapFactory.decodeFile(destFile_list.get(finalK).getAbsolutePath());
                        ImageView imageView = findViewById(viewId_list[finalK]);
                        imageView.setImageBitmap(bitmap);

                        status.setVisibility(View.VISIBLE);
                        status.setText(finalK+1+"/20 images downloaded...");
                        probar.setVisibility(View.VISIBLE);
                        probar.setProgress((finalK+1)*5);
                    }
                });
            }
        }
    }

//    protected void progressbar(int num){
//        TextView status = findViewById(R.id.protext);
//        ProgressBar probar = findViewById(R.id.probar);
//
//        status.setVisibility(View.VISIBLE);
//        status.setText(num+"/20 images downloaded...");
//        probar.setVisibility(View.VISIBLE);
//        probar.setProgress(num);
//    }
}
