package com.example.android_ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameTest extends AppCompatActivity implements Chronometer.OnChronometerTickListener {

    private List<Bitmap> selectedImgs = new ArrayList<>();
    private List<Bitmap> duplicatedImgs = new ArrayList<>();
    private Bitmap[] bitmaparray = new Bitmap[12];
    private int firstClickId = -1;
    private int secondClickId = -1;
    Bitmap[] originalArray = new Bitmap[12];
    private int clickCounter = 0;
    private Chronometer chronometer;
    private int temp0, temp1, temp, counter;
    private String winner;
    private int[] viewId_list = {
        R.id.A1, R.id.A2, R.id.A3, R.id.A4, R.id.A5,
                R.id.A6, R.id.A7, R.id.A8, R.id.A9, R.id.A10,
                R.id.A11, R.id.A12
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        stopService(new Intent(getApplicationContext(), MusicService.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_test);

        //get the 6 saved images
        getSelectedImgs();
        //duplicate the 6 images
        duplicateImgs();
        //shuffle the 12 images
        Collections.shuffle(duplicatedImgs);
        // setting the shuffled images in to an array to track their position
        for(int i=0; i<12; i++)
        {
            bitmaparray[i] = duplicatedImgs.get(i);
        }

        //getting the default picture to hide each image
        Bitmap questionMarkPicture = BitmapFactory.decodeResource(getResources(), R.drawable.question);
        for (int i = 0; i < 12; i++) {
            originalArray[i] = questionMarkPicture;
        }

        // load question mark images onto views for first time
        refreshImgs();

        //setting clicking sound, successful match sound and fail match sound
        final MediaPlayer mp=MediaPlayer.create(this,R.raw.click_sound);
        final MediaPlayer successSound = MediaPlayer.create(this,R.raw.success);
        final MediaPlayer wrongSound = MediaPlayer.create(this,R.raw.fail);

        //setting the onclick listener for each of the imageViews
        for (int j =0; j < 12; j++){
            ImageView v = (ImageView)findViewById(viewId_list[j]);
            v.setImageBitmap(originalArray[j]);
            v.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mp.start();
                    //check if it is the 2nd click
                    if(firstClickId != -1){
                        for(int L = 0; L<12;L++){
                            if (viewId_list[L] == v.getId()) {
                                secondClickId = L;
                                System.out.println("SecondClick");

                                // flip image for second click
                                originalArray[secondClickId] = duplicatedImgs.get(secondClickId);
                                refreshImgs();

                                break;
                            }
                        }
                        // check whether two images match
                        TextView counterText = findViewById(R.id.count);
                        if(bitmaparray[firstClickId] == bitmaparray[secondClickId]){
                            v.setEnabled(false);
                            successSound.start();

                            // if match, increase counter
                            counter++;
                            counterText.setText(counter + " of 6 images matched");

                            // check for end game condition
                            //if number of matches equals to 6
                            if (counter == 6) {
                                //stop timer, and saves player's timing
                                chronometer.stop();

                                temp0 = Integer.parseInt(chronometer.getText().toString().split(":")[0]);
                                temp1 = Integer.parseInt(chronometer.getText().toString().split(":")[1]);
                                temp  = temp0*60+temp1;


                                // check if the second player is currently playing by checking if player1's timing
                                //has been passed from the end game screen
                                Intent intent = getIntent();
                                int player1 = intent.getIntExtra("P1Timing", 0);
                                Intent intentToGameEnd = new Intent(GameTest.this,GameEnd.class);

                                if (player1 != 0){
                                    if(player1 < temp)
                                        winner = "PLAYER 1 WINS";
                                    else if (player1 > temp)
                                        winner = "PLAYER 2 WINS";
                                    else
                                        winner ="DRAW";

                                    boolean pvp = true;

                                    //inform the endgame screen that it is multiplayer mode who the winner is based on
                                    // each player's time
                                    intentToGameEnd.putExtra("pvp", pvp);
                                    intentToGameEnd.putExtra("winner", winner);
                                }

                                //passing player's timing to the End Game Screen
                                String finalMessage = "Well done! You completed the game in " + temp + " seconds";
                                intentToGameEnd.putExtra("message", finalMessage);
                                intentToGameEnd.putExtra("P1Timing", temp);
                                startActivity(intentToGameEnd);
                            }
                        }
                        //if the pictures does not match
                        else {
                                wrongSound.start();
                                ImageView v1 = findViewById(viewId_list[firstClickId]);
                                //allow the first images to be clickable again
                                v1.setEnabled(true);
                                clickCounter++;

                                // if no match, change both images back to question marks
                                originalArray[firstClickId] = questionMarkPicture;
                                originalArray[secondClickId] = questionMarkPicture;
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshImgs();
                                    }
                                }, 1000);

                        }
                        firstClickId = -1;
                        secondClickId = -1;
                    }
                    // if this is the first click, we will get position of the click, and disable clicking the same image
                    else {
                        for (int k = 0; k<12; k++) {
                            //to check the position of the button clicked
                            if (viewId_list[k] == v.getId()) {
                                firstClickId = k;
                                //disable clicking the same image again
                                v.setEnabled(false);

                                //if this is the first time the player is clicking an image, start timer
                                if (clickCounter == 0) {
                                    chronometer = (Chronometer) findViewById(R.id.chronometer);
                                    chronometer.setOnChronometerTickListener(GameTest.this);
                                    chronometer.start();
                                    chronometer.setBase(SystemClock.elapsedRealtime());// reset
                                }
                                clickCounter++;

                                // flip image for first click
                                originalArray[firstClickId] = duplicatedImgs.get(firstClickId);
                                refreshImgs();

                                break;
                            }
                        }
                    }
                }

            });
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

    private void refreshImgs() {
        for (int i = 0; i < 12; i++) {
            ImageView v = (ImageView)findViewById(viewId_list[i]);
            v.setImageBitmap(originalArray[i]);
        }
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
    }
}