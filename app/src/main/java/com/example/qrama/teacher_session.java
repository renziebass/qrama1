package com.example.qrama;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.WriterException;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class teacher_session extends AppCompatActivity {

    TextView tvsubjectid;
    TextView tvsubdesc;
    TextView tvsubstarttime;
    TextView tvsubendtime;
    TextView tvsubday;
    TextView schedule_active_id;
    TextView tvCurrentDate;
    ImageView QRholder;
    TextClock textClock;
    TextView SessiontStarted;
    Button Bdismissed;
    Button Bcamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_session);

        Bdismissed = findViewById(R.id.DismissClass);
        Bcamera = findViewById(R.id.CameraClass);
        SessiontStarted = (TextView) findViewById(R.id.SessionTimeStarted);
        textClock = (TextClock)findViewById(R.id.textClock1);
        schedule_active_id = findViewById(R.id.schedule_active_id);
        tvsubjectid = findViewById(R.id.subject_active_id);
        tvsubdesc = findViewById(R.id.subject_active_des);
        tvsubstarttime = findViewById(R.id.subject_start_time);
        tvsubendtime = findViewById(R.id.subject_end_time);
        tvsubday = findViewById(R.id.subject_active_daysched);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        QRholder = findViewById(R.id.QRholder);


        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        tvCurrentDate.setText(currentDate);

        String currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());

        SessiontStarted.setText(currentTime);

        SharedPreferences sharedPreferences = getSharedPreferences("SAid", MODE_PRIVATE);
        String CSID = sharedPreferences.getString("value1", "");

        String qrvarcharvalue = currentDate + "-" + CSID;
        QRGEncoder qrgEncoder = new QRGEncoder(qrvarcharvalue, null, QRGContents.Type.TEXT, 500);
        Bitmap qrBits = qrgEncoder.getBitmap();
        QRholder.setImageBitmap(qrBits);

        TeacherClassInfo();
        CreateSession();
        Bcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teacher_session.this, teacher_camera.class);
                startActivity(intent);
            }
        });
        Bdismissed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("End Class Session?")
                .setMessage("Ending Session will Dismiss Class")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        teacher_session.super.onBackPressed();
                        EndSession();

                    }
                }).create().show();
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
    private void EndSession() {

        SharedPreferences sharedPreferences = getSharedPreferences("SAid", MODE_PRIVATE);
        String CSID = sharedPreferences.getString("value1", "");
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        SharedPreferences sharedPreferences1 = getSharedPreferences("myKey", MODE_PRIVATE);
        String ATid = sharedPreferences1.getString("value", "");

        String currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());

        String attendance_code = String.valueOf(currentDate + "-" + CSID);
        String time_ended = String.valueOf(currentTime).toString();

        String[] field = new String[2];
        field[0] = "attendance_code";
        field[1] = "time_ended";

        //Creating array for data
        String[] data = new String[2];
        data[0] = attendance_code;
        data[1] = time_ended;
        PutData putData = new PutData("http://192.168.254.119/qrama/teacher_end_session.php", "POST", field, data);
        if (putData.startPut()) {
            if (putData.onComplete()) {
                String result = putData.getResult();
                if (result.equals("Class Dismissed!")) {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                }
                //End ProgressBar (Set visibility to GONE)
            }
            //End Write and Read data with URL
        }
    }
    private void TeacherClassInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("SAid", MODE_PRIVATE);
        String CSID = sharedPreferences.getString("value1", "");

        String url= "http://192.168.254.119/QRAMA/get_class_info.php?schedule_id="+CSID;
        schedule_active_id.setText(CSID);
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
                                String start_time = jsonobject.getString("start_time");
                                String end_time = jsonobject.getString("end_time");
                                String day = jsonobject.getString("day");

                                tvsubjectid.setText(subject_id);
                                tvsubdesc.setText(description);
                                tvsubstarttime.setText(start_time);
                                tvsubendtime.setText(end_time);
                                tvsubday.setText(day);
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