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
    static String PhoneAddress;
    private EditText editPhoneNumber;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_phonenum, container, false);

        /*
            입력받은 사용자의 휴대폰번호정보를 UserData의 PhoneAddr 변수에 저장합니다.
        */

        editPhoneNumber = (EditText) view.findViewById(R.id.editText_phonenumber);
        editPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus && editPhoneNumber.getText().toString()!="") {
                    String subPhoneNum = editPhoneNumber.getText().toString();
                    UserData.PhoneAddr = subPhoneNum.substring(3, subPhoneNum.length()) + "@token.com";
                }
            }
        });

        return view;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
