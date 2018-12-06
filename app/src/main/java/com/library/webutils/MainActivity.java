package com.library.webutils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.library.view.webview.WebActivity;
import com.library.view.webview.WebNavigationActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_1, bt_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListener();
    }


    private void initViews() {
        bt_1 = findViewById(R.id.bt_1);
        bt_2 = findViewById(R.id.bt_2);
    }

    private void initListener() {
        bt_1.setOnClickListener(this);
        bt_2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_1:
                WebActivity.show(MainActivity.this, "https://www.baidu.com/");
                break;
            case R.id.bt_2:
                WebNavigationActivity.show(MainActivity.this, "https://www.baidu.com/");
                break;
        }
    }
}
