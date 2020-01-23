package com.imeja.carpooling.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.imeja.carpooling.R;
import com.imeja.carpooling.auth.login.LoginActivity;

public class Hallfinder extends AppCompatActivity implements View.OnClickListener {

    private Button mSignOutButton;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hallfinder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSignOutButton = (Button) findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }
    private void signOut() {
        mAuth.signOut();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_out_button:
                signOut();
                Intent i = new Intent(Hallfinder.this, LoginActivity.class);
                startActivity(i);
                break;
        }
    }
}
