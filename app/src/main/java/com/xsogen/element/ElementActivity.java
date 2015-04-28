package com.xsogen.element;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ElementActivity extends ActionBarActivity {
    Button btnShowLocation;
    com.xsogen.element.GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        final TextView geoLog = (TextView) findViewById(R.id.geoLog);

        geoLog.setMovementMethod(new ScrollingMovementMethod());
        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(ElementActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    geoLog.append("Lat: " + latitude + ", Long: " + longitude + "\n");
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });
    }

}
