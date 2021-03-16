package com.example.qrama;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Button teacherL;
    Button studentL;
    ImageView seclogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        teacherL = findViewById(R.id.teacherL);
        studentL = findViewById(R.id.studentL);
        seclogo = findViewById(R.id.seclogo);

        teacherL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, teacher_login.class);
                startActivity(intent);
            }
        });
        studentL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, student_login.class);
                startActivity(intent);
            }
        });

    }
}