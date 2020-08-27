package org.tensorflow.lite.examples.classification.WorkoutData;

import android.graphics.Bitmap;

public class WorkoutData {
    private Bitmap img;
    private String date;
    private String outcome;

    public WorkoutData(Bitmap _img, String _date, String _outcome)
    {
        this.img = _img;
        this.date = _date;
        this.outcome = _outcome;
    }


    public Bitmap getImg()
    {
        return this.img;
    }

    public String getDate()
    {
        return this.date;
    }

    public String getOutcome() { return this.outcome; }
}
