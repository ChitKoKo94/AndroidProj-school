package com.example.android_ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameEnd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        final MediaPlayer mp=MediaPlayer.create(this,R.raw.complete);
        mp.start();

        //check if there is a winner, and display winner
        Intent intent = getIntent();
        TextView winner = findViewById(R.id.winner);
        String winWho = intent.getStringExtra("winner");
        if (winWho != null)
            winner.setText(winWho);

        //display completion message and player's timing
        String finalMessage = intent.getStringExtra("message");
        TextView text = findViewById(R.id.timingMessage);
        if (text != null) {
            text.setText(finalMessage);
        }

        //display options after game ends
        Button player2 = findViewById(R.id.player2);
        Button restart = findViewById(R.id.restartGameButton);
        Button main = findViewById(R.id.restartMainButton);

        //check if currently it is the end of the second player's turn
        Boolean pvp = intent.getBooleanExtra("pvp", false);

        // if pvp = true, it means this is the end of second player, hide Player2 button.
        // if pvp = false, it means this is the end of player 1 turn, we show multiplayer option
        if (pvp)
            player2.setVisibility(View.INVISIBLE);
        else
            player2.setVisibility(View.VISIBLE);

        // restart the game meant for the next player to play
        player2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //passing Player1's timing back to gametest if they select to play against another player
                Intent intent = getIntent();
                int p1time = intent.getIntExtra("P1Timing", 0);
                Intent intentToGameTest = new Intent(GameEnd.this,GameTest.class);
                intentToGameTest.putExtra("P1Timing", p1time);
                finish();
                startActivity(intentToGameTest);
            }
        });

        // restart btn will restart game
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reset timings
                int p1time = 0;
                Intent intentToGameTest = new Intent(GameEnd.this, GameTest.class);
                intentToGameTest.putExtra("P1Timing", p1time);
                finish();
                startActivity(intentToGameTest);
            }
        });

        //main button returns to image retrieval page
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reset timings
                int p1time = 0;
                Intent intentToGameTest = new Intent(GameEnd.this, MainActivity.class);
                intentToGameTest.putExtra("P1Timing", p1time);
                finish();
                startActivity(intentToGameTest);
            }
        });
    }
}