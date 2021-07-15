package com.example.android_ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    //wjx

    private static final String EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(jpg|png))$)";
    List<String> list = new ArrayList<>();
    private boolean canDownload = true;
    ArrayList<ImageView> selectedImgs = new ArrayList<ImageView>();

    protected List<String> img_list = new ArrayList<>();
    protected Thread bkgThread;
    private Intent musicIntent;

    int[] viewId_list = {
            R.id.imageView11,R.id.imageView12,R.id.imageView13,R.id.imageView14,R.id.imageView21,
            R.id.imageView22,R.id.imageView23,R.id.imageView24,R.id.imageView31,R.id.imageView32,
            R.id.imageView33,R.id.imageView34,R.id.imageView41,R.id.imageView42,R.id.imageView43,
            R.id.imageView44,R.id.imageView51,R.id.imageView52,R.id.imageView53,R.id.imageView54
    };

    Button b1=findViewById(R.id.tag_flower);
    Button b2=findViewById(R.id.tag_love);
    Button b3=findViewById(R.id.tag_biz);
    Button b4=findViewById(R.id.tag_travel);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        String URL = "https://stocksnap.io";

        //music
        musicIntent = new Intent(getApplicationContext(), MusicService.class);
        startService(new Intent(getApplicationContext(), MusicService.class));


        Button fetch = findViewById(R.id.fetch);
        if (fetch != null){
            fetch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    img_list.clear();
                    EditText req_url = findViewById(R.id.url);
                    String url = req_url.getText().toString();
                    if (bkgThread != null) {
                        bkgThread.interrupt();
                        for(int p=0; p<20; p++) {
                            ImageView imageView = findViewById(viewId_list[p]);
                            imageView.setImageDrawable(getDrawable(R.drawable.sample));
                        }
                    } else {
                        for (int p = 0; p < 20; p++) {
                            ImageView imageView = findViewById(viewId_list[p]);
                            imageView.setImageDrawable(getDrawable(R.drawable.sample));
                        }
                    }
                    bkgThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            img_list.clear();
                            if(bkgThread.isInterrupted()){
                                canDownload = false;
                                return;
                            }
                            try {
                                Document document = Jsoup.connect(URL).get();
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
                            canDownload = true;
                            startDownloadImage(img_list);
                        }
                    });
                    bkgThread.start();
                }
            });
        }


        Drawable border = getDrawable( R.drawable.border);
        for(int id: viewId_list)
        {
            ImageView img = (ImageView)findViewById(id);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedImgs.contains(img)) {
                        selectedImgs.remove(img);
                        img.setBackgroundResource(0);
                    } else {
                        selectedImgs.add(img);
                        img.setBackground(border);
                    }

                    if (selectedImgs.size() == 6) {
                        saveImgs();
                        Intent intent = new Intent(MainActivity.this, GameTest.class);
                        startActivity(intent);
                    }
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
        for (int k = 0; k < imglist.size(); k++) {
            if (!canDownload) {
                break;
            }
            else if (imgDL.downloadImages(imglist.get(k), destFile_list.get(k))) {
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
        canDownload = true;
    }

    private void saveImgs(){
        for (int i = 0; i<6; i++){
            String imageName ="image" +i;
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = getApplicationContext().openFileOutput(imageName, Context.MODE_PRIVATE);
                Bitmap bitmap = ((BitmapDrawable)selectedImgs.get(i).getDrawable()).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
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
