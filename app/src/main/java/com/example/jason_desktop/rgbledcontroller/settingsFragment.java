package com.example.jason_desktop.rgbledcontroller;

//import android.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

/*
 * Created by Jason-Desktop on 3/24/2017.
 */

public class settingsFragment extends Fragment implements View.OnClickListener{
    final static String ARG_ANIMATION_NAME = "animation";
    String animation = "";

    settingSelectedListener mCallback;

    @Override
    public void onClick(View v) {
        Log.d("settingFragment: ", "Selected " + animation);
        mCallback.selected(animation);
    }

    public interface settingSelectedListener {
        public void selected(String name);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (settingSelectedListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + "must implement settingSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_layout, container, false);
        Button buttonClick = (Button) view.findViewById(R.id.button3);
        buttonClick.setOnClickListener(this);

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            animation = new String(savedInstanceState.getCharArray(ARG_ANIMATION_NAME));
        }

        //return super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

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
        }else if (!Objects.equals(animation, "")) {
            updateArticleView(animation);
        }
    }

    public void updateArticleView(String name) {
        TextView text = (TextView) getActivity().findViewById(R.id.textView3);
        text.setText(name);
        animation = name;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putCharArray(ARG_ANIMATION_NAME, animation.toCharArray());
    }
}
