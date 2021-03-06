package com.example.android_ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
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


public class MainActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();
    private List<File> destFile_list = new ArrayList<>();
    protected List<String> img_list = new ArrayList<>();
    private ArrayList<ImageView> selectedImgs = new ArrayList<ImageView>();
    private boolean canDownload = true;
    private long mLastClickTime = 0;
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

        //music
        musicIntent = new Intent(getApplicationContext(), MusicService.class);
        startService(new Intent(getApplicationContext(), MusicService.class));

        //Adding function to the "Fetch" button
        Button fetch = findViewById(R.id.fetch);

        if (fetch != null){
            fetch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    //prevent double clicks
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    //clear image files if fetching is not the first time
                    if(!destFile_list.isEmpty())
                        destFile_list.clear();

                    // load all default images while images from link being downloaded
                    for(int i:viewId_list){
                        ImageView img = findViewById(i);
                        img.setVisibility(View.VISIBLE);
                    }

                    //ensure that the list of image urls is empty before download start
                    img_list.clear();
                    EditText req_url = findViewById(R.id.url);
                    String url = req_url.getText().toString();

                    //if there is a background thread running, we interrupt the background thread.
                    if (bkgThread != null) {
                        bkgThread.interrupt();
                        //reset image place holders
                        for(int p=0; p<20; p++) {
                            ImageView imageView = findViewById(viewId_list[p]);
                            imageView.setImageDrawable(getDrawable(R.drawable.sample));
                        }
                    } else {
                        //reset image placeholders
                        for (int p = 0; p < 20; p++) {
                            ImageView imageView = findViewById(viewId_list[p]);
                            imageView.setImageDrawable(getDrawable(R.drawable.sample));
                        }
                    }

                    //starting the background thread to download images whenever "fetch" is clicked
                    bkgThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            img_list.clear();
                            if(bkgThread.isInterrupted()){
                                //boolean to check if can start download or not.
                                //if thread is interrupted, stop download
                                canDownload = false;
                                return;
                            }
                            //using Jsoup library method to find the image urls
                            try {
                                Document document = Jsoup.connect(url).get();
                                Elements tags = document.getElementsByTag("img");
                                String src;
                                for(int i=0; i<tags.size(); i++){
                                    src = tags.get(i).attr("src");
                                    //looking for .png and .jpg files
                                    if (src.contains(".png") || src.contains(".jpg")){
                                        img_list.add(src);
                                    }
                                    //once 20 images found, break;
                                    if (img_list.size() == 20) break;
                                }
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                            canDownload = true;
                            // start download images
                            startDownloadImage(img_list);
                        }
                    });
                    bkgThread.start();
                }
            });
        }

        //set borders when selecting images
        Drawable border = getDrawable( R.drawable.border);
        //set clicking sound
        final MediaPlayer mp = MediaPlayer.create(this,R.raw.click_sound);
        //set on click listener to the downloaded images
        for(int id: viewId_list)
        {
            ImageView img = (ImageView)findViewById(id);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //start background music
                    mp.start();
                    //unselect Image
                    if (selectedImgs.contains(img)) {
                        selectedImgs.remove(img);
                        img.setBackgroundResource(0);
                    //select image
                    } else {
                        selectedImgs.add(img);
                        img.setBackground(border);
                    }

                    //start next activity upon choosing 6th image
                    if (selectedImgs.size() == 6) {
                        saveImgs();
                        Intent intent = new Intent(MainActivity.this, GameTest.class);
                        finish();
                        startActivity(intent);
                    }
                }
            });

            //navigation buttons to select other image categories
            Button b1=findViewById(R.id.tag_flower);
            Button b2=findViewById(R.id.tag_love);
            Button b3=findViewById(R.id.tag_biz);
            Button b4=findViewById(R.id.tag_travel);
            EditText req_url=findViewById(R.id.url);

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
                        return;
                    }

                    String url="https://stocksnap.io/search/flower";
                    req_url.setText(url);
                    fetch.performClick();

                }
            });
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
                        return;
                    }

                    String url="https://stocksnap.io/search/love";
                    req_url.setText(url);
                    fetch.performClick();

                }
            });
            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
                        return;
                    }

                    String url="https://stocksnap.io/search/business";
                    req_url.setText(url);
                    fetch.performClick();
                }
            });
            b4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
                        return;
                    }

                    String url="https://stocksnap.io/search/travel";
                    req_url.setText(url);
                    fetch.performClick();
                }
            });
        }

    }

    //Downloading the 20 images
    protected void startDownloadImage(List<String> imglist){
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        TextView status = findViewById(R.id.protext);
        ProgressBar probar = findViewById(R.id.probar);

        for(int i=0; i<20; i++){
            String destFilename = UUID.randomUUID().toString() + imglist.get(i).lastIndexOf(".") + i;
            File destFile = new File(dir, destFilename);
            destFile_list.add(destFile);
        }

        // use downloadImages method from ImageDownloader class to download images 1 by 1
        ImageDownloader imgDL = new ImageDownloader();
        for (int k = 0; k < imglist.size(); k++) {
            // check the interruption of the thread before downloading an image
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
    }

    //save the 6 select images to internal storage
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
}
