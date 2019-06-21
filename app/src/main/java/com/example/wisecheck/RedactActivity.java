package com.example.wisecheck;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RedactActivity extends AppCompatActivity implements View.OnClickListener {
    String[] spinnerData = {"Программист", "Бухгалтер", "Менеджер", "Уборщик", "Системный администратор"};
    EditText edFio, edUri;
    Button btnCreate,edDate;
    String path, imageName,myHost, myPath;
     int id;
    ArrayList<Employee> listEmployee = new ArrayList<Employee>();
    Spinner spinner;

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener dateD = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };
    private void updateLabel() {

        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        edDate.setText(sdf.format(myCalendar.getTime()));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        edFio = findViewById(R.id.edt_fio);
        edDate = findViewById(R.id.edt_Bday);
        edUri = findViewById(R.id.edt_uri);
        btnCreate = findViewById(R.id.btnCreateClient);
        btnCreate.setOnClickListener(this);
        edDate.setOnClickListener(this);
        spinner=findViewById(R.id.spinner);
        btnCreate.setText("Редактировать");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        fillinfFields();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edt_Bday:
                new DatePickerDialog(RedactActivity.this, dateD, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.btnCreateClient:
                parseUri(edUri.getText().toString());
                downloadFile(edUri.getText().toString());


        }
    }
    public  void fillinfFields(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                AppDatabase db = App.getInstance().getDatabase();
                EmployeeDao employeeDao = db.employeeDao();
                List<Employee> employees = employeeDao.getAll();
                listEmployee.addAll(employees);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        edFio.setText(listEmployee.get(id).getName());
                        edDate.setText(listEmployee.get(id).getBirthday());
                        edUri.setText(listEmployee.get(id).getUrl());
                        for (int j = 0; j<spinnerData.length-1; j++) {

                            if (spinnerData[j].equals(listEmployee.get(id).getProfession())) {
                                spinner.setSelection(j);
                                break;
                            }
                        }
                    }
                });
            }
        }).start();
    }
    public void update(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = App.getInstance().getDatabase();
                EmployeeDao employeeDao = db.employeeDao();
                List<Employee> employees = employeeDao.getAll();
                listEmployee.addAll(employees);
                listEmployee.get(id).setName(edFio.getText().toString());
                listEmployee.get(id).setBirthday(edDate.getText().toString());
                listEmployee.get(id).setProfession(spinner.getSelectedItem().toString());
                listEmployee.get(id).setPathToFile(path);
                listEmployee.get(id).setUrl(edUri.getText().toString());
                employeeDao.update(listEmployee.get(id));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        }).start();
    }
    private void downloadFile(String path) {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(myHost);
        Retrofit retrofit = builder.build();
        FileDownoloadClient fileDownoloadClient = retrofit.create(FileDownoloadClient.class);
        Call<ResponseBody> call = fileDownoloadClient.downoloadFile(edUri.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                 writeResponceBodyToDisk(response.body());
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RedactActivity.this, "Отсутствует интернет-соединение", Toast.LENGTH_SHORT).show();
                Log.d("stud","ua ua ua");

            }
        });

    }
    private boolean writeResponceBodyToDisk(ResponseBody body) {
        try {

            File myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    edFio.getText().toString()+imageName);
            path = myFile.getAbsolutePath();
            InputStream inputStream = null;
            OutputStream outputStream = null;
            Log.d("stud","file created");
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownaloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(myFile);
                while (true) {
                    Log.d("stud","file loaded");
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader,0,read);
                    fileSizeDownaloaded += read;
                    Log.d("stud","download "+ fileSizeDownaloaded+ "of"+fileSize);
                }
                outputStream.flush();
                update();
                return true;
            } catch (NullPointerException e) {
                Toast.makeText(RedactActivity.this,"Неверный URL", Toast.LENGTH_SHORT).show();
                return false;
            } finally {
                Log.d("stud","StreamDelete");
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }

            }
        } catch (IOException e) {
            Log.d("stud","ua ua ua");
            return false;
        }
    }
    public void parseUri(String url){
        String[] host = url.split("/",0);
        myHost=host[0]+"/"+host[1]+"/"+host[2]+"/";
        imageName=host[host.length-1];
        for (int i =3 ;i<host.length;i++){
            if(i!= host.length-2) {
                myPath += host[i] + "/";
            }
            else {
                myPath += host[i];
            }

        }
    }

}
