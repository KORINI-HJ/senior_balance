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

public class signUpFragmentName extends Fragment {

    private String title;
    private int page;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();


    public static signUpFragmentName newInstance(int page, String title) {
        signUpFragmentName fragment = new signUpFragmentName();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String name;
        View view = inflater.inflate(R.layout.fragment_signup_name, container, false);
        EditText editName = (EditText) view.findViewById(R.id.editText_name);

        /////////////////////////////////////
        ////////////User Name////////////////
        /////////////////////////////////////
        name = editName.getText().toString();
        variable UserINfo = new variable();
        UserINfo.UserMap.put("name",name);

        ////////////////////////////////////
        ////////////create User/////////////
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
