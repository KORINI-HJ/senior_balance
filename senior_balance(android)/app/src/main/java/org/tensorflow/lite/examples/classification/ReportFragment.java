package org.tensorflow.lite.examples.classification;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.tensorflow.lite.examples.classification.WorkoutData.WorkoutAdapter;
import org.tensorflow.lite.examples.classification.WorkoutData.WorkoutData;

import java.util.ArrayList;


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


    ArrayList<WorkoutData> data_list;
    WorkoutAdapter workoutAdapter;

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
        View view = (View)inflater.inflate(R.layout.fragment_report, null);

        data_list = new ArrayList<WorkoutData>();
        data_list.add(new WorkoutData(0, "2020-08-01", "5/10 성공"));
        data_list.add(new WorkoutData(1, "2020-08-02", "8/10 성공"));
        data_list.add(new WorkoutData(2, "2020-08-03", "4/10 성공"));
        data_list.add(new WorkoutData(3, "2020-08-04", "7/10 성공"));
        // get_data
        workoutAdapter = new WorkoutAdapter(getActivity(), data_list);

        ListView listView = (ListView)view.findViewById(R.id.workout_data_list);
        listView.setAdapter(workoutAdapter);

        return view;
    }
}
