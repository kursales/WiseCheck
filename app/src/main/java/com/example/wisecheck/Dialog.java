package com.example.wisecheck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;
import java.util.List;

public class Dialog {
    private Context context;
    private int id;
    AlertDialog.Builder ad;
    ArrayList<Employee> listEmployee = new ArrayList<Employee>();
    public Dialog(Context context,int id) {
        this.context = context;
        this.id=id;
    }

    public void run() {
        String message = "select action";
        String button1String = "Redact";
        String button2String = "Delete";

        ad = new AlertDialog.Builder(context);
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent intent = new Intent(context,RedactActivity.class);
                intent.putExtra("id",id);
                context.startActivity(intent);
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                update();
                Intent intent = new Intent(context,Main2Activity.class);
                context.startActivity(intent);



            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });
        ad.show();

    }
    public  void  update(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = App.getInstance().getDatabase();
                EmployeeDao employeeDao = db.employeeDao();
                final List<Employee> employees = employeeDao.getAll();
                listEmployee.addAll(employees);
                employeeDao.delete(listEmployee.get(id));
            }
        }).start();
    }
}



