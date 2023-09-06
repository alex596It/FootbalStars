package com.example.footbalstars;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
private TextView textView;
  private   String points;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        textView = findViewById(R.id.textViewnew);
        Intent intent = getIntent();
       points = String.valueOf(intent.getIntExtra("result",0));
        textView.setText("Вы набрали "+ points+" баллов из 25" );
    }

    public void onClickBack(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

    }
}