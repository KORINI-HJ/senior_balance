package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MakeUserDataActivity extends AppCompatActivity {

    private ImageButton button;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseAuth firebaseAuth;
    String Phone;
    String NameTemp;
    private String PhoneAddr;
    private String Passwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdata);
        firebaseAuth = FirebaseAuth.getInstance();

        /*
            사용자가 회원가입시 회원정보를 등록후, auth가 바뀌면 mainActivity로 이동
         */
        button = (ImageButton) findViewById(R.id.btn_join);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(UserData.PhoneAddr + "*");
                if(!UserData.PhoneAddr.equals("") && !UserData.Name.equals(""))
                {
                    createUser(UserData.PhoneAddr, UserData.Name);


                }
                else{
                    Toast.makeText(MakeUserDataActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*
            사용자의 정보를 로그인정보로 전환합니다.
         */
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                }else{

                }
            }
        };

    }

    /*
        LoginUser method
     */
    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.addAuthStateListener(firebaseAuthListener);

                        } else {
                        }
                    }
                });
    }

    /*
        createUser method
     */
    DatabaseReference UserReference = databaseReference.child("User");
    private void createUser(final String PhoneAddr, String name ) {
        firebaseAuth.createUserWithEmailAndPassword(PhoneAddr, name)
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( @NonNull Task<AuthResult> task) {
                        Map<String, Object> users = new HashMap<>();

                        // 홍길동1234 에서 홍길동 부분만 추출
                        int len = UserData.Name.length()-4;
                        NameTemp = UserData.Name.substring(0,len);

                        //db에 데이터 업로드
                        String phoneAddress = UserData.PhoneAddr.substring(0,UserData.PhoneAddr.length()-10);
                        users.put((phoneAddress), new User(UserData.Birth, NameTemp, UserData.Gender,"010"+phoneAddress));
                        if (task.isSuccessful()) {
                            // 회원가입 성공시
                            UserReference.updateChildren(users);
                            loginUser(UserData.PhoneAddr, UserData.Name);
                            Toast.makeText(MakeUserDataActivity.this, "오늘도 건강한 하루되세요~!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MakeUserDataActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // 계정이 중복된 경우
                            System.out.println(Phone);
                            Log.d("flag", "signInAnonymously:failure"+task.getException().getMessage());
                            Toast.makeText(MakeUserDataActivity.this, "계정이 중복되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static class User {
        public String date_of_birth;
        public String name;
        public String gender;
        public String phoneAddress;

        public User() { }

        public User(String dateOfBirth, String name, String gender,String PhoneAddress){
            this.date_of_birth = dateOfBirth;
            this.name = name;
            this.gender = gender;
            this.phoneAddress = PhoneAddress;
        }
    }
}
