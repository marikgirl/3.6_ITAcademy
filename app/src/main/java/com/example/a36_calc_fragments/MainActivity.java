package com.example.a36_calc_fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MessageFragment fragment =  (MessageFragment)getFragmentManager().findFragmentById(R.id.fragment_answer);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String value = extras.getString("value");
            if(value !=null && fragment != null && fragment.isInLayout()){
                fragment.setMessage(value);
            }
        }
    }
}