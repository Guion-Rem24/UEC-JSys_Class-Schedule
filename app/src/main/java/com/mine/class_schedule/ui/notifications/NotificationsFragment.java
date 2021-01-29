package com.mine.class_schedule.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mine.class_schedule.AlarmIntegrator;
import com.mine.class_schedule.Model.Alarm.Alarm;
import com.mine.class_schedule.Model.Alarm.AlarmListAdapter;
import com.mine.class_schedule.Model.Alarm.TYPE_ALARM;
import com.mine.class_schedule.Model.MyClass.MyClass;
import com.mine.class_schedule.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private final String TAG="NotificationsFragment";

    private List<Alarm> alarmList;
    private AlarmIntegrator alarmIntegrator;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v(TAG,"---- onCreate ----");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_alarm);
        final AlarmListAdapter adapter = new AlarmListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        alarmIntegrator = new AlarmIntegrator(getContext());


        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        notificationsViewModel.getAllClasses().observe(getViewLifecycleOwner(), new Observer<List<MyClass>>() {
            @Override
            public void onChanged(List<MyClass> classList) {
                for(MyClass c : classList){
                    int num = c.getAlertNum();
                    for(int i=0;i<num;i++){
                        notificationsViewModel.insert(alarmIntegrator.createNewAlarm(c, i));
                    }
                    for(int i=num;i<3;i++){
                        notificationsViewModel.deleteAlarmOf(c.getClassPos() + TYPE_ALARM.getAlarm(i));
                        alarmIntegrator.cancelAlarmOf(c, i);
                    }
                }
            }
        });

        notificationsViewModel.getAllAlarms().observe(getViewLifecycleOwner(), new Observer<List<Alarm>>() {
            @Override
            public void onChanged(@NonNull final List<Alarm> alarms) {
                Log.d(TAG, "[onChanged] alarmList");
//                notificationsViewModel.deleteAllAlarm();
                alarmList = alarms;
                adapter.setAlarms(alarmList);
            }
        });
        return root;
    }


}