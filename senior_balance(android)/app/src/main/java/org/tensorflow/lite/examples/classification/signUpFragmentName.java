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

public class signUpFragmentName extends Fragment {

    private String title;
    private int page;
    static EditText editName;
    static TextView textView;

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
        View view = inflater.inflate(R.layout.fragment_signup_name, container, false);

        /*
            사용자의 이름을 입력받으면 UserData의 Name 변수에 값을 저장합니다.
        */
        textView = (TextView) view.findViewById(R.id.textview) ;
        editName = (EditText) view.findViewById(R.id.editText_name);
        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus)
            {

                if(!hasFocus ) {
                    String mName = editName.getText().toString();
                    if(editName.getText().toString()!="")
                        UserData.Name=editName.getText().toString()+"1234";
                }
                else{

                }

            }
        });
        editName.addTextChangedListener(new TextWatcher() {
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
