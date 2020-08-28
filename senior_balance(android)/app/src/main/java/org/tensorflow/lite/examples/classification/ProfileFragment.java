package org.tensorflow.lite.examples.classification;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.layout.simple_list_item_1;


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
    private TextView sex;

    private Button name_button;
    private Button birthDay_button;



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
            if (User!=null) {
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
        name = (TextView)view.findViewById(R.id.name_text);
        birthDay=(TextView)view.findViewById(R.id.birthday_text);
        sex = (TextView)view.findViewById(R.id.sex_text);
        //email = (TextView)view.findViewById(R.id.email);


        initDatabase();
        checkCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReference =mDatabase.getReference().child("User").child(_email.split("@")[0]); // 변경값을 확인할 child 이름

        FirebaseUser User  = FirebaseAuth.getInstance().getCurrentUser();

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                HashMap<String, String> UserInfoMap = new HashMap<String, String>();
                for(DataSnapshot child : dataSnapshot.getChildren()){

                    String key = child.getKey();
                    String value = child.getValue().toString();
                    UserInfoMap.put(key,value);
                }
                System.out.println(UserInfoMap.get("name"));
                name.setText(UserInfoMap.get("name"));
                birthDay.setText(UserInfoMap.get("date_of_birth"));
                sex.setText(UserInfoMap.get("gender"));
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
