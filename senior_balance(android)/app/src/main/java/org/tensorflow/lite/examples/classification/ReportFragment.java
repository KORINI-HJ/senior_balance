package org.tensorflow.lite.examples.classification;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.examples.classification.WorkoutData.WorkoutAdapter;
import org.tensorflow.lite.examples.classification.WorkoutData.WorkoutData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view;
    TextView textViewTitle;
    //
    private DatabaseReference mDatabase;

    String name;
    private LineChart lineChart;

    ArrayList<WorkoutData> data_list;
    WorkoutAdapter workoutAdapter;
    // for graph
    //

    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_report, container, false);
        view = (View)inflater.inflate(R.layout.fragment_report, null);

        textViewTitle = view.findViewById(R.id.report_title);
        // get_data from DB
        get_data_from_DB();
        return view;
    }

    private void setChart()
    {
        ArrayList<String> arrayWeekDate= new ArrayList<>();
        int[] arrayWeekCount = {0, 0, 0, 0, 0, 0, 0};
        int[] arrayWeekWrongCount = {0, 0, 0, 0, 0, 0, 0};

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        SimpleDateFormat dateFormatFromDB = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -6);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        for(int i = 0; i < data_list.size(); i++)
        {
            Calendar cal_one = Calendar.getInstance();
            try{
                Date date = dateFormatFromDB.parse(data_list.get(i).getDate());
                cal_one.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long day_index = (cal_one.getTimeInMillis() - cal.getTimeInMillis()) / (24*60*60*1000);
            if(day_index >= 0 && day_index < 7)
            {
                arrayWeekCount[(int)day_index] += data_list.get(i).getCount();
                arrayWeekWrongCount[(int)day_index] += data_list.get(i).getWrongCount();
            }
        }
        //-------------------------------------------------------------------
        List<Entry> countEntries = new ArrayList<>();
        List<Entry> wrongCountEntries = new ArrayList<>();
        for(int i = 0; i < 7; i++)
        {
            arrayWeekDate.add(dateFormat.format(cal.getTime()));
            countEntries.add(new Entry(i, (int)(arrayWeekCount[i]*1.5)));
            wrongCountEntries.add(new Entry(i, arrayWeekWrongCount[i]));
            cal.add(Calendar.DATE, 1);
        }
        //-------------------------------------------------------------------
        LineDataSet lineCountSet = new LineDataSet(countEntries, "바르게 운동한 시간(초)");
        LineDataSet lineWrongSet = new LineDataSet(wrongCountEntries, "틀린 시간(초)");

        lineCountSet.setColor(Color.GREEN);
        lineCountSet.setCircleColor(Color.GREEN);
        lineWrongSet.setColor(Color.RED);
        lineWrongSet.setCircleColor(Color.RED);

        LineData chartData = new LineData();
        chartData.addDataSet(lineWrongSet);
        chartData.addDataSet(lineCountSet);
        lineChart.setData(chartData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return arrayWeekDate.get((int)value);
            }
        });


        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setEnabled(false);

        lineChart.setDoubleTapToZoomEnabled(false);
        //lineChart.setBackgroundColor(Color.BLACK);
        lineChart.invalidate();
    }

    private FirebaseDatabase database;
    private DatabaseReference refUserWorkout;
    private DatabaseReference refUser;
    String email_origin;
    String email;
    private void get_data_from_DB()
    {
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if (User!=null)
        {
            email_origin = User.getEmail();
            email = email_origin.split("@")[0];
            email = email.substring(3);
        }
        else {
            email = "NULL";
        }
        
        database = FirebaseDatabase.getInstance();

        refUser = database.getReference().child("User").child(email_origin.split("@")[0]); // 변경값을 확인할 child 이름
        refUserWorkout = database.getReference("User/" + email + "/WorkOut");

        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                HashMap<String, String> UserInfoMap = new HashMap<String, String>();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    String key = child.getKey();
                    String value = child.getValue().toString();
                    UserInfoMap.put(key,value);
                }
                name = UserInfoMap.get("name");
                textViewTitle.setText(name + "님의 운동기록");
                Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });

        data_list = new ArrayList<WorkoutData>();
        //DatabaseReference refWorkout = refUserWorkout.child("WorkOut");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    WorkoutData workoutData = child.getValue(WorkoutData.class);
                    String value = workoutData.getDate() + "_" + workoutData.getExercise() + "_" + Integer.toString(workoutData.getCount()) + "_" + Integer.toString(workoutData.getWrongCount());
                    data_list.add(workoutData);
                    //data_list.add(new WorkoutData(workoutData.getDate(), workoutData.getExercise(), workoutData.count, workoutData.wrongCount));
                }
                lineChart = (LineChart)view.findViewById(R.id.line_chart);
                setChart();
                workoutAdapter = new WorkoutAdapter(getActivity(), data_list);
                ListView listView = (ListView)view.findViewById(R.id.workout_data_list);
                listView.setAdapter(workoutAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        refUserWorkout.addValueEventListener(postListener);
    }
}
