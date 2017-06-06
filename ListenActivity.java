package com.example.user.record;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by user on 2016-12-29.           // 정지하고 다시시작 부분, 끝까지 다하고 다시 시작 부분
 */

public class ListenActivity extends AppCompatActivity implements View.OnClickListener{
    MediaPlayer mPlayer ;
    String file_name;
    int pos;
    String path = Environment.getExternalStorageDirectory().toString()+"/MyRecord";
    SeekBar seekbar;
    Button playBtn, tempPauseBtn, replayBtn, stopBtn;
    String pathh;
    int poss = -999;
    TextView text1;

    //private int mCurRecTimeMs;
    //private int mCurProgressTimeDisplay;

    Handler mProgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            //mCurRecTimeMs = mCurRecTimeMs + 100;
            //mCurProgressTimeDisplay = mCurProgressTimeDisplay + 100;

            text1 = (TextView)findViewById(R.id.text1);

            if(mPlayer == null) return;
            if(mPlayer.isPlaying()) {
                seekbar.setProgress(mPlayer.getCurrentPosition());
                double m = mPlayer.getCurrentPosition()*0.001;
                int mm = (int)Math.floor(m);
                double s = mPlayer.getDuration()*0.001;
                int ss = (int)Math.floor(s);
                text1.setText( mm+ " : " + ss);
            }
            mProgressHandler.sendEmptyMessageDelayed(0,100);
            /*if(mCurRecTimeMs <0) {
            } else if (mCurRecTimeMs <20000) {
                seekbar.setProgress(mCurProgressTimeDisplay);

                int m = mCurProgressTimeDisplay/20000;
                int s = (mCurProgressTimeDisplay%20000)/1000;
                text1.setText(m + " : " + s);
                mProgressHandler.sendEmptyMessageDelayed(0,100);
            } else {
                //play();
            }*/
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        mPlayer = new MediaPlayer();        //

        Intent it = getIntent();
        file_name = it.getStringExtra("file_name");
        pos = it.getIntExtra("position",-1);
        pathh = path +"/"+ file_name;

        try {
            mPlayer.setDataSource(pathh);           //
            mPlayer.prepare();
        } catch(Exception e) {
        }

        seekbar = (SeekBar)findViewById(R.id.playbarr);
        seekbar.setMax(mPlayer.getDuration());                  // seekbar의 길이를 녹음된 길이 만큼으로 지정      //
        seekbar.setVisibility(ProgressBar.VISIBLE);

        playBtn = (Button) findViewById(R.id.play);
        tempPauseBtn = (Button) findViewById(R.id.tempPause);
        replayBtn = (Button) findViewById(R.id.replay);
        stopBtn = (Button) findViewById(R.id.stop);

        playBtn.setOnClickListener(this);
        replayBtn.setOnClickListener(this);
        tempPauseBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);

        mPlayer.setOnCompletionListener(mOnComplete);       //
        mPlayer.setOnSeekCompleteListener(mOnSeekComplete);     //
        mProgressHandler.sendEmptyMessageDelayed(0, 100);           //
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.play:
                    play();
                break;

            case R.id.tempPause:
                try {
                    poss = mPlayer.getCurrentPosition();
                    mPlayer.pause();
                    //mCurRecTimeMs = -999;
                    mProgressHandler.sendEmptyMessageAtTime(0, 0);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "일시 정지 할 필요가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.replay:
                        //mCurRecTimeMs = 0;
                        //int mCurProgressTimeDisplay = poss;
                if(poss != -999) {
                        mProgressHandler.sendEmptyMessageAtTime(poss, 0);
                try {
                    mPlayer.seekTo(poss);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "다시 시작할 필요가 없습니다.", Toast.LENGTH_SHORT).show();
                }}
                break;

            case R.id.stop:
                try {
                    mPlayer.stop();
                    //mCurRecTimeMs = -999;
                    //mProgressHandler.sendEmptyMessageDelayed(0, 0);
                    seekbar.setProgress(0);
                    text1.setText( "0 : 0");
                    String str = pathh;
                    mPlayer.reset();
                    mPlayer.setDataSource(str);           //
                    mPlayer.prepare();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "멈출 필요가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    MediaPlayer.OnCompletionListener mOnComplete = new MediaPlayer.OnCompletionListener() {     // 재생 완료시
        public void onCompletion(MediaPlayer arg0) {            // 재생이 완료되었을 때에
            mPlayer.reset();
            seekbar.setProgress(0);
            try {
                mPlayer.setDataSource(pathh);           //
                mPlayer.prepare();
            }catch(Exception e) {
            }
        }
    };

    MediaPlayer.OnSeekCompleteListener mOnSeekComplete = new MediaPlayer.OnSeekCompleteListener() {         // 위치 이동 완료 처리
        public void onSeekComplete(MediaPlayer mp) {            // 위치 이동 완료 처리
            mPlayer.start();
        }
    };

    SeekBar.OnSeekBarChangeListener mOnSeek = new SeekBar.OnSeekBarChangeListener() {               // 재생 위치 이동
        public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser)
                mPlayer.seekTo(progress);
        }

        public void onStartTrackingTouch(SeekBar seekbar) {
            if(mPlayer.isPlaying()) {
                mPlayer.pause();
            }
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    public void play() {
        //mCurRecTimeMs = 0;
        //mCurProgressTimeDisplay = 0;
        try {
            if(mPlayer.isPlaying() == false) {
                mPlayer.start();
            }
        } catch (Exception e) {
        }
    }

    public void onBackPressed() {           // 뒤로 가기 버튼 눌렀을때
        super.onBackPressed();
        if(mPlayer != null) {               // 액티비티 종료시 재생 강제 종료
            mPlayer.release();
            mPlayer=null;
        }
        Intent itt = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(itt);
    }
}
