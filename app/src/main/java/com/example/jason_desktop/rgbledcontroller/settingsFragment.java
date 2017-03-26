package com.example.jason_desktop.rgbledcontroller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.example.jason_desktop.rgbledcontroller.MainActivity.REQUEST_ENABLE_BT;

/*
 * Created by Jason-Desktop on 3/24/2017.
 */

public class settingsFragment extends Fragment implements View.OnClickListener{
    final static String ARG_ANIMATION_NAME = "animation";

    final static int REQUEST_PICK_COLOR = 1;

    String animation = "";
    TextView test;
    settingSelectedListener mCallback;

    ConstraintLayout control_layout;
    ConstraintLayout brightness_control_layout;
    ConstraintLayout color_select_layout;
    ConstraintLayout hueCycle_layout;
    ConstraintLayout fadeTime_layout;
    ConstraintLayout patternCycle_layout;
    ConstraintLayout beat_layout;

    SeekBar brightness_seekbar;
    EditText et_hueCycle;
    EditText et_fadeTime;
    EditText et_patternCycle;
    EditText et_beat;

    int function = 0;
    int r = 0;
    int g = 0;
    int b = 0;
    int t_hueCycle = 20;
    int t_fadeTime = 20;
    int t_patternCycle = 20;
    int beat = 20;
    int brightness = 100;

    boolean isDefault = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button3:
                Log.d("settingFragment: ", "Selected " + animation);

                //function already read
                //r already read
                //g already read
                //b already read
                t_hueCycle = Integer.parseInt(et_hueCycle.getText().toString());
                t_fadeTime = Integer.parseInt(et_fadeTime.getText().toString());
                t_patternCycle = Integer.parseInt(et_patternCycle.getText().toString());
                beat = Integer.parseInt(et_beat.getText().toString());
                //brightness already read

                animation = convertToInput();
                mCallback.selected(animation);
                break;
            case R.id.btnColorSelect:
                Intent intent = new Intent(getActivity(), ColorSelector.class);
                intent.putExtra("red", r);
                startActivityForResult(intent, REQUEST_PICK_COLOR);
                break;
            default:
                break;
        }
    }

    String convertToInput(){
        String temp = "";

        String strFunction = Integer.toString(function);
        String strR = Integer.toString(r);
        String strG = Integer.toString(g);
        String strB = Integer.toString(b);
        String strT_hueCycle = Integer.toString(t_hueCycle );
        String strT_fadeTime = Integer.toString(t_fadeTime );
        String strT_patternCycle  = Integer.toString(t_patternCycle );
        String strBeat = Integer.toString(beat );
        String strBrightness = Integer.toString(brightness);

        if (isDefault){
            temp = strFunction.length() + strFunction + "00000000";
            Log.d("converToInput: ", temp);
        }else{
            temp += strFunction.length() + strFunction;
            temp += strR.length() + strR;
            temp += strG.length() + strG;
            temp += strB.length() + strB;
            temp += strT_hueCycle.length() + strT_hueCycle;
            temp += strT_fadeTime.length() + strT_fadeTime;
            temp += strT_patternCycle.length() + strT_patternCycle;
            temp += strBeat.length() + strBeat;
            temp += strBrightness.length() + strBrightness;
        }
        return temp;
    }







    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Check which request we're responding to
        if (requestCode == REQUEST_PICK_COLOR) {// Make sure the request was successful
            if (resultCode == RESULT_OK) {
                test.setBackgroundColor(data.getIntExtra("color", 0));
                r = data.getIntExtra("red", 0);
                g = data.getIntExtra("green", 0);
                b = data.getIntExtra("blue", 0);
                test.setText("r: " + r + " g: " + g + " b: " + b);
            }
        }
    }

    public interface settingSelectedListener {
        public void selected(String name);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (settingSelectedListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement settingSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_layout, container, false);
        Button buttonClick = (Button) view.findViewById(R.id.button3);
        buttonClick.setOnClickListener(this);
        Button btnColorSelect = (Button) view.findViewById(R.id.btnColorSelect);
        btnColorSelect.setOnClickListener(this);

        control_layout = (ConstraintLayout) view.findViewById(R.id.control_layout);
        brightness_control_layout = (ConstraintLayout) view.findViewById(R.id.brightness_control_layout);
        color_select_layout = (ConstraintLayout) view.findViewById(R.id.color_select_layout);
        hueCycle_layout = (ConstraintLayout) view.findViewById(R.id.hueCycle_layout);
        fadeTime_layout = (ConstraintLayout) view.findViewById(R.id.fadeTime_layout);
        patternCycle_layout = (ConstraintLayout) view.findViewById(R.id.patternCycle_layout);
        beat_layout = (ConstraintLayout) view.findViewById(R.id.beat_layout);

        et_hueCycle = (EditText) view.findViewById(R.id.et_hueCycle);
        et_fadeTime = (EditText) view.findViewById(R.id.et_fadeTime);
        et_patternCycle = (EditText) view.findViewById(R.id.et_patternCycle);
        et_beat = (EditText) view.findViewById(R.id.et_beat);

        test = (TextView) view.findViewById(R.id.color_preview);

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            animation = new String(savedInstanceState.getCharArray(ARG_ANIMATION_NAME));
        }

        final TextView seekBar_text = (TextView) view.findViewById(R.id.seekBar_text);
        brightness_seekbar = (SeekBar) view.findViewById(R.id.seekBar);
        brightness_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                seekBar_text.setText(progresValue+"");
                brightness = progresValue;
            }
            public void onStartTrackingTouch(SeekBar seekBar) {  }
            public void onStopTrackingTouch(SeekBar seekBar) {  }
        });

        Switch switch1 = (Switch) view.findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    control_layout.setVisibility(View.GONE);
                    isDefault = true;
                }else{
                    control_layout.setVisibility(View.VISIBLE);
                    isDefault = false;
                }
            }
        });



        return view;
    }


    /*
    PatternList patterns = {
0 SolidColor_rgb, SolidPulse_rgb, SolidRainbow, SolidRainbowPulse,
4 rainbow_singleStrip, rainbowWithGlitter, confetti, sinelon, juggle, bpm,
10 rainbow_twoStrip, rainbow_circle_twoStrip, sinelon_twoStrip, circle, circle_rainbow, alternate, alternate_opposite,
17 demoReel100
};
        brightness_control_layout.setVisibility(View.GONE);
        color_select_layout.setVisibility(View.GONE);
        hueCycle_layout.setVisibility(View.GONE);
        fadeTime_layout.setVisibility(View.GONE);
        patternCycle_layout.setVisibility(View.GONE);
        beat_layout.setVisibility(View.GONE);
        break;
     */


    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateArticleView(new String(args.getCharArray(ARG_ANIMATION_NAME)));
            function = args.getInt("function");

            switch (function){
                case 0:
                    hueCycle_layout.setVisibility(View.GONE);
                    fadeTime_layout.setVisibility(View.GONE);
                    patternCycle_layout.setVisibility(View.GONE);
                    beat_layout.setVisibility(View.GONE);
                    break;
                case 1:
                    hueCycle_layout.setVisibility(View.GONE);
                    patternCycle_layout.setVisibility(View.GONE);
                    beat_layout.setVisibility(View.GONE);
                    break;
                case 2:
                    color_select_layout.setVisibility(View.GONE);
                    fadeTime_layout.setVisibility(View.GONE);
                    patternCycle_layout.setVisibility(View.GONE);
                    beat_layout.setVisibility(View.GONE);
                    break;
                case 3:case 12:
                    color_select_layout.setVisibility(View.GONE);
                    patternCycle_layout.setVisibility(View.GONE);
                    beat_layout.setVisibility(View.GONE);
                    break;
                case 4:case 5:case 6:case 7:case 8:case 9:case 10:case 11:
                    color_select_layout.setVisibility(View.GONE);
                    fadeTime_layout.setVisibility(View.GONE);
                    patternCycle_layout.setVisibility(View.GONE);
                    beat_layout.setVisibility(View.GONE);
                    break;
                case 13:
                    hueCycle_layout.setVisibility(View.GONE);
                    beat_layout.setVisibility(View.GONE);
                    break;
                case 14:
                    color_select_layout.setVisibility(View.GONE);
                    beat_layout.setVisibility(View.GONE);
                    break;
                case 15:case 16:
                    hueCycle_layout.setVisibility(View.GONE);
                    beat_layout.setVisibility(View.GONE);
                    break;
                case 17:
                    color_select_layout.setVisibility(View.GONE);
                    fadeTime_layout.setVisibility(View.GONE);
                    patternCycle_layout.setVisibility(View.GONE);
                    beat_layout.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }else if (!Objects.equals(animation, "")) {
            updateArticleView(animation);
        }
    }

    public void updateArticleView(String name) {
        TextView text = (TextView) getActivity().findViewById(R.id.textView3);
        text.setText(name);
        animation = name;
        //text.setVisibility(View.GONE);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putCharArray(ARG_ANIMATION_NAME, animation.toCharArray());
    }
}
