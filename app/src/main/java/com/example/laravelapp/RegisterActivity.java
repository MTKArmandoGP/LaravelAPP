package com.example.laravelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText etNameRegister,etEmailRegister,etPasswordRegister,etPasswordConfirmationRegister;
    Button btnRegisterRegister,btnBack;

    String NameR,EmailR,PasswordR,PasswordCR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNameRegister=findViewById(R.id.name_register);
        etEmailRegister=findViewById(R.id.email_register);
        etPasswordRegister=findViewById(R.id.password_register);
        etPasswordConfirmationRegister=findViewById(R.id.passwordConfirmation_register);
        btnRegisterRegister=findViewById(R.id.btnResgisterRegister);
        btnBack=findViewById(R.id.btnBack);

        btnRegisterRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRegister();
            }
        });


    }

    private void checkRegister(){
        NameR=etNameRegister.getText().toString();
        EmailR=etEmailRegister.getText().toString();
        PasswordR=etPasswordRegister.getText().toString();
        PasswordCR=etPasswordConfirmationRegister.getText().toString();

        if(NameR.isEmpty()||EmailR.isEmpty()||PasswordR.isEmpty()){
            alertFail("Name,Email and Password are required");
        } else if (!PasswordR.equals(PasswordCR)) {
            alertFail("The passwords don´t match");
        }else{
            sendRegister();
        }
    }

    private void sendRegister() {
        JSONObject params= new JSONObject();
        try {
            params.put("name",NameR);
            params.put("email",EmailR);
            params.put("password",PasswordR);
            params.put("password_confirmation",PasswordCR);

        }catch (JSONException e){
            e.printStackTrace();
        }
        String data=params.toString();
        String url=getString(R.string.api_server)+"/register";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http=new Http(RegisterActivity.this,url);
                http.setMethod("post");
                http.setData(data);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code=http.getStatusCode();
                        if(code==201||code==200){
                            alertSuccess("Register Complete");
                        } else if (code==422) {
                            try{
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg=response.getString("message");
                                alertFail(msg );
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(RegisterActivity.this, "EROOR"+code, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void alertSuccess(String s) {
        new AlertDialog.Builder(this)
                .setTitle("SUCCESS")
                .setIcon(R.drawable.baseline_check_box_24)
                .setMessage(s)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                })
                .show();
    }

    private void alertFail(String s) {
        new AlertDialog.Builder(this)
                .setTitle("ERROR")
                .setIcon(R.drawable.baseline_warning_24)
                .setMessage(s)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }
}