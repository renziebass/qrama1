package com.example.qrama;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class student_login extends AppCompatActivity {

    TextView notStudent;
    Button BLoginStudent;
    TextInputEditText TIELoginTextID;
    TextInputEditText TIELoginTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        final EditText userID = findViewById(R.id.LoginTextID);

        TIELoginTextID = findViewById(R.id.LoginTextID);
        TIELoginTextPassword = findViewById(R.id.LoginTextPassword);

        notStudent = findViewById(R.id.notStudent);
        notStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(student_login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        BLoginStudent = findViewById(R.id.LoginStudent);
        BLoginStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id, password;

                String CuserID = userID.getText().toString().trim();
                SharedPreferences sharedPref = getSharedPreferences("myKey", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("value", CuserID);
                editor.apply();

                id = String.valueOf(TIELoginTextID.getText());
                password = String.valueOf((TIELoginTextPassword.getText()));

                if(!id.equals("") && !password.equals("")) {

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String [] field = new String [2];
                            field[0] = "id";
                            field[1] = "password";
                            String [] data = new String [2];
                            data[0] = id;
                            data [1] = password;
                            PutData putData = new PutData("http://192.168.254.119/qrama/student_login.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if (result.equals("Login Success")) {
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), student_main.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                }

            }
        });
    }
}
