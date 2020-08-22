package org.tensorflow.lite.examples.classification;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

//import android.support.annotation.NonNull;

//////////////////////////////////////////////////////
//  회원 가입 페이지
//
//  기능 : 회원 정보 등록, database 에 저장
//
//  database 내용: (이름, 생년월일, 이메일, 성별, 키 , 몸무게)
//////////////////////////////////////////////////////

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextWeight;
    private EditText editTextHeight;

    private Button buttonJoin;
    private CheckBox Checkfemale;
    private CheckBox Checkmale;
    private String sex;

    private String birth;
    private String height,weight;
    private String[] emailToken;
    private String Name;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private int mYear =0, mMonth=0, mDay=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = (EditText) findViewById(R.id.editText_email);
        editTextPassword = (EditText) findViewById(R.id.editText_passWord);
        editTextName = (EditText) findViewById(R.id.editText_name);
        editTextHeight = (EditText) findViewById(R.id.editText_height);
        editTextWeight = (EditText) findViewById(R.id.editText_weight);


        buttonJoin = (Button) findViewById(R.id.btn_join);
        Checkfemale = (CheckBox) findViewById(R.id.checkbox_female);
        Checkmale = (CheckBox) findViewById(R.id.checkbox_male);

        DatePicker datePicker = findViewById(R.id.BirthDay);
        datePicker.init(mYear, mMonth, mDay,getmOnDateChangedListener);

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

        ////////////////////////////////
        /////email, password, name//////
        ////////////////////////////////
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")&&
                !editTextName.getText().toString().equals("")) {
                    // 이메일과 비밀번호가 공백이 아닌 경우
                    weight = editTextWeight.getText().toString();
                    height = editTextHeight.getText().toString();
                    Name=editTextName.getText().toString();
                    createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                } else {
                    // 이메일과 비밀번호가 공백인 경우
                    Toast.makeText(SignUpActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

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

        /////////////////////////////////////////////////////////////////////////////
        /////mapping User data : mapping user informations to store in DataBase//////
        /////////////////////////////////////////////////////////////////////////////
        public static class User {

            public String date_of_birth;
            public String name;
            public String email;
            public String sex;
            public String weight;
            public String height;

            public User(String birth, String name, String sex, String weight, String height) {
                //Default Constructor
            }

            public User(String dateOfBirth, String email, String Name, String sex, String weight, String height) {
                this.date_of_birth = dateOfBirth;
                this.email = email;
                this.name = Name;
                this.height = height;
                this.weight = weight;
                this.sex = sex;
            }


        }

        ////////////////////////////////////////////////////////////
        /////create User data : store informations in DataBase//////
        ////////////////////////////////////////////////////////////
        DatabaseReference UserReference = databaseReference.child("User");

        private void createUser(final String email, String password ) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete( @NonNull Task<AuthResult> task) {

                            emailToken = email.split("@");
                            Map<String, Object> users = new HashMap<>();
                            users.put(emailToken[0], new User(birth,email,Name,sex,weight,height));
                            if (task.isSuccessful()) {
                                // 회원가입 성공시
                                UserReference.updateChildren(users);
                                Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                // 계정이 중복된 경우
                                Toast.makeText(SignUpActivity.this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
}