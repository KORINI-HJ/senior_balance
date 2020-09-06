package org.tensorflow.lite.examples.classification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


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
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private ChildEventListener mChild;
    private TextView birthDay;
    private TextView name;
    // private TextView email;
    private TextView gender;
    private TextView phoneAddr;

    private ImageButton setting_button;
    private ImageView user_image;
    private ImageButton alarm_button;
    private final int GET_GALLARY_IMAGE=200;
    private Activity activity;
    private Context context;
    Calendar alarmCalendar;
    int alarmHour = 0, alarmMinute = 0;
    /*
    * 카메라 설정
    * */
    private String mCurrentPhotoPath;
    private static final int FROM_CAMERA = 0;
    private static final int FROM_ALBUM = 1;
    private Uri imgUri, photoURI, albumURI;
    private String downloadUrl;
    private int flag;
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
        if (User != null) {
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
        name = (TextView) view.findViewById(R.id.name_text);
        birthDay = (TextView) view.findViewById(R.id.birthday_text);
        gender = (TextView) view.findViewById(R.id.gender_text);
        phoneAddr = (TextView) view.findViewById(R.id.phoneAddress_text);
        alarm_button = (ImageButton) view.findViewById(R.id.alarm_btn);
        user_image = (ImageView) view.findViewById(R.id.profile_image);
        activity = getActivity();

        /*
        * 톱니바퀴 모양을 누르면 사진업로드화면으로 이동합니다.
        * 1. 카메라 권한을 설정하고 앨법으로부터 사진을 업로드합니다.
        * */

            //앨범선택, 사진촬영, 취소 다이얼로그 생성
            user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeDialog();
                }
        });
        /*
        * timepicker를 이용해 알람을 이용할 시간을 설정합니다.
        * */
        Notification.Builder mBuilder = new Notification.Builder(activity);
        alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog
                        = new TimePickerDialog(activity, android.R.style.Theme_Light_WallpaperSettings, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        alarmHour = hourOfDay;
                        alarmMinute = minute;
                        new Alarm(getContext()).setAlarm();
                    }
                }, alarmHour, alarmMinute, false);
                timePickerDialog.show();
            }
        });

        /*
        * 데이터 베이스로부터 정보를 읽어옵니다.
        */
        mAuth = FirebaseAuth.getInstance();
        initDatabase();
        checkCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference().child("User").child(_email.split("@")[0]); // 변경값을 확인할 child 이름
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                HashMap<String, String> UserInfoMap = new HashMap<String, String>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    String key = child.getKey();
                    String value = child.getValue().toString();
                    UserInfoMap.put(key, value);
                }
                System.out.println(UserInfoMap.get("name"));
                name.setText(UserInfoMap.get("name"));
                birthDay.setText(UserInfoMap.get("date_of_birth"));
                gender.setText(UserInfoMap.get("gender"));
                phoneAddr.setText(UserInfoMap.get("phoneAddress"));
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

    /*
    * 다이얼로그를 생성하는 함수입니다.
    * */
    private void makeDialog(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Dialog);
        alt_bld.setTitle("사용자 사진을 변경하시겠습니까?").setIcon(R.drawable.ic_icon).setMessage("사용자 사진을 변경하시겠습니까?").setCancelable(
                false).setPositiveButton("사진촬영",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.v("알림", "다이얼로그 > 사진촬영 선택");
                        // 사진 촬영 클릭
                        flag = 0;
                        takePhoto();
                    }
                }).setNeutralButton("앨범선택",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        Log.v("알림", "다이얼로그 > 앨범선택 선택");
                        //앨범에서 선택
                        flag = 1;
                        selectAlbum();
                    }
                }).setNegativeButton("취소   ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.v("알림", "다이얼로그 > 취소 선택");
                        // 취소 클릭. dialog 닫기.
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.show();

    }

    //앨범 선택 클릭
    public void selectAlbum(){
        //앨범 열기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent,FROM_ALBUM);
    }

    //사진 찍기 클릭
    public void takePhoto(){
        // 촬영 후 이미지 가져오기
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getContext().getPackageManager())!=null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(photoFile!=null){
                    Uri providerURI = FileProvider.getUriForFile(getContext(),getContext().getPackageName(),photoFile);
                    imgUri = providerURI;
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, FROM_CAMERA);
                }
            }
        }else{
            Log.v("알림", "저장공간에 접근 불가능");
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
            case FROM_ALBUM : {
                //앨범에서 가져오기
                if(data.getData()!=null){
                    try{
                        File albumFile = null;
                        albumFile = createImageFile();
                        photoURI = data.getData();
                        albumURI = Uri.fromFile(albumFile);
                        galleryAddPic();
                        //이미지뷰에 이미지 셋팅
                        user_image.setImageURI(photoURI);
                        //cropImage();
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.v("알림","앨범에서 가져오기 에러");
                    }
                }
                break;
            }
            case FROM_CAMERA : {
                //촬영
                try{
                    Log.v("알림", "FROM_CAMERA 처리");
                    galleryAddPic();
                    //이미지뷰에 이미지셋팅
                    user_image.setImageURI(imgUri);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    /*
    * 이미지 파일 생성
    * */
    public File createImageFile() throws IOException{

        String imgFileName = System.currentTimeMillis() + ".jpg";
        File imageFile= null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "ireh");
        if(!storageDir.exists()){
            //없으면 만들기
            Log.v("알림","storageDir 존재 x " + storageDir.toString());
            storageDir.mkdirs();
        }
        Log.v("알림","storageDir 존재함 " + storageDir.toString());
        imageFile = new File(storageDir,imgFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }
    /*
    * 겔러리에 현제사진 저장
    * */
    public void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);
        makeConfirmDialog();
    }

    /*
    * 파이어베이스저장소에 이미지 저장
    * */
    public void makeConfirmDialog(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Dialog);
        alt_bld.setTitle("사진을 선택해주세요").setIcon(R.drawable.ic_icon).setMessage("사용할 사진을 선택해주세요").setCancelable(
                false).setPositiveButton("선택한 사진으로 저장",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //DB에 등록하기
                        final String cu = mAuth.getUid();
                        //1. 사진을 storage에 저장하고 그 url을 알아내야함..
                        String filename = cu + "_" + System.currentTimeMillis();
                        storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReferenceFromUrl("본인의 Firebase 저장소").child("WriteClassImage/" + filename);
                        UploadTask uploadTask;
                        Uri file = null;
                        if(flag ==0){
                            //사진촬영
                            file = Uri.fromFile(new File(mCurrentPhotoPath));
                        }else if(flag==1){
                            //앨범선택
                            file = photoURI;
                        }
                        uploadTask = storageRef.putFile(file);
                        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Theme_AppCompat);
                        progressDialog.setMessage("업로드중...");
                        progressDialog.show();
                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Log.v("알림", "사진 업로드 실패");
                                exception.printStackTrace();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                Log.v("알림", "사진 업로드 성공 " + downloadUrl);
                            }
                        });
                    }
                }).setNegativeButton("닫기",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 아니오 클릭. dialog 닫기.
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }



    /*
    * 알람 설정을해줍니다.
    * */
    public class Alarm{
        private Context context;
        public Alarm(Context context){
            this.context=context;
        }
        public void setAlarm() {
            AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), BroadCast.class);

            PendingIntent sender = PendingIntent.getBroadcast(getActivity(),0,intent,0);
            alarmCalendar = Calendar.getInstance();
            alarmCalendar.setTimeInMillis(System.currentTimeMillis());
            alarmCalendar.set(Calendar.HOUR_OF_DAY, alarmHour);
            alarmCalendar.set(Calendar.MINUTE, alarmMinute);
            alarmCalendar.set(Calendar.SECOND, 0);
            String workOutHour = alarmHour + "시" + alarmMinute + "분";
            Toast.makeText(getContext(), "운동 시간이 " + workOutHour + "으로 설정되었습니다.", Toast.LENGTH_SHORT).show();

            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP,alarmCalendar.getTimeInMillis(),sender);

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
