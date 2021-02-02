package com.mine.class_schedule.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mine.class_schedule.AlarmIntegrator;
import com.mine.class_schedule.Model.Alarm.Alarm;
import com.mine.class_schedule.Model.Alarm.TYPE_ALARM;
import com.mine.class_schedule.Model.MyClass.MyClass;
import com.mine.class_schedule.R;
import com.mine.class_schedule.View.MainActivity;
import com.mine.class_schedule.ui.classview.ClassTableView;
import com.mine.class_schedule.ui.classview.ClassView;

import java.util.List;

public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";
    private HomeViewModel homeViewModel;
    private ClassTableView classTableView;
    private AlarmIntegrator alarmIntegrator;
    private View root;
    private List<MyClass> classList;
    private List<Alarm> alarmList;

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
        ((MainActivity) getActivity()).setViewModel(homeViewModel);

        alarmIntegrator = new AlarmIntegrator(getContext());

        homeViewModel.getAllClasses().observe(getViewLifecycleOwner(), new Observer<List<MyClass>>() {
            @Override
            public void onChanged(List<MyClass> classes) {
                Log.d(TAG, "[Observe] List<MyClass> is Changed");
                if(classes == null){
                    Log.d(TAG,"classes is null");
                }
                classList = classes;
                classTableView.setClassData(true);
                // UI update
                classTableView.updateClassesUI(classes);

                for(MyClass c : classes){
                    int num = c.getAlertNum();
                    for(int i=0;i<num;i++){
                        homeViewModel.insert(alarmIntegrator.createNewAlarm(c, i));
                    }
                    for(int i=num;i<3;i++){
                        homeViewModel.deleteAlarmOf(c.getClassPos() + TYPE_ALARM.getAlarm(i));
                        alarmIntegrator.cancelAlarmOf(c, i);
                    }
                }
            }
        });

        if(homeViewModel.getAllAlarms() != null){
            homeViewModel.getAllAlarms().observe(getViewLifecycleOwner(), new Observer<List<Alarm>>() {
                @Override
                public void onChanged(List<Alarm> alarms) {
                    Log.d(TAG, "[Observe] List<Alarm> is Changed");
                    if (alarms == null) {
                        Log.d(TAG, "alarms is null");
                    }
                }
            });
        }

        root = inflater.inflate(R.layout.fragment_home, container, false);
        classTableView = root.findViewById(R.id.table_classView);

        classTableView.post(new Runnable() {
            @Override
            public void run() {
                classTableView.setLayoutSize(classTableView.getWidth(), classTableView.getHeight());
                Log.d(TAG, "(w, h = "+classTableView.getWidth()+", "+classTableView.getHeight()+")");
            }
        });



        Button sampleButton = root.findViewById(R.id.sample_button);
        sampleButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ClassView view = classTableView.getClass(0,0);
                byte Id = view.getPosId();
            }

        });

        Log.v(TAG, "====on CreateView ====");
        return root;
    }


    public MyClass getMyClass(byte posId){
        MyClass result = null;
        for(MyClass c : classList){
//            Log.v(TAG, "posId: "+posId+", c.getClassPos: "+c.getClassPos());
            if((int)c.getClassPos() == (int)posId){
//                Log.v(TAG, "getMyClass() of "+posId+" exists");
                result = c;
                break;
            }
        }
        return result;
    }
}