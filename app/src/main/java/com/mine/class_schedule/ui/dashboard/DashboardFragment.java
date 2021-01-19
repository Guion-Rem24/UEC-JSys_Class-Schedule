package com.mine.class_schedule.ui.dashboard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mine.class_schedule.AlertService;
import com.mine.class_schedule.LayerService;
import com.mine.class_schedule.OverlayService;
import com.mine.class_schedule.View.MainActivity;
import com.mine.class_schedule.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    private final String TAG="DashboardFragment";
    FloatingActionButton fab;
    private static MainActivity activity;
    public static boolean activatedlayerService;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v(TAG,"---- onCreate ----");
        activity = MainActivity.current_pointer_;
        activatedlayerService = false;

        if(!activity.checkOverlayPermission()) activity.requestOverlayPermission();

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        fab = root.findViewById(R.id.fab);
        if(fab == null) {
            Log.v(TAG, "fab has null pointer");
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertService.startAlarm(activity.getApplicationContext());
            }
        });



        // sample Start Button ///////////////////////////
        Button startButton = root.findViewById(R.id.sample_start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v(TAG, "---- Clicked ----");
                Intent intent = new Intent(getActivity().getApplication(), OverlayService.class);
                LocalBroadcastManager lbManager = LocalBroadcastManager.getInstance(getActivity().getApplication());
                lbManager.sendBroadcast(intent);
                getActivity().startService(intent);
//                if(!activatedlayerService) {
//                    activatedlayerService = true;
//                    Intent intent = new Intent(getActivity(), OverlayService.class);
//                    PendingIntent pendingIntent = PendingIntent.getService( getContext(),
//                                                                            0,//MainActivity.REQUEST_SYSTEM_OVERLAY,
//                                                                            intent,
//                                                                            PendingIntent.FLAG_ONE_SHOT );
//                    AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
//                    manager.set(AlarmManager.ELAPSED_REALTIME, 3000, pendingIntent);
//                }
            }
        });
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();

    }
}