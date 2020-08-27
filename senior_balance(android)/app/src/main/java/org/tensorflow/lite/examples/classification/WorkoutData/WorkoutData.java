package org.tensorflow.lite.examples.classification.WorkoutData;

public class WorkoutData {
    private int exercise;
    private String date;
    private String outcome;

    public WorkoutData(int _exercise, String _date, String _outcome)
    {
        this.exercise = _exercise;
        this.date = _date;
        this.outcome = _outcome;
    }


    public int getExercise() { return this.exercise; }

    public String getDate()
    {
        return this.date;
    }

    public String getOutcome() { return this.outcome; }
}
