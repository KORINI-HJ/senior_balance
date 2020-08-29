package org.tensorflow.lite.examples.classification;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.tensorflow.lite.examples.classification.WorkoutData.WorkoutAdapter;
import org.tensorflow.lite.examples.classification.WorkoutData.WorkoutData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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

    //
    String name;
    private LineChart lineChart;

    ArrayList<WorkoutData> data_list;
    WorkoutAdapter workoutAdapter;
    // for graph
    ArrayList<String> arrayWeekDate= new ArrayList<>();
    int[] arrayWeekCount = {0, 0, 0, 0, 0, 0, 0};
    int[] arrayWeekWrongCount = {0, 0, 0, 0, 0, 0, 0};
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

        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if (User!=null) {
            // User is signed in.
            name = User.getDisplayName();
        }
        else{
            name = "OOO";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_report, container, false);
        View view = (View)inflater.inflate(R.layout.fragment_report, null);

        TextView textViewTitle = view.findViewById(R.id.report_title);
        textViewTitle.setText(name + "님의 운동기록");

        // get_data from DB
        data_list = new ArrayList<WorkoutData>();
        data_list.add(new WorkoutData(0, "2020-08-24 20:00:00", 10, 5));
        data_list.add(new WorkoutData(1, "2020-08-25 23:00:00", 40,7));
        data_list.add(new WorkoutData(2, "2020-08-29 11:00:00", 30,4));
        data_list.add(new WorkoutData(3, "2020-08-21 20:55:00", 60,2));

        workoutAdapter = new WorkoutAdapter(getActivity(), data_list);


        lineChart = (LineChart)view.findViewById(R.id.line_chart);
        setChart();

        ListView listView = (ListView)view.findViewById(R.id.workout_data_list);
        listView.setAdapter(workoutAdapter);

        return view;
    }

    private void setChart()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        SimpleDateFormat dateFormatFromDB = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat testingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
            //Toast.makeText(getContext(), Integer.toString((int)day_index), Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), testingFormat.format(cal_one.getTime()) + "\n" + testingFormat.format(cal.getTime()) + "\n" + Integer.toString((int)day_index), Toast.LENGTH_SHORT).show();
            if(day_index >= 0 && day_index < 7)
            {
                arrayWeekCount[(int)day_index] += data_list.get(i).getCount();
                arrayWeekWrongCount[(int)day_index] += data_list.get(i).getWrongCount();
            }
        }
        List<Entry> countEntries = new ArrayList<>();
        for(int i = 0; i < 7; i++)
        {
            arrayWeekDate.add(dateFormat.format(cal.getTime()));
            countEntries.add(new Entry(i, arrayWeekCount[i]));
            cal.add(Calendar.DATE, 1);
        }


        LineDataSet lineDataSet = new LineDataSet(countEntries, "Count");
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return arrayWeekDate.get((int)value);
            }
        });


        YAxis yAxis = lineChart.getAxisLeft();

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setEnabled(false);

        lineChart.setDoubleTapToZoomEnabled(false);
        //lineChart.setBackgroundColor(Color.BLACK);
        lineChart.invalidate();
    }
}
