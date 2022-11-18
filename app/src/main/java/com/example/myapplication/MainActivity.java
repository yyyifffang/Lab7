package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //建立兩個數值，用於計算兔子與烏龜的進度
    private int progressRabbit =0;
    private int progressTurtle = 0;

    private Button btn_start;
    private SeekBar sb_rabbit,sb_turtle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = findViewById(R.id.btn_start);
        sb_rabbit = findViewById(R.id.sb_rabbit);
        sb_turtle = findViewById(R.id.sb_turtle);

        //開始按鈕監聽器
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_start.setEnabled(false); //比賽期間不可重複操作
                progressRabbit = 0;//初始化進度
                progressTurtle = 0;
                sb_rabbit.setProgress(0);
                sb_turtle.setProgress(0);
                //執行方法
                runRabbit();
                runTurtle();
            }
        });
    }

    //建立handler物件接收訊息
    private final Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            //判斷編號，並更新seekbar的進度
            if (msg.what ==1)
                sb_rabbit.setProgress(progressRabbit);
            else if (msg.what ==2)
                sb_turtle.setProgress(progressTurtle);

            //判斷誰抵達終點
            if (progressRabbit >=100 && progressTurtle < 100) {
                Toast.makeText(MainActivity.this,"兔子勝利",Toast.LENGTH_SHORT).show(); //顯示兔子勝利
                btn_start.setEnabled(true); //按鈕可操作
            } else if(progressTurtle>=100 && progressRabbit<100){
                Toast.makeText(MainActivity.this,"烏龜勝利",Toast.LENGTH_SHORT).show(); //顯示烏龜勝利
                btn_start.setEnabled(true);//按鈕可操作
            }
            return false;

        }
    });

    private void  runRabbit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //兔子有三分之二的機率會偷懶
                boolean[] sleepProbability = {true,true,false};

                while (progressRabbit<=100 && progressTurtle<100){
                    try {
                        Thread.sleep(100);//延遲0.1秒更新賽況
                        //隨機產生0~2
                        if (sleepProbability[(int)(Math.random()*3)])
                            Thread.sleep(300);//兔子休息0.3秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressRabbit += 3; //每次跑三步

                    Message msg = new Message(); //建立message物件
                    msg.what =1;//加入編號
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void runTurtle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressTurtle <=100 && progressRabbit<100){
                    try {
                        Thread.sleep(100);//延遲
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    progressTurtle +=1;

                    Message msg = new Message();
                    msg.what=2;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
}