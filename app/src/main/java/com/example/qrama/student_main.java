package com.example.qrama;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;



public class student_main extends AppCompatActivity {

    TextView student_active_id;
    TextView student_active_fullname = null;
    TextView student_active_course = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        student_active_id = findViewById(R.id.student_active_id);
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String ASid = sharedPreferences.getString("value", "");
        student_active_id.setText(ASid);
        getData();
    }

    private void getData() {

        String url = Config5.DATA_URL + student_active_id.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(url, response -> showJSON(response), error -> Toast.makeText(student_main.this, error.getMessage(), Toast.LENGTH_LONG).show());
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void showJSON(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config5.JSON_ARRAY);
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String first_name = jo.getString(Config5.KEY_FIRSTNAME);
                String department = jo.getString(Config5.DEPARTMENT);
                student_active_fullname.setText(first_name);
                student_active_course.setText(department);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }}
