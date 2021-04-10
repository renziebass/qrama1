package com.example.qrama;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;


public class student_main extends AppCompatActivity {

    Button AttendClassButton;
    Button Blogout;
    TextView student_active_id;
    TextView phpstudentfullname;
    TextView tvCurrentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        phpstudentfullname = findViewById(R.id.student_active_fullname);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        tvCurrentDate.setText(currentDate);

        AttendClassButton = findViewById(R.id.AttendClassButton);
        AttendClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(student_main.this, student_camera.class);
                startActivity(intent);
            }
        });
        Blogout = findViewById(R.id.logoutTAcc);
        Blogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        student_active_id = findViewById(R.id.student_active_id);
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String ASid = sharedPreferences.getString("value", "");
        student_active_id.setText(ASid);

        getStudentInfo();

    }
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        student_main.super.onBackPressed();
                    }
                }).create().show();
    }
    private void getStudentInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String ASid = sharedPreferences.getString("value", "");

        String url= "http://192.168.254.119/QRAMA/get_student_info.php?id="+ASid;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonarray = new JSONArray(response);
                            for(int i=0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                String firstname = jsonobject.getString("first_name");
                                String middlename = jsonobject.getString("middle_name");
                                String lastname = jsonobject.getString("last_name");
                                phpstudentfullname.setText(firstname+" "+middlename+" "+lastname);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null){
                            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

}


