package org.tensorflow.lite.examples.classification.WorkoutData;

public class WorkoutData {
    private int exercise;
    private String date;
    public int count;
    public int wrongCount;


    public WorkoutData(int _exercise, String _date, int _count, int _wrongCount)
    {
        this.exercise = _exercise;
        this.date = _date;
        this.count = _count;
        this.wrongCount = _wrongCount;
    }


    public int getExercise() { return this.exercise; }

    public String getDate()
    {
        return this.date;
    }

    public int getCount() { return this.count; }

    public int getWrongCount() { return this.wrongCount; }
}
