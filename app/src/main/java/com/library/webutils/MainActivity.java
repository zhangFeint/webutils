package com.library.webutils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.library.view.webview.WebActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.show(MainActivity.this,"https://www.baidu.com/");

            }
        });
    }
}
