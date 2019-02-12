package com.innomob.dhcoverlay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.innomob.dhcoverlay.service.OverlayShowingService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void clickedButton(View v) {
        Intent svc = new Intent(this, OverlayShowingService.class);
        startService(svc);
    }
}
