package com.phoon.phoon.Momento;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by Hello on 12/4/2017.
 */

public class SettingActivity extends AppCompatActivity {

    SharedPreferences preferences;
    Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        preferences = this.getSharedPreferences("notificationSetting", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSpinner = (Spinner) findViewById(R.id.spinnerColor);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this, R.array.notif_color, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        if (preferences.getString("notification", "").equals("blue")) {
            mSpinner.setSelection(0);
        } else if (preferences.getString("notification", "").equals("green")){
            mSpinner.setSelection(1);
        }
        else{
            mSpinner.setSelection(2);
        }

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(
                    AdapterView parent, View view, int position, long id) {
                if (id == 0) {
                    preferences.edit().putString("notification", "blue").apply();
                    Snackbar.make(mSpinner , "LED light is now set to BLUE", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (id == 1){
                    preferences.edit().putString("notification", "green").apply();
                    Snackbar.make(mSpinner , "LED light is now set to GREEN", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    preferences.edit().putString("notification", "yellow").apply();
                    Snackbar.make(mSpinner , "LED light is now set to YELLOW", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            public void onNothingSelected(AdapterView parent) {
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
