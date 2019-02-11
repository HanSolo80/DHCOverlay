package com.innomob.dhcoverlay;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void clickedButton(View v) {
//        Runnable task2 = () -> {
//            Intent svc = new Intent(this, OverlayShowingService.class);
//            startService(svc);
//        };
//
//        new Thread(task2).start();

        Intent svc = new Intent(this, OverlayShowingService.class);
        startService(svc);

        //Toast.makeText(this, "Activity button click event", Toast.LENGTH_SHORT).show();
    }
}
