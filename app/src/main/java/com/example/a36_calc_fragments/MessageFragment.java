package com.example.a36_calc_fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessageFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_fragment, container, false);
    }

    public void setMessage(String item){
        TextView message = (TextView) getView().findViewById(R.id.message);
        message.setText(item);
    }
}
