package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ImageButton buttonLogIn;
    private ImageButton buttonSignUp;
    private String PhoneAddr;
    private String Passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = (EditText) findViewById(R.id.editText_email);
        editTextPassword = (EditText) findViewById(R.id.editText_passWord);
        buttonSignUp = (ImageButton) findViewById(R.id.btn_signup);

        /*
            LoginActivity에서 SignUpActivity로 이동
        */
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        /*
            사용자가 로그인시 회원정보의 auth가 바뀌면 mainActivity로 이동
         */
        buttonLogIn = (ImageButton) findViewById(R.id.btn_login);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextEmail.getText().toString().equals("") &&
                        !editTextPassword.getText().toString().equals("")) {
                    MakeUserLoginInformation();
                    loginUser(PhoneAddr, Passwd);
                } else {
                    Toast.makeText(LogInActivity.this, "계정과 비밀번호를 입력하세요.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{

                }
            }
        };
    }
    public void MakeUserLoginInformation(){

        PhoneAddr = editTextEmail.getText().toString();
        PhoneAddr = PhoneAddr.substring(3,PhoneAddr.length())+"@token.com";
        Passwd = editTextPassword.getText().toString()+"1234";

    }

    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //로그인 성공
                            Toast.makeText(LogInActivity.this, "로그인 하였습니다.",
                                    Toast.LENGTH_SHORT).show();

                            firebaseAuth.addAuthStateListener(firebaseAuthListener);
                        } else {
                            System.out.println(Passwd);
                            System.out.println(PhoneAddr);
                            Toast.makeText(LogInActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected  void onStop() {
        super.onStop();
    }
}
