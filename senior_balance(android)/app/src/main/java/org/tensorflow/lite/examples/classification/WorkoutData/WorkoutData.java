package org.tensorflow.lite.examples.classification.WorkoutData;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class WorkoutData {
    private int exercise;
    private String date;
    public int count;
    public int wrongCount;

    public WorkoutData()
    {}

    public WorkoutData(String _date, int _exercise, int _count, int _wrongCount)
    {
        this.date = _date;
        this.exercise = _exercise;
        this.count = _count;
        this.wrongCount = _wrongCount;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("exercise", exercise);
        result.put("count", count);
        result.put("wrongCount", wrongCount);
        return result;
    }


    public int getExercise() { return this.exercise; }

    public String getDate()
    {
        return this.date;
    }

    public int getCount() { return this.count; }

    public int getWrongCount() { return this.wrongCount; }
}
