package org.tensorflow.lite.examples.classification.WorkoutData;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.examples.classification.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkoutAdapter extends BaseAdapter {
    Context context = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<WorkoutData> data_list;
    int[] icons = {R.drawable.siticon, R.drawable.standicon, R.drawable.layicon, R.drawable.toolicon};

    public WorkoutAdapter(Context _context, ArrayList<WorkoutData> _data_list) {
        this.context = _context;
        this.data_list = _data_list;
        this.mLayoutInflater = LayoutInflater.from(_context);
    }

    @Override
    public int getCount() {
        return data_list.size();
    }

    @Override
    public Object getItem(int index) {
        return data_list.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View _view, ViewGroup viewGroup) {
        View view = mLayoutInflater.inflate(R.layout.workout_report_data, null);

        ImageView imageView = (ImageView)view.findViewById(R.id.exercise_img);
        TextView textViewDate = (TextView)view.findViewById(R.id.date);
        TextView textViewOutcome = (TextView)view.findViewById(R.id.outcome);

        int exercise = data_list.get(index).getExercise();
        int exerciseImg = icons[exercise];

        imageView.setImageResource(exerciseImg);

        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        SimpleDateFormat showFormat = new SimpleDateFormat("MM월dd일 HH:mm");

        Date date = new Date();
        try {
            date = parseFormat.parse(data_list.get(index).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String date_str = showFormat.format(date);

        textViewDate.setText(date_str);

        int workout_time = (int)(data_list.get(index).getCount() * 1.5);
        String outcome = Integer.toString(workout_time) + "초";
        textViewOutcome.setText(outcome);

        return view;
    }
}