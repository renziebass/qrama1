package com.example.qrama;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;

public class student_session extends AppCompatActivity {
    Button Btimeout;
    TextView phpstudentfullname;
    TextView phpstudentid;

    TextView phpsubjectid;
    TextView phpsubdesc;
    TextView phpsubtimestarted;

    TextView studentstatus;
    TextView tvCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_session);

        phpstudentfullname = findViewById(R.id.student_active_fullname);
        phpstudentid = findViewById(R.id.student_active_id);

        phpsubjectid = findViewById(R.id.sttvsubjectid);
        phpsubdesc = findViewById(R.id.sttvsubdesc);
        phpsubtimestarted = findViewById(R.id.sttvsubtimestarted);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        studentstatus  =findViewById(R.id.student_active_status);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        tvCurrentDate.setText(currentDate);

        getScannedClassInfo();
        getStudentInfo();
        Btimeout = findViewById(R.id.bstudent_timeout);
        Btimeout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    private void CreateSession() {

        SharedPreferences sharedPreferences = getSharedPreferences("SAid", MODE_PRIVATE);
        String CSID = sharedPreferences.getString("value1", "");
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        SharedPreferences sharedPreferences1 = getSharedPreferences("myKey", MODE_PRIVATE);
        String ATid = sharedPreferences1.getString("value", "");

        String currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());

        String attendance_code = String.valueOf(currentDate + "-" + CSID);
        String sched_id = String.valueOf(CSID);
        String teacher_id = String.valueOf(ATid);
        String time_started = String.valueOf(currentTime);
        String date = String.valueOf(DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));

        String[] field = new String[5];
        field[0] = "attendance_code";
        field[1] = "sched_id";
        field[2] = "teacher_id";
        field[3] = "time_started";
        field[4] = "date";
        //Creating array for data
        String[] data = new String[5];
        data[0] = attendance_code;
        data[1] = sched_id;
        data[2] = teacher_id;
        data[3] = time_started;
        data[4] = date;
        PutData putData = new PutData("http://192.168.254.119/qrama/teacher_create_session.php", "POST", field, data);
        if (putData.startPut()) {
            if (putData.onComplete()) {
                String result = putData.getResult();
                if (result.equals("Class Started")) {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                }
                //End ProgressBar (Set visibility to GONE)
            }
            //End Write and Read data with URL
        }
    }
    private void getScannedClassInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("ScannedQR", MODE_PRIVATE);
        String SQR = sharedPreferences.getString("value3", "");

        String url= "http://192.168.254.119/QRAMA/student_session.php?ac="+sharedPreferences.getString("value3", "");
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonarray = new JSONArray(response);
                            for(int i=0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                String subject_id = jsonobject.getString("subject_id");
                                String description = jsonobject.getString("description");
                                String time_started = jsonobject.getString("time_started");
                                phpsubjectid.setText(subject_id);
                                phpsubdesc.setText(description);
                                phpsubtimestarted.setText(time_started);
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
                            Toast.makeText(getApplicationContext(), "Failed to get Class Info!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Dismiss")
                .setMessage("Are you sure you want to Dismiss Class?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        student_session.super.onBackPressed();
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

                                String id = jsonobject.getString("id");
                                String firstname = jsonobject.getString("first_name");
                                String middlename = jsonobject.getString("middle_name");
                                String lastname = jsonobject.getString("last_name");

                                phpstudentfullname.setText(firstname+" "+middlename+" "+lastname);
                                phpstudentid.setText(id);
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
                            Toast.makeText(getApplicationContext(), "Failed to Get Student Info", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}