package com.example.gecko.ereader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.mob.tools.utils.ResHelper.getStringRes;


public class TwoActivity extends Activity implements View.OnClickListener {



    private EditText phone;

    private EditText cord;

    private TextView now;

    private Button getCord;


    private String iPhone;

    private int time = 60;

    private boolean flag = true;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.two);

        CTextView cTextView = findViewById(R.id.cTextView);
        cTextView.setText("Welcome", AnimationUtils.loadAnimation(this, R.anim.myanim), 300);

        init();

        MobSDK.init(this,"23683e269e3d4","19945e070108b210090c287cb7e72985");
        EventHandler eh=new EventHandler(){



            @Override

            public void afterEvent(int event, int result, Object data) {



                Message msg = new Message();

                msg.arg1 = event;

                msg.arg2 = result;

                msg.obj = data;

                handler.sendMessage(msg);

            }



        };

        SMSSDK.registerEventHandler(eh);



    }



    private void init() {

        phone = findViewById(R.id.textView3);

        cord = findViewById(R.id.textView5);

        now = findViewById(R.id.now);

        getCord = findViewById(R.id.getcord);

        Button saveCord = findViewById(R.id.savecord);

        Button button2 = findViewById(R.id.button2);

        getCord.setOnClickListener(this);

        saveCord.setOnClickListener(this);

        button2.setOnClickListener(this);

    }



    @Override

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.getcord:

                if(!TextUtils.isEmpty(phone.getText().toString().trim())){

                    if(phone.getText().toString().trim().length()==11){

                        iPhone = phone.getText().toString().trim();

                        SMSSDK.getVerificationCode("86",iPhone);

                        cord.requestFocus();

                        getCord.setVisibility(View.GONE);

                    }else{

                        Toast.makeText(TwoActivity.this, "请输入完整电话号码", Toast.LENGTH_LONG).show();

                        phone.requestFocus();

                    }

                }else{

                    Toast.makeText(TwoActivity.this, "请输入您的电话号码", Toast.LENGTH_LONG).show();

                    phone.requestFocus();

                }

                break;



            case R.id.savecord:

                if(!TextUtils.isEmpty(cord.getText().toString().trim())){

                    if(cord.getText().toString().trim().length()==4){

                        String iCord = cord.getText().toString().trim();

                        SMSSDK.submitVerificationCode("86", iPhone, iCord);

                        flag = false;
                        Intent intent =new Intent(this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }else{

                        Toast.makeText(TwoActivity.this, "请输入完整验证码", Toast.LENGTH_LONG).show();

                        cord.requestFocus();

                    }

                }else{

                    Toast.makeText(TwoActivity.this, "请输入验证码", Toast.LENGTH_LONG).show();

                    cord.requestFocus();

                }

                break;

            case R.id.button2:
                Intent intent = new Intent(TwoActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);



            default:

                break;

        }

    }



    //验证码送成功后提示文字

    private void reminderText() {

        now.setVisibility(View.VISIBLE);

        handlerText.sendEmptyMessageDelayed(1, 1000);

    }



    Handler handlerText =new Handler(){

        public void handleMessage(Message msg) {

            if(msg.what==1){

                if(time>0){

                    now.setText("验证码已发送"+time+"秒");

                    time--;

                    handlerText.sendEmptyMessageDelayed(1, 1000);

                }else{

                    now.setText("提示信息");

                    time = 60;

                    now.setVisibility(View.GONE);

                    getCord.setVisibility(View.VISIBLE);

                }

            }else{

                cord.setText("");

                now.setText("提示信息");

                time = 60;

                now.setVisibility(View.GONE);

                getCord.setVisibility(View.VISIBLE);

            }

        };

    };



    Handler handler=new Handler(){



        @Override

        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub

            super.handleMessage(msg);

            int event = msg.arg1;

            int result = msg.arg2;

            Object data = msg.obj;

            Log.e("event", "event="+event);

            if (result == SMSSDK.RESULT_COMPLETE) {

                //短信注册成功后，返回MainActivity,然后提示新好友

                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功,验证通过

                    Toast.makeText(getApplicationContext(), "验证码校验成功", Toast.LENGTH_SHORT).show();
                    String str=phone.getText().toString();
                    SharedPreferences sp =TwoActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit=sp.edit();
                    edit.putString("phone",str);
                    edit.commit();
                    Intent intent = new Intent(TwoActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

                    handlerText.sendEmptyMessage(2);

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){//服务器验证码发送成功

                    reminderText();

                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();

                }else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表

                    Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();

                }

            } else {

                if(flag){

                    getCord.setVisibility(View.VISIBLE);

                    Toast.makeText(TwoActivity.this, "验证码获取失败，请重新获取", Toast.LENGTH_SHORT).show();

                    phone.requestFocus();

                }else{

                    ((Throwable) data).printStackTrace();

                    int resId = getStringRes(TwoActivity.this, "smssdk_network_error");

                    Toast.makeText(TwoActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();

                    cord.selectAll();

                    if (resId > 0) {

                        Toast.makeText(TwoActivity.this, resId, Toast.LENGTH_SHORT).show();

                    }

                }



            }



        }



    };



    @Override

    protected void onDestroy() {

        super.onDestroy();

        SMSSDK.unregisterAllEventHandler();

    }



}
