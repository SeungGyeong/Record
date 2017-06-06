package com.example.user.record;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MediaRecorder mRecorder;
    SeekBar seekbar;
    Button recordBtn, stopBtn;
    String save_name;

    private int mCurRecTimeMs;
    private int mCurProgressTimeDisplay;

    static  public ArrayList<String> mDatas = new ArrayList<String>();
    String path = Environment.getExternalStorageDirectory().toString()+"/MyRecord";
    ListView list;

    int size;

    Handler mProgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            mCurRecTimeMs = mCurRecTimeMs + 100;
            mCurProgressTimeDisplay = mCurProgressTimeDisplay + 100;

            if(mCurRecTimeMs <0) {
            } else if (mCurRecTimeMs <20000) {
                seekbar.setProgress(mCurProgressTimeDisplay);
                mProgressHandler.sendEmptyMessageDelayed(0,100);
            } else {
                record();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = (TextView)findViewById(R.id.text1);
        seekbar = (SeekBar)findViewById(R.id.playbar);
        seekbar.setVisibility(ProgressBar.VISIBLE);


        recordBtn = (Button) findViewById(R.id.record);
        stopBtn = (Button) findViewById(R.id.stop);

        recordBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);

        mDatas.clear();
        try
        {
            FilenameFilter fileFilter = new FilenameFilter()  // 특정 확장자만 가지고 오고 싶을 경우 사용
            {
                public boolean accept(File dir, String name)
                {
                    return name.endsWith("mp4"); // 사용하고 싶은 확장자
                }
            };
            File file = new File(path);
            if(!file.exists()) {
                file.mkdirs();
            }
            File[] files = file.listFiles(fileFilter);
            String [] titleList = new String [files.length];
            for(int i = files.length-1; i >=0; i--)
            {
                titleList[i] = files[i].getName();
                mDatas.add (titleList[i].toString());
            }//end for

        } catch( Exception e )
        {
            e.printStackTrace();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mDatas);
        list = (ListView) findViewById(R.id.recordlist);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {                     // 리스트뷰의 아이템 클릭시
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(getApplicationContext(), ListenActivity.class);
                String file_name = list.getItemAtPosition(position).toString();
                it.putExtra("file_name", file_name);
                it.putExtra("position", position);
                startActivity(it);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.record:
                record();
                break;
            case R.id.stop:
                stop();
                break;
            default:
                break;
        }
    }

    public void record () {
        mCurRecTimeMs = 0;
        mCurProgressTimeDisplay = 0;

        SimpleDateFormat day = new SimpleDateFormat("yyyy.MM.dd.HHmmss");
        Date date = new Date();
        String fname = day.format(date);
        String dirPath = Environment.getExternalStorageDirectory().toString() + "/MyRecord";
        String PATH_NAME = dirPath+ "/" + fname + ".mp4";
        save_name = PATH_NAME;

        if(mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.reset();
        } else {
            mRecorder.reset();
        }

        try {

            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // AMR_NB를 MPEG_4로 바꿔준다.
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mRecorder.setOutputFile(PATH_NAME);
            mRecorder.prepare();
            mRecorder.start();
            mProgressHandler.sendEmptyMessageDelayed(0, 100);
        }   catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void stop () {
        if(mRecorder == null) {
            Toast.makeText(getApplicationContext(),
                    "저장할 것이 없습니다.", Toast.LENGTH_SHORT).show();
            return;

        } else if(mRecorder != null){
            try {
                mRecorder.stop();
                mCurRecTimeMs = -999;
                mProgressHandler.sendEmptyMessageDelayed(0, 0);
                Toast.makeText(getApplicationContext(),
                        "저장되었습니다", Toast.LENGTH_SHORT).show();
            }catch (Exception e) {
            } finally {
                mRecorder.release();
                mRecorder = null;

                mDatas.clear();
                try
                {
                    FilenameFilter fileFilter = new FilenameFilter()  // 특정 확장자만 가지고 오고 싶을 경우 사용
                    {
                        public boolean accept(File dir, String name)
                        {
                            return name.endsWith("mp4"); // 사용하고 싶은 확장자
                        }
                    };
                    File file = new File(path);
                    if(!file.exists()) {
                        file.mkdirs();
                    }
                    File[] files = file.listFiles(fileFilter);
                    String [] titleList = new String [files.length];
                    for(int i = files.length-1; i >=0; i--)
                    {
                        titleList[i] = files[i].getName();
                        mDatas.add (titleList[i].toString());
                    }

                } catch( Exception e )
                {
                    e.printStackTrace();
                }

                final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mDatas);
                list = (ListView) findViewById(R.id.recordlist);
                list.setAdapter(adapter);

            }
        }
    }
}
