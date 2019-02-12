package com.innomob.dhcoverlay.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.innomob.dhcoverlay.R;

public class OverlayShowingService extends Service implements OnTouchListener, OnClickListener {

    private View topLeftView;

    private Drawable xDrawable;
    private Button overlayedButton;
    private ImageView xView;
    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private WindowManager wm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        overlayedButton = new Button(this);
        overlayedButton.setText("Overlay button");
        overlayedButton.setOnTouchListener(this);
        overlayedButton.setAlpha(0.5f);
        overlayedButton.setBackgroundColor(Color.GREEN);
        overlayedButton.setOnClickListener(this);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        wm.addView(overlayedButton, params);

        topLeftView = new View(this);
        WindowManager.LayoutParams topLeftParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        topLeftParams.gravity = Gravity.START | Gravity.TOP;
        topLeftParams.x = 0;
        topLeftParams.y = 0;
        topLeftParams.width = 0;
        topLeftParams.height = 0;
        wm.addView(topLeftView, topLeftParams);

        xView = new ImageView(this);
        xView.setVisibility(View.INVISIBLE);
        xDrawable = ContextCompat.getDrawable(this, R.drawable.ic_x_symbol);
        xView.setImageDrawable(paintDrawable(xDrawable, R.color.colorBlack));

        WindowManager.LayoutParams paramsX = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        paramsX.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        paramsX.y = 100;
        wm.addView(xView, paramsX);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayedButton != null) {
            wm.removeView(overlayedButton);
            wm.removeView(topLeftView);
            wm.removeView(xView);
            overlayedButton = null;
            topLeftView = null;
            xView = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        xView.setVisibility(View.VISIBLE);
        xView.setImageDrawable(paintDrawable(xDrawable, R.color.colorBlack));

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getRawX();
            float y = event.getRawY();

            moving = false;

            int[] location = new int[2];
            overlayedButton.getLocationOnScreen(location);

            originalXPos = location[0];
            originalYPos = location[1];

            offsetX = originalXPos - x;
            offsetY = originalYPos - y;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int[] topLeftLocationOnScreen = new int[2];
            topLeftView.getLocationOnScreen(topLeftLocationOnScreen);

            float x = event.getRawX();
            float y = event.getRawY();

            WindowManager.LayoutParams params = (LayoutParams) overlayedButton.getLayoutParams();

            int newX = (int) (offsetX + x);
            int newY = (int) (offsetY + y);

            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                return false;
            }

            params.x = newX - (topLeftLocationOnScreen[0]);
            params.y = newY - (topLeftLocationOnScreen[1]);

            if (hoverOverView(event, xView)) {
                xView.setImageDrawable(paintDrawable(xDrawable, R.color.colorRed));
                System.out.println("Change red");
            } else {
                xView.setImageDrawable(paintDrawable(xDrawable, R.color.colorBlack));
            }

            wm.updateViewLayout(overlayedButton, params);
            moving = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (moving) {

                if (hoverOverView(event, xView)) {
                    this.stopSelf();
                } else {
                    xView.setVisibility(View.INVISIBLE);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Overlay button click event", Toast.LENGTH_SHORT).show();
    }

    private boolean hoverOverView(MotionEvent event, View v) {
        int offset = 100;
        float x = event.getRawX();
        float y = event.getRawY();
        int[] posView = new int[2];
        v.getLocationOnScreen(posView);
        return x + offset >= posView[0] && x <= posView[0] + xView.getWidth() + offset &&
                y + offset >= posView[1] && y <= posView[1] + xView.getHeight() + offset;
    }

    private Drawable paintDrawable(Drawable drawable, @ColorRes int color) {
        Drawable mWrappedDrawable = drawable.mutate();
        mWrappedDrawable = DrawableCompat.wrap(mWrappedDrawable);
        DrawableCompat.setTint(mWrappedDrawable, getResources().getColor(color, null));
        DrawableCompat.setTintMode(mWrappedDrawable, PorterDuff.Mode.SRC_IN);
        return mWrappedDrawable;
    }

}
