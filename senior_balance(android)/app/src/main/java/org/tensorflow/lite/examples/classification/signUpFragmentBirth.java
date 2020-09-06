package org.tensorflow.lite.examples.classification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class signUpFragmentBirth extends Fragment {

    String title;
    int page;
//    public static variable Var = new variable();
    signUpFragmentPhone Phone = new signUpFragmentPhone();
    signUpFragmentName Name = new signUpFragmentName();

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth firebaseAuth;

    private CheckBox Checkfemale;
    private CheckBox Checkmale;
    String gender;
    String birth;
    private int mYear =1960, mMonth=1, mDay=0;
    private ImageButton button;
    private TextView textView;

    private Context context;
    Activity activity;

    @Override
    public void onAttach(Context mContext) {
        super.onAttach(context);
        context = mContext;
        if (mContext instanceof Activity) { activity = (Activity) mContext; }
    }

    // newInstance constructor for creating fragment with arguments
    public static signUpFragmentBirth newInstance(int page, String title) {
        signUpFragmentBirth fragment = new signUpFragmentBirth();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){

        }
    }
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_birth, container, false);
        activity = getActivity();

        /*
            생년월일을 선택하고 UserData 의 Birth 에 데이터를 저장합니다.
        */
        DatePicker datePicker = view.findViewById(R.id.datepicker_birthday);
        datePicker.init(mYear, mMonth, mDay,getmOnDateChangedListener);

        /*
            성별을 선택합니다.
        */
        textView = (TextView) view.findViewById(R.id.textview3);
        Checkfemale = (CheckBox) view.findViewById(R.id.checkbox_female);
        Checkfemale.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View v){
                if(((CheckBox) v).isChecked() && gender != "남") {
                    gender = "여";
                    UserData.Gender = gender;
                    Intent intent = new Intent(activity, MakeUserDataActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
                else { }
            }
        });
        Checkmale = (CheckBox) view.findViewById(R.id.checkbox_male);
        Checkmale.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View v){
                if(((CheckBox) v).isChecked() && gender != "여") {
                    gender = "남";
                    UserData.Gender = gender;
                    Intent intent = new Intent(activity, MakeUserDataActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
                else {}
            }

        });

        return view;
    }

    DatePicker.OnDateChangedListener getmOnDateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
            mYear = year;
            mMonth = month;
            mDay = day;
            String sYear = Integer.toString(mYear);
            String sMonth = Integer.toString(mMonth);
            String sDay = Integer.toString(mDay);
            birth = sYear+sMonth+sDay;
            UserData.Birth = birth;
        }
    };


}
