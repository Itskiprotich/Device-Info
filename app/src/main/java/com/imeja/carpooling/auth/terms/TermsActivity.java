package com.imeja.carpooling.auth.terms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.imeja.carpooling.MainActivity;
import com.imeja.carpooling.R;
import com.imeja.carpooling.auth.login.LoginActivity;
import com.imeja.carpooling.auth.profile.ProfileActivity;

public class TermsActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    CheckBox terms;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        getSupportActionBar().setTitle("Terms");
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        floatingActionButton = findViewById(R.id.next);
        terms = findViewById(R.id.terms);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (terms.isChecked()) {
                    startActivity(new Intent(TermsActivity.this, LoginActivity.class));
                    TermsActivity.this.finish();
                } else {
                    Snackbar.make(coordinatorLayout, R.string.message_please_Accept_terms_and_conditions, Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_refresh, null).show();
                }
            }
        });
    }
}
