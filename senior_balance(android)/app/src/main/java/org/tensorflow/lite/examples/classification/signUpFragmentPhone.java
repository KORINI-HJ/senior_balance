package org.tensorflow.lite.examples.classification;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class signUpFragmentPhone extends Fragment {


    String title;
    int page;
    static String PhoneAddress;
    private EditText editPhoneNumber;
    private TextView textView;

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
        textView = (TextView) view.findViewById(R.id.textview2);
        editPhoneNumber = (EditText) view.findViewById(R.id.editText_phonenumber);
        editPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus ) {
                    if(editPhoneNumber.getText().toString()!="") {
                        String subPhoneNum = editPhoneNumber.getText().toString();
                        UserData.PhoneAddr = subPhoneNum.substring(3, subPhoneNum.length()) + "@token.com";
                    }
                }
            }
        });
        editPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textView.setText("화면을옆으로 밀어주세요");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return view;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
