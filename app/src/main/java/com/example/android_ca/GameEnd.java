package com.example.android_ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

        Intent intent = getIntent();
        String finalMessage = intent.getStringExtra("message");
        TextView text = findViewById(R.id.timingMessage);
        if (text != null) {
            text.setText(finalMessage);
        }

        Button goBackBtn = findViewById(R.id.goBack);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameEnd.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}