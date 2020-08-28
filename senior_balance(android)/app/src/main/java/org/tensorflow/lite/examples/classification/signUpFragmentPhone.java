package org.tensorflow.lite.examples.classification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signUpFragmentPhone extends Fragment {

    String title;
    int page;
    private EditText editPhoneNumber;
    private String PhonNumber;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    // newInstance constructor for creating fragment with arguments
    public static signUpFragmentPhone newInstance(int page, String title) {
        signUpFragmentPhone fragment = new signUpFragmentPhone();
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

        View view = inflater.inflate(R.layout.fragment_signup_phonenum, container, false);

//        if(editPhoneNumber.getText().toString()!="") {
//            PhonNumber = editPhoneNumber.getText().toString();
//        }
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

        return view;
    }


}
