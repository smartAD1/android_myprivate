package com.iii.privateapplication;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private AccountManager amgr;
    private TelephonyManager tmgr;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView)findViewById(R.id.img);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        1);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.GET_ACCOUNTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        1);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);
            }
        }

        tmgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        String lineum = tmgr.getLine1Number();
        String imei = tmgr.getSubscriberId();
        Log.d("brad", lineum);
        Log.d("brad", imei);

        tmgr.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);

        amgr = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = amgr.getAccounts();
        for (Account account : accounts) {
            String accountName = account.name;
            String accountType = account.type;
            Log.d("brad", accountName + ":" + accountType);
        }

    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if (state == TelephonyManager.CALL_STATE_IDLE) {
                //change to idel
                Log.d("brad", "off");
            } else if (state == TelephonyManager.CALL_STATE_RINGING) {
                Log.d("brad", incomingNumber);
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //
                Log.d("brad", "talk");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    public void test1(View v){// 挖出別人的電話 名字
        ContentResolver contentResolver = getContentResolver();

        String name = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        String num = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Cursor c = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                ,new String[]{name,num},null,null,name);

        int count = c.getCount();
        Log.d("brad", "Count:" +count);

        while (c.moveToNext()){
            String dname = c.getString(c.getColumnIndex(name));
            String dnum = c.getString(c.getColumnIndex(num));
            Log.d("brad", dname + ":" + dnum);
        }

    }
    public void test2(View v){//挖出別人的電話 照片 名字
        ContentResolver contentResolver = getContentResolver();

        Uri sim = Uri.parse("content://icc/adn");

        String name = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        String num = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Cursor c = contentResolver.query(
                sim
                ,null,null,null,name);

        int count = c.getCount();
        Log.d("brad", "Count:" +count);

        while (c.moveToNext()){
            String dname = c.getString(c.getColumnIndex("name"));
            String dnum = c.getString(c.getColumnIndex("number"));
            Log.d("brad", dname + ":" + dnum);
        }

    }
    public void test3(View v){//輸出最近一張新拍的照片
        ContentResolver contentResolver = getContentResolver();
        Cursor c =contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null);

        c.moveToNext();
        String photo = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
        Log.d("brad",photo);
        img.setImageBitmap(BitmapFactory.decodeFile(photo));
    }
}