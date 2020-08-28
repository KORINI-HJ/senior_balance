package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signUpFragmentBirth extends Fragment {

    String title;
    int page;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private CheckBox Checkfemale;
    private CheckBox Checkmale;
    private String sex;
    private String birth;
    private int mYear =0, mMonth=0, mDay=0;
    private Button button;



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
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup_birth, container, false);
        Checkfemale = (CheckBox) view.findViewById(R.id.checkbox_female);
        Checkmale = (CheckBox) view.findViewById(R.id.checkbox_male);
        button = (Button) view.findViewById(R.id.btn_join);

        //////////////////////////
        /////   Birthday    /////
        //////////////////////////
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
            }
        };


        ////////////////////////////////
        /////   male or female    //////
        ////////////////////////////////
        Checkfemale.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View v){
                if(((CheckBox) v).isChecked()) {
                    sex = "여";
                }
            }
        });
        Checkmale.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View v){
                if(((CheckBox) v).isChecked() && sex == "") {
                    sex = "남";
                }
            }
        });

        DatePicker datePicker = view.findViewById(R.id.datepicker_birthday);
        datePicker.init(mYear, mMonth, mDay,getmOnDateChangedListener);

        ////////////////////////////////////
        ////////////CreateUser//////////////
        ////////////////////////////////////
        DatabaseReference UserReference = databaseReference.child("User");
        FirebaseAuth firebaseAuth;



//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//
//                        @Override
//                        public void onComplete( @NonNull Task<AuthResult> task) {
//
//                            emailToken = email.split("@");
//                            Map<String, Object> users = new HashMap<>();
//                            users.put(emailToken[0], new SignUpActivity.User(birth,email,Name,sex,weight,height));
//                            if (task.isSuccessful()) {
//                                // 회원가입 성공시
//                                UserReference.updateChildren(users);
//                                Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
//                                finish();
//                            } else {
//                                // 계정이 중복된 경우
//                                Toast.makeText(SignUpActivity.this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });


        ////////////////////////////////
        /////email, password, name//////
        ////////////////////////////////
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


}
