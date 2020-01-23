package com.imeja.carpooling.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.imeja.carpooling.MainActivity;
import com.imeja.carpooling.R;
import com.imeja.carpooling.model.RealmUtils;

public class SettingsActivity extends AppCompatActivity {
    ListView home;
    Toolbar toolbar;
    Context context;
    String[] values = new String[]{"Security ", "Ride", "Communication",
            "Notification", "Favourites", "Route Groups",
            "Account", "Vacation", "Resent to Default",
            "Logout"};
    int[] images = {
            R.drawable.ic_security_black_24dp,
            R.drawable.ic_directions_car_black_24dp,
            R.drawable.ic_chat_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_favorite_black_24dp,
            R.drawable.ic_supervisor_account_black_24dp,
            R.drawable.ic_vpn_key_black_24dp,
            R.drawable.ic_date_range_black_24dp,
            R.drawable.ic_history_black_24dp,
            R.drawable.ic_power_settings_new_black_24dp};
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        home = findViewById(R.id.listView);
        MyAdapter adapter = new MyAdapter(SettingsActivity.this, values, images);
        home.setAdapter(adapter);
        home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {

                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:

                        break;
                    case 3:
                        break;
                    case 4:

                        break;
                    case 5:

                        break;
                    case 6:

                        break;
                    case 7:

                        break;
                    case 8:

                        break;
                    case 9:
                        popUp();
                        break;

                }

            }
        });

    }

    private void popUp() {
        alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage("Are you sure you want to Log Out?");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        RealmUtils.setLogged(false);
                        finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.LeftRightDialogTheme;
        alertDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private class MyAdapter extends ArrayAdapter {
        Context context;
        String[] values;
        int[] imageArray;

        public MyAdapter(Context context, String[] values, int[] imageArray) {
            super(context, R.layout.settings, R.id.bb, values);
            this.context = context;
            this.values = values;
            this.imageArray = imageArray;
        }

        @NonNull
        @Override
        public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.settings, parent, false);
            ImageView imageView = row.findViewById(R.id.aa);
            TextView textView = row.findViewById(R.id.bb);
            imageView.setImageResource(imageArray[position]);
            textView.setText(values[position]);
            return row;
        }
    }
}