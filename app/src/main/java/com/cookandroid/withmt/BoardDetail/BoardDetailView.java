package com.cookandroid.withmt.BoardDetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cookandroid.withmt.ApiClient;
import com.cookandroid.withmt.MyPage.MyInfo;
import com.cookandroid.withmt.R;
import com.cookandroid.withmt.Writing.ModifyWriting;
import com.cookandroid.withmt.Writing.WritingView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetailView extends AppCompatActivity {
    Dialog CheckDel;
    Uri uri;
    int boardid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boarddetail);

        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        TextView title = (TextView)findViewById(R.id.title);
        TextView date = (TextView)findViewById(R.id.date);
        TextView maxNum = (TextView)findViewById(R.id.maxNum);
        TextView chosenGender = (TextView)findViewById(R.id.gender);
        TextView content = (TextView)findViewById(R.id.content);
        TextView userNic = (TextView)findViewById(R.id.userNic);
        TextView userIcon = (TextView)findViewById(R.id.userIcon);
        TextView userInfo = (TextView)findViewById(R.id.userInfo);

        //boardId 받아옴
        Intent intent = getIntent();
        int id = intent.getIntExtra("boardId", 0);

        //오픈카톡 버튼 오류 해결하기
        Button btnjoin = (Button) findViewById(R.id.btnjoin);
        btnjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Log.d("Tag", String.valueOf(id));

        // 게시글 상세 받아오기
        ApiClient.getApiService().getBoard(id)
            .enqueue(new Callback<BoardDetailResponse>() {
                @Override
                public void onResponse(Call<BoardDetailResponse> call, Response<BoardDetailResponse> response) {
                    if(response.isSuccessful()){
                        BoardDetailResponse board = response.body();
                        // 본문 세팅
                        title.setText(board.getBoard().getTitle());
                        date.setText(board.getBoard().getDate());
                        maxNum.setText(String.valueOf(board.getBoard().getMember())+"명");
                        switch (board.getBoard().getGender()){
                            case 0: chosenGender.setText("남성만"); break;
                            case 1: chosenGender.setText("여성만"); break;
                            case 2: chosenGender.setText("성별 무관"); break;
                        }
                        content.setText(board.getBoard().getContent());
                        uri = Uri.parse(board.getBoard().getLink());

                        //사용자 정보 세팅
                        //이모지
                        switch (board.getUser().getImoji()){
                            case "BEAR": userIcon.setText("🐻"); break;
                            case "TIGER": userIcon.setText("🐯"); break;
                            case "RABBIT": userIcon.setText("🐰"); break;
                            case "FOX": userIcon.setText("🦊"); break;
                        }
                        //닉네임
                        userNic.setText(board.getUser().getNickname() + " 님");
                        //등산 목적
                        String prefer = "";
                        if(board.getUser().getFriendship() == 1 && board.getUser().getClimbingMate() == 1) {
                            prefer = "친목+등산";
                        } else if(board.getUser().getFriendship() == 1 && board.getUser().getClimbingMate() == 0) {
                            prefer = "친목 위주";
                        } else if(board.getUser().getFriendship() == 0 && board.getUser().getClimbingMate() == 1) {
                            prefer = "등산 위주";
                        }
                        //숙련도
                        String level = "";
                        if(board.getUser().getClimbingLevel() == 0) {
                            level = "입문자";
                        } else if(board.getUser().getClimbingLevel() == 0.33) {
                            level = "경험자";
                        } else if(board.getUser().getClimbingLevel() == 0.66) {
                            level = "숙련가";
                        } else if(board.getUser().getClimbingLevel() == 1) {
                            level = "전문가";
                        }
                        //성별
                        String gender = "";
                        switch (board.getUser().getGender()){
                            case 0: gender = "남성"; break;
                            case 1: gender = "여성"; break;
                        }
                        //연령대
                        String age = "";
                        switch (board.getUser().getAge()){
                            case 1: age = "10대"; break;
                            case 2: age = "20대"; break;
                            case 3: age = "30대"; break;
                            case 4: age = "40대"; break;
                            case 5: age = "50대"; break;
                            case 6: age = "60대 이상"; break;
                        }
                        userInfo.setText(level+"/"+prefer+"/"+gender+"/"+age);

                    }
                }

                @Override
                public void onFailure(Call<BoardDetailResponse> call, Throwable t) {
                    Log.e("Tag", String.valueOf(t));
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            //뒤로가기
            case android.R.id.home:
                finish();
                return true;
            //메뉴 글 수정
            case R.id.tool_modify:
                Intent intent = getIntent();
                boardid = intent.getIntExtra("boardId", 0);
                Intent modifyintent = new Intent(getApplicationContext(), ModifyWriting.class);
                modifyintent.putExtra("boardId", boardid);
                return true;
            //메뉴 글 삭제
            case R.id.tool_del:
                CheckDel = new Dialog(BoardDetailView.this);
                CheckDel.requestWindowFeature(Window.FEATURE_NO_TITLE);
                CheckDel.setContentView(R.layout.delete_dialog);
                showDialogDel();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void showDialogDel(){
        CheckDel.show();
        CheckDel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnNo:
                CheckDel.dismiss();
                break;
            case R.id.btnOk:
                Intent intent = getIntent();
                boardid = intent.getIntExtra("boardId", 0);
                ApiClient.getApiService().deleteBoard(boardid)
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if(response.isSuccessful()){
                                    Log.d("Tag", String.valueOf(response.code()));
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
        }
    }
}