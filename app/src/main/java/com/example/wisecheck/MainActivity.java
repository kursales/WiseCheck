package com.example.wisecheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSION_REQUEST = 100;
    RecyclerView recV;
    ImageButton btnAdd;
    final ArrayList<Employee> listEmployee = new ArrayList<Employee>();
    RecycleTurnAdapter adapter;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recV=findViewById(R.id.recV);
        btnAdd=findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        recV.setLayoutManager(new LinearLayoutManager(this));
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST);
        }



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAdd:
                Intent intent = new Intent(this, CreateActivity.class);
                startActivity(intent);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        new Thread(new Runnable(){
            @Override
            public void run() {
                AppDatabase db = App.getInstance().getDatabase();
                EmployeeDao employeeDao = db.employeeDao();
                List<Employee> employees = employeeDao.getAll();
                listEmployee.removeAll(listEmployee);
                listEmployee.addAll(employees);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        adapter = new RecycleTurnAdapter(listEmployee, new RecycleTurnAdapter.OnUserClickListener() {
                            @Override
                            public void onUserClick(Employee employee) {
                              new Dialog(MainActivity.this, listEmployee.indexOf(employee)).run();
                            }
                        });
                        recV.setAdapter(adapter);

                    }
                });
            }
        }).start();


    }

    }