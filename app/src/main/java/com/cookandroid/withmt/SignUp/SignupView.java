package com.cookandroid.withmt.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.withmt.ApiClient;
import com.cookandroid.withmt.Login.LoginRequest;
import com.cookandroid.withmt.Login.LoginView;
import com.cookandroid.withmt.PreferenceCheck.PreferenceResearchView;
import com.cookandroid.withmt.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupView extends AppCompatActivity {
    InputMethodManager imm;
    EditText editName, editId, editPW, confirmPW;
    Button btnNew, btnCheckname, btnCheckId;
    TextView alertname, alertId;
    String userid, userpw, usernic, imoji, gender, age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        editName = (EditText) findViewById(R.id.newName);
        editId = (EditText) findViewById(R.id.newId);
        editPW = (EditText) findViewById(R.id.newPW);
        confirmPW = (EditText) findViewById(R.id.confirmPW);

        btnCheckname = (Button)findViewById(R.id.btnCheckname);
        alertname = (TextView)findViewById(R.id.alertnickname);
        btnCheckId = (Button)findViewById(R.id.btnCheckId);
        alertId = (TextView)findViewById(R.id.alertId);

        btnNew = (Button) findViewById(R.id.btnNew);
        ImageButton btnX = (ImageButton) findViewById(R.id.btnClose);

        imoji = "";

        Spinner spinner = (Spinner) findViewById(R.id.spinAge);
        final String[] agelist = {"10대", "20대", "30대", "40대", "50대", "60대 이상"};

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, agelist);
        spinner.setAdapter(adapter);
        //나이 선택
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                age = String.valueOf(position+1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //닫기>로그인화면
        btnX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //성별 라디오 버튼 선택
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rdMale: gender = "0"; break;
                    case R.id.rdFemale: gender = "1"; break;
                }
            }
        });

        //닉네임이나 아이디 사용자 확인 후 수정할 경우
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alertname.setText("중복 확인을 해주세요.");
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        editId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alertId.setText("중복 확인을 해주세요.");
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        TextView pwlegthcheck = (TextView)findViewById(R.id.pwlegthcheck);
        TextView alertpw = (TextView)findViewById(R.id.alertPw);
        //비밀번호 길이 확인(5글자 이상인지)
        editPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editPW.getText().toString().length() > 5)
                    pwlegthcheck.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // 비밀번호 일치 확인
        confirmPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                alertpw.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(confirmPW.getText().toString().equals(editPW.getText().toString())){
                    alertpw.setVisibility(View.INVISIBLE);
                }
                else{
                    alertpw.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //회원가입버튼
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //입력 누락된 곳 확인
                if(imoji.equals("") ||
                        editName.getText().toString().equals("") ||
                        editId.getText().toString().equals("") ||
                        alertpw.getVisibility() == View.VISIBLE ||
                        confirmPW.getText().toString().equals("") ||
                        pwlegthcheck.getVisibility() == View.VISIBLE ||
                        radioGroup.getCheckedRadioButtonId() == -1 ||
                        !alertname.getText().equals("사용 가능한 닉네임입니다.") ||
                        !alertId.getText().equals("사용 가능한 아이디입니다.")) {
                    Toast tmsg = Toast.makeText(SignupView.this, "빠진 부분 없이 전부 입력해주세요.", Toast.LENGTH_SHORT);
                    tmsg.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
                    tmsg.show();
                }
                else{
                    //닉네임, 아이디, 비밀번호 변수에 할당
                    usernic = editName.getText().toString();
                    userid = editId.getText().toString();
                    userpw = editPW.getText().toString();
                    //회원가입 post
                    SignUpRequest signupRequest = new SignUpRequest(usernic, userid, userpw, gender, age, imoji);
                    Call<SignUpResponse> call = new ApiClient().getApiService().postSignUp(signupRequest);
                    call.enqueue(new Callback<SignUpResponse>() {
                        @Override
                        public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                            if(response.isSuccessful()){
                                //회원가입 성공하면 로그인
                                LoginRequest loginRequest = new LoginRequest(userid, userpw);
                                Call<String> logincall = new ApiClient().getApiService().postLogin(loginRequest);
                                logincall.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if(response.isSuccessful()){
                                            SharedPreferences userinfo = getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor autoLogin = userinfo.edit();
                                            autoLogin.putString("inputId", userid);
                                            autoLogin.putString("inputPW", userpw);
                                            autoLogin.commit();
                                            Intent intent = new Intent(getApplicationContext(), PreferenceResearchView.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.d("Tag",String.valueOf(t));
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<SignUpResponse> call, Throwable t) {

                        }
                    });
                }
            }
        });

        //닉네임 중복 확인
        btnCheckname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(editName.getWindowToken(),0);
                Call<String> call = ApiClient.getApiService().getNickname(editName.getText().toString());
                 call.enqueue(new Callback<String>() {
                     @Override
                     public void onResponse(Call<String> call, Response<String> response) {
                         if(response.body().equals("1")){
                             alertname.setText("이미 사용 중인 닉네임입니다.");
                             alertname.setVisibility(View.VISIBLE);
                         }
                         else{
                             if(editName.getText().toString().equals("")){
                                 alertname.setText("사용할 수 없는 닉네임입니다.");
                                 alertname.setVisibility(View.VISIBLE);
                             }
                             else{
                                 alertname.setText("사용 가능한 닉네임입니다.");
                                 alertname.setVisibility(View.VISIBLE);
                             }
                         }
                     }

                     @Override
                     public void onFailure(Call<String> call, Throwable t) {

                     }
                 });
            }
        });

        //아이디 중복 확인
        btnCheckId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(editId.getWindowToken(),0);
                Call<String> call = ApiClient.getApiService().getID(editId.getText().toString());
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().equals("1")){
                            alertId.setText("이미 사용 중인 아이디입니다.");
                            alertId.setVisibility(View.VISIBLE);
                        }
                        else{
                            if(editId.getText().toString().equals("")){
                                alertId.setText("사용할 수 없는 아이디입니다.");
                                alertId.setVisibility(View.VISIBLE);
                            }
                            else{
                                alertId.setText("사용 가능한 아이디입니다.");
                                alertId.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });

    }
    //이모지 버튼 클릭 시
    public void onClick(View v){
        Button bear = (Button)findViewById(R.id.profile_B);
        Button tiger = (Button)findViewById(R.id.profile_T);
        Button rabbit = (Button)findViewById(R.id.profile_R);
        Button fox = (Button)findViewById(R.id.profile_F);
        imoji = "";
        switch (v.getId()){
            case R.id.profile_B:
                imoji = "BEAR";
                bear.setBackgroundResource(R.drawable.profile_btn_click);
                tiger.setBackground(null);
                rabbit.setBackground(null);
                fox.setBackground(null);
                break;
            case R.id.profile_T:
                imoji = "TIGER";
                bear.setBackground(null);
                tiger.setBackgroundResource(R.drawable.profile_btn_click);
                rabbit.setBackground(null);
                fox.setBackground(null);
                break;
            case R.id.profile_R:
                imoji = "RABBIT";
                bear.setBackground(null);
                tiger.setBackground(null);
                rabbit.setBackgroundResource(R.drawable.profile_btn_click);
                fox.setBackground(null);
                break;
            case R.id.profile_F:
                imoji = "FOX";
                bear.setBackground(null);
                tiger.setBackground(null);
                rabbit.setBackground(null);
                fox.setBackgroundResource(R.drawable.profile_btn_click);
                break;
        }
    }


    //배경 클릭 시 키보드 숨김
    public void linearOnclick(View v){
        imm.hideSoftInputFromWindow(editId.getWindowToken(),0);
        imm.hideSoftInputFromWindow(editPW.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(editName.getWindowToken(),0);
        imm.hideSoftInputFromWindow(confirmPW.getWindowToken(), 0);
    }

}