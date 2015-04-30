package com.xsogen.element;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ElementActivity extends ActionBarActivity {
    Button btnShowLocation;
    com.xsogen.element.GPSTracker gps;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);

        dbHelper = DatabaseHelper.getInstance(this);

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

                    // TODO: is this right? why am I using 1 for id and its working?
                    LocationRecord loc_rec = new LocationRecord(1, latitude, longitude);
                    dbHelper.getLocationDatabase().addLocation(loc_rec);
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
