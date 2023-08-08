package com.example.laravelapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class PrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        localStorage=new LocalStorage(PrincipalActivity.this);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        System.out.println("Token almacenado en LocalStorage: " + localStorage.getToken());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_clock:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClockFragment()).commit();
                break;
            case R.id.nav_alerts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AlertsFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    private void getUser(){
        String url=getString(R.string.api_server)+"/user";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http=new Http(PrincipalActivity.this,url);
                http.setToken(true);
                http.send();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code=http.getStatusCode();
                        if(code==200){
                            try{
                                JSONObject response=new JSONObject(http.getResponse());
                                String name=response.getString("name");
                                String email=response.getString("email");
                                String create_at=response.getString("created_at");

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void logout(){
        String url=getString(R.string.api_server)+"/logout";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http=new Http(PrincipalActivity.this,url);
                http.setMethod("post");
                http.setToken(true);
                http.send();

                Boolean tokenP=http.getToken();
                System.out.println("BOOLEAN TOKEEEEN: "+tokenP);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code= http.getStatusCode();
                        if(code==200){
                            Intent intent=new Intent(PrincipalActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(PrincipalActivity.this, "Error "+code, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }).start();
    }
}
