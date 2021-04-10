package com.example.qrama;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class teacher_main extends AppCompatActivity {

    TextView teacher_active_id;
    TextView phpteacherfullname;
    TextView phpteacherdepartment;
    ListView listView_schedules;
    TextInputEditText inputschedid;
    Button BStart;
    Button BLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        final EditText schedID = findViewById(R.id.inputTextSchedule);

        phpteacherdepartment = findViewById(R.id.teacher_active_department);
        phpteacherfullname = findViewById(R.id.teacher_active_fullname);


        teacher_active_id = findViewById(R.id.teacher_active_id);

        SharedPreferences sharedPreferences1 = getSharedPreferences("myKey", MODE_PRIVATE);
        String ATid = sharedPreferences1.getString("value", "");

        teacher_active_id.setText(ATid);

        getTeacherInfo();
        getTeacherSchedules();

        inputschedid = findViewById(R.id.inputTextSchedule);

        BStart = findViewById(R.id.start_class);
        BStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sched_id, user_id;

                sched_id = String.valueOf(inputschedid.getText());
                user_id = String.valueOf((teacher_active_id.getText()));

                String CSID = schedID.getText().toString().trim();
                SharedPreferences sharedPref = getSharedPreferences("SAid", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("value1", CSID);
                editor.apply();

                if(!sched_id.equals("") && !user_id.equals("")) {

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String [] field = new String [2];
                            field[0] = "sched_id";
                            field[1] = "user_id";
                            String [] data = new String [2];
                            data[0] = sched_id;
                            data [1] = user_id;
                            PutData putData = new PutData("http://192.168.254.119/qrama/start_schedule.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if (result.equals("Schedule Found")) {
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), teacher_session.class);
                                        startActivity(intent);
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
        BLogout = findViewById(R.id.logoutTAcc);
        BLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        teacher_main.super.onBackPressed();
                    }
                }).create().show();
    }
    private void getTeacherSchedules() {
        listView_schedules = findViewById(R.id.listView_schedules);

        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String ASid = sharedPreferences.getString("value", "");

        String url= "http://192.168.254.119/QRAMA/get_teacher_schedules.php?teacher_id="+ASid;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                        try {
                            JSONArray jsonarray = new JSONArray(response);
                            for(int i=0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                String sched_id = jsonobject.getString(Config5.SCHEDULE_ID);
                                String teacher_id = jsonobject.getString(Config5.USER_ID);
                                String description = jsonobject.getString(Config5.SUBJECT_DES);
                                String start_time = jsonobject.getString(Config5.START_TIME);
                                String end_time = jsonobject.getString(Config5.END_TIME);
                                String day = jsonobject.getString(Config5.DAY);

                                final HashMap<String, String> schedules = new HashMap<>();
                                schedules.put(Config5.SCHEDULE_ID, sched_id);
                                schedules.put(Config5.USER_ID, teacher_id);
                                schedules.put(Config5.SUBJECT_DES, description);
                                schedules.put(Config5.START_TIME, start_time);
                                schedules.put(Config5.END_TIME, end_time);
                                schedules.put(Config5.DAY, day);

                                list.add(schedules);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ListAdapter adapter = new SimpleAdapter(
                                teacher_main.this, list, R.layout.teacher_schedules_list,
                                new String[]{Config5.SCHEDULE_ID, Config5.SUBJECT_DES, Config5.START_TIME, Config5.END_TIME, Config5.DAY},
                                new int[]{R.id.phptv_sched_id, R.id.phptv_subject_des, R.id.phptv_starttime, R.id.phptv_endtime,R.id.phptv_day});

                        listView_schedules.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null){
                            Toast.makeText(getApplicationContext(), "Failed to get Teacher Schedules!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void getTeacherInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String ASid = sharedPreferences.getString("value", "");

        String url= "http://192.168.254.119/QRAMA/get_teacher_info.php?id="+ASid;
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
                                String department = jsonobject.getString("department");
                                phpteacherfullname.setText(firstname+" "+middlename+" "+lastname);
                                phpteacherdepartment.setText(department);
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
                            Toast.makeText(getApplicationContext(), "Failed to get Teacher Information!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

}


