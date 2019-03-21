package com.wrbug.componentrouter.acomponent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.wrbug.componentrouter.annotation.MethodRouter;
import com.wrbug.componentrouter.annotation.ObjectRoute;

@ObjectRoute("/a/AFragment")
public class AFragment extends Fragment {
    private EditText et;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        et = view.findViewById(R.id.et);
    }

    @MethodRouter("getText")
    String getText() {
        return et.getText().toString();
    }

}
