package com.wrbug.componentrouter.bcomponent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wrbug.componentrouter.annotation.MethodRouter;
import com.wrbug.componentrouter.annotation.ObjectRoute;

@ObjectRoute("/b/bfragment")
public class BFragment extends Fragment {
    TextView tv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_b, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tv = view.findViewById(R.id.tv);
    }

    @MethodRouter("setText")
    public void setText(String text) {
        tv.setText(text);
    }

}
