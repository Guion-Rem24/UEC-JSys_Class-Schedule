package com.mine.class_schedule.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mine.class_schedule.ClassTableView;
import com.mine.class_schedule.R;

public class HomeFragment extends Fragment {
    private final String TAG = "HOMEFRAGMENT";
    private HomeViewModel homeViewModel;
    private ClassTableView classTableView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v(TAG,"---- onCreate ----");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Home");
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        classTableView = root.findViewById(R.id.table_classView);
//        String class_text = classTableView.getClassText(0,0);

        classTableView.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "(w, h = "+classTableView.getWidth()+", "+classTableView.getHeight()+")");
//                Log.d(TAG, "certain class: "+class_text);
            }
        });
//        ClassTableView = new ClassTableView(getContext());

        Log.v(TAG, "====on CreateView ====");
        return root;
    }
}