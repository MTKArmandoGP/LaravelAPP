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

public class MainActivity extends AppCompatActivity {

    EditText etEmail,etPassword;
    Button btnLogin,btnRegister;
    String email_login,password_login;

    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localStorage=new LocalStorage(MainActivity.this);

        etEmail=findViewById(R.id.email_login);
        etPassword=findViewById(R.id.password_login);
        btnLogin=findViewById(R.id.btnLogin);
        btnRegister=findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);

            }
        });
    }

    private void checkLogin() {

        email_login=etEmail.getText().toString();
        password_login=etPassword.getText().toString();
        if(email_login.isEmpty()||password_login.isEmpty()){
            alertFail("Porfavor llena todos los campos");
        }else{
            sendLogin();
        }


    }

    private void sendLogin() {
        JSONObject params=new JSONObject();
        try{
            params.put("email",email_login);
            params.put("password",password_login);
        }catch (JSONException e){
            e.printStackTrace();
        }
        String data = params.toString();
        String url=getString(R.string.api_server)+"/login";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(MainActivity.this,url);
                http.setMethod("post");
                http.setData(data);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if(code==200){
                            try {
                                JSONObject response=new JSONObject(http.getResponse());
                                String token=response.getString("token");
                                System.out.println("TOKEEEN: "+token);
                                localStorage.setToken(token);
                                System.out.println("Token almacenado en LocalStorage: " + localStorage.getToken());
                                Intent intent = new Intent(MainActivity.this,PrincipalActivity.class);
                                startActivity(intent);
                                finish();

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        } else if (code==402) {
                            try {
                                JSONObject response=new JSONObject(http.getResponse());
                                String msg=response.getString("message");
                                alertFail(msg);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }else if(code==401){
                            try {
                                JSONObject response=new JSONObject(http.getResponse());
                                String msg=response.getString("message");
                                alertFail(msg);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "Error "+code, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();

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