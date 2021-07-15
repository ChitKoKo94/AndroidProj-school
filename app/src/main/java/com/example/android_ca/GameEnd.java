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

        Intent intent = getIntent();

        TextView winner = findViewById(R.id.winner);
        String winWho = intent.getStringExtra("winner");
        if (winWho != null)
            winner.setText(winWho);

        String finalMessage = intent.getStringExtra("message");
        TextView text = findViewById(R.id.timingMessage);
        if (text != null) {
            text.setText(finalMessage);
        }

        Button player2 = findViewById(R.id.player2);
        Button restart = findViewById(R.id.restartGameButton);
        Button main = findViewById(R.id.restartMainButton);

        //check if currently it is the end of the second palyer's turn
        Boolean pvp = intent.getBooleanExtra("pvp", false);

        // if pvp = true means this is the end of second player, hide Player2 button
        // if pvp = false means this is the end of player 1 turn, we show Player2 button to play
        if (pvp)
            player2.setVisibility(View.INVISIBLE);
        else
            player2.setVisibility(View.VISIBLE);

        main.setVisibility(View.VISIBLE);
        restart.setVisibility(View.VISIBLE);

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
                Intent intent = new Intent(GameEnd.this, GameTest.class);
                finish();
                startActivity(intent);
            }
        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameEnd.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }
}