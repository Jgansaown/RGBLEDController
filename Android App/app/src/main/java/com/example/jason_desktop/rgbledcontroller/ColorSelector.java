package com.example.jason_desktop.rgbledcontroller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.graphics.Color.HSVToColor;
import static android.graphics.Color.blue;
import static android.graphics.Color.colorToHSV;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.graphics.Color.rgb;

public class ColorSelector extends AppCompatActivity {
    private SeekBar red_seekBar;
    private SeekBar green_seekBar;
    private SeekBar blue_seekBar;
    private SeekBar hue_seekBar;
    private SeekBar saturation_seekBar;
    private SeekBar value_seekBar;

    private TextView red_progress;
    private TextView green_progress;
    private TextView blue_progress;
    private TextView hue_progress;
    private TextView saturation_progress;
    private TextView value_progress;

    private TextView previewTextView;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id== android.R.id.home ){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_selector);


        setTitle("Color Selector");


        initializeVariables();
        SeekBar_Setup();

        red_progress.setText(red_seekBar.getProgress()+"");
        green_progress.setText(green_seekBar.getProgress()+"");
        blue_progress.setText(blue_seekBar.getProgress()+"");

        //getWindow().setStatusBarColor(Color.BLUE);
        //getWindow().setNavigationBarColor(Color.YELLOW);
        //getWindow().setTitleColor();

    }

    // A private method to help us initialize our variables.
    private void initializeVariables() {
        red_seekBar = (SeekBar) findViewById(R.id.red_seekBar);
        green_seekBar = (SeekBar) findViewById(R.id.green_seekBar);
        blue_seekBar = (SeekBar) findViewById(R.id.blue_seekBar);

        hue_seekBar = (SeekBar) findViewById(R.id.hue_seekBar);
        saturation_seekBar = (SeekBar) findViewById(R.id.saturation_seekBar);
        value_seekBar = (SeekBar) findViewById(R.id.value_seekBar);

        red_progress = (TextView) findViewById(R.id.red_progress);
        green_progress = (TextView) findViewById(R.id.green_progress);
        blue_progress = (TextView) findViewById(R.id.blue_progress);

        hue_progress = (TextView) findViewById(R.id.hue_progress);
        saturation_progress = (TextView) findViewById(R.id.saturation_progress);
        value_progress = (TextView) findViewById(R.id.value_progress);

        previewTextView = (TextView) findViewById(R.id.preview_color);
    }
    //setup seekbars
    void SeekBar_Setup(){
        //rgb seekbar
        red_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean track = false;
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                red_progress.setText(Integer.toString(progresValue));
                if (fromUser){
                    changeColor_rgb(progresValue, green_seekBar.getProgress(), blue_seekBar.getProgress());
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {  }
            public void onStopTrackingTouch(SeekBar seekBar) {  }
        });
        green_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                green_progress.setText(Integer.toString(progresValue));
                if (fromUser){
                    changeColor_rgb(red_seekBar.getProgress(), progresValue, blue_seekBar.getProgress());
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        blue_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                blue_progress.setText(Integer.toString(progresValue));
                if (fromUser){
                    changeColor_rgb(red_seekBar.getProgress(), green_seekBar.getProgress(), progresValue);
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        //hsv seekbar
        hue_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                hue_progress.setText(Integer.toString(progresValue));
                if (fromUser){
                    changeColor_hsv(progresValue, saturation_seekBar.getProgress(), value_seekBar.getProgress());
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        saturation_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                saturation_progress.setText(Integer.toString(progresValue));
                if (fromUser){
                    changeColor_hsv(hue_seekBar.getProgress(), progresValue, value_seekBar.getProgress());
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        value_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                value_progress.setText(Integer.toString(progresValue));
                if (fromUser){
                    changeColor_hsv(hue_seekBar.getProgress(), saturation_seekBar.getProgress(), progresValue);
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    void changeColor_rgb(int r, int g, int b){
        float[] hsv = {0.0f,0.0f,0.0f};
        int color = rgb(r,g,b);
        colorToHSV(color, hsv);

        hue_seekBar.setProgress((int) hsv[0], true);
        saturation_seekBar.setProgress((int) (hsv[1]*100), true);
        value_seekBar.setProgress((int) (hsv[2]*100), true);

        previewTextView.setBackgroundColor(color);
    }
    void changeColor_hsv(int h, int s, int v){
        float[] hsvColor = {h, s/100.0f, v/100.0f};
        int color = HSVToColor(hsvColor);

        red_seekBar.setProgress(red(color), true);
        green_seekBar.setProgress(green(color), true);
        blue_seekBar.setProgress(blue(color), true);

        previewTextView.setBackgroundColor(color);
    }

    public void setColor(View view){
        Intent extraIntent = new Intent();
        extraIntent.putExtra("red", red_seekBar.getProgress());
        extraIntent.putExtra("green", green_seekBar.getProgress());
        extraIntent.putExtra("blue", blue_seekBar.getProgress());
        extraIntent.putExtra("color", rgb(red_seekBar.getProgress(), green_seekBar.getProgress(), blue_seekBar.getProgress()));
        setResult(RESULT_OK, extraIntent);
        finish();
    }
}
