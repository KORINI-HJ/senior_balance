package org.tensorflow.lite.examples.classification.WorkoutData;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.examples.classification.R;

import java.util.ArrayList;

public class WorkoutAdapter extends BaseAdapter {
    Context context = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<WorkoutData> data_list;

    public WorkoutAdapter(Context _context, ArrayList<WorkoutData> _data_list) {
        this.context = _context;
        this.data_list = _data_list;
        this.mLayoutInflater = LayoutInflater.from(_context);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int index, View _view, ViewGroup viewGroup) {
        View view = mLayoutInflater.inflate(R.layout.workout_report_data, null);

        ImageView imageView = (ImageView)view.findViewById(R.id.exercise_img);
        TextView textViewDate = (TextView)view.findViewById(R.id.date);
        TextView textViewOutcome = (TextView)view.findViewById(R.id.outcome);

        Bitmap exerciseImg = data_list.get(index).getImg();
        imageView.setImageBitmap(exerciseImg);
        textViewDate.setText(data_list.get(index).getDate());
        textViewDate.setText(data_list.get(index).getOutcome());

        return view;
    }
}