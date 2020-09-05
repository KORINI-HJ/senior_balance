package org.tensorflow.lite.examples.classification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;


public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String _name;
    private String _phoneNumber;
    private String _birthDay;
    private String _email;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private ChildEventListener mChild;
    private TextView birthDay;
    private TextView name;
    // private TextView email;
    private TextView gender;
    private TextView phoneAddr;

    private Button name_button;
    private Button birthDay_button;
    private ImageButton alarm_button;
    private Activity activity;
    private Tag TAG;
    Calendar alarmCalendar;
    int alarmHour = 0, alarmMinute = 0;

    /////////////////////////////////////////////
    /////////////Default constructor/////////////
    /////////////////////////////////////////////
    public ProfileFragment() {
        // Required empty public constructor

    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public void checkCurrentUser() {
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if (User != null) {
            // User is signed in.
            _name = User.getDisplayName();
            _email = User.getEmail();
            _phoneNumber = User.getPhoneNumber();

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, null);
        name = (TextView) view.findViewById(R.id.name_text);
        birthDay = (TextView) view.findViewById(R.id.birthday_text);
        gender = (TextView) view.findViewById(R.id.gender_text);
        phoneAddr = (TextView) view.findViewById(R.id.phoneAddress_text);
        alarm_button = (ImageButton) view.findViewById(R.id.alarm_btn);
        activity = getActivity();

        /*
        * timepicker를 이용해 알람을 이용할 시간을 설정합니다.
        * */
        Notification.Builder mBuilder = new Notification.Builder(activity);
        alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog
                        = new TimePickerDialog(activity, android.R.style.Theme_Light_WallpaperSettings, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        alarmHour = hourOfDay;
                        alarmMinute = minute;
                        new Alarm(getContext()).setAlarm();
                    }
                }, alarmHour, alarmMinute, false);
                timePickerDialog.show();
            }
        });

        /*
        * 데이터 베이스로부터 정보를 읽어옵니다.
        */
        initDatabase();
        checkCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference().child("User").child(_email.split("@")[0]); // 변경값을 확인할 child 이름
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                HashMap<String, String> UserInfoMap = new HashMap<String, String>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    String key = child.getKey();
                    String value = child.getValue().toString();
                    UserInfoMap.put(key, value);
                }
                System.out.println(UserInfoMap.get("name"));
                name.setText(UserInfoMap.get("name"));
                birthDay.setText(UserInfoMap.get("date_of_birth"));
                gender.setText(UserInfoMap.get("gender"));
                phoneAddr.setText(UserInfoMap.get("phoneAddress"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }


        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatabase();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    /*
    * 알람 설정을해줍니다.
    * */
    public class Alarm{
        private Context context;
        public Alarm(Context context){
            this.context=context;
        }
        public void setAlarm() {
            AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), BroadCast.class);

            PendingIntent sender = PendingIntent.getBroadcast(getActivity(),0,intent,0);
            alarmCalendar = Calendar.getInstance();
            alarmCalendar.setTimeInMillis(System.currentTimeMillis());
            alarmCalendar.set(Calendar.HOUR_OF_DAY, alarmHour);
            alarmCalendar.set(Calendar.MINUTE, alarmMinute);
            alarmCalendar.set(Calendar.SECOND, 0);
            String workOutHour = alarmHour + "시" + alarmMinute + "분";
            Toast.makeText(getContext(), "운동 시간이 " + workOutHour + "으로 설정되었습니다.", Toast.LENGTH_SHORT).show();

            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP,alarmCalendar.getTimeInMillis(),sender);

         }
    }



    private void initDatabase() {
        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("User");

        mChild = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
        };


    }
        @Override
        public void onDestroy () {
            super.onDestroy();
            mReference.removeEventListener(mChild);
        }

}
