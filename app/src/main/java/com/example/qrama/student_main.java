package com.example.qrama;

import androidx.appcompat.app.AppCompatActivity;
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



public class student_main extends AppCompatActivity {

    Button AttendClassButton;
    TextView student_active_id;
    TextView phpfullname;
    TextView phpcourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        phpcourse = findViewById(R.id.student_active_course);
        phpfullname = findViewById(R.id.student_active_fullname);

        AttendClassButton = findViewById(R.id.AttendClassButton);
        AttendClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(student_main.this, student_camera.class);
                startActivity(intent);
            }
        });
        student_active_id = findViewById(R.id.student_active_id);
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String ASid = sharedPreferences.getString("value", "");
        student_active_id.setText(ASid);

        getSqlDetails();

    }
    private void getSqlDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String ASid = sharedPreferences.getString("value", "");

        String url= "http://192.168.254.194/QRAMA/student_main.php?id="+ASid;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonarray = new JSONArray(response);
                            for(int i=0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                String name = jsonobject.getString("first_name");
                                String department = jsonobject.getString("department");
                                phpfullname.setText(name);
                                phpcourse.setText(department);

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


