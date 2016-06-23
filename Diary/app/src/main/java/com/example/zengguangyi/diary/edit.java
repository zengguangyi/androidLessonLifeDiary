package com.example.zengguangyi.diary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by zengguangyi on 2016/6/23.
 */
public class edit extends Activity {
    Button editBtn;
    EditText editTitle;
    EditText editContent;
    int edit_listnum;

    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);

        editTitle = (EditText)findViewById(R.id.edit_title);
        editContent = (EditText)findViewById(R.id.edit_content);
        editBtn = (Button)findViewById(R.id.edit_btn);

        /*接收main传过来的LISTNUM*/
        Bundle bundle = edit.this.getIntent().getExtras();
        edit_listnum = bundle.getInt("LISTNUM");
        Log.d("editA", "editlistnum: " + edit_listnum);

        /*读取该条日记*/
        SharedPreferences edit_sp = getSharedPreferences("data3", MODE_PRIVATE);
        editTitle.setText(edit_sp.getString("TITLE" + edit_listnum, ""));
        editContent.setText(edit_sp.getString("CONTENT" + edit_listnum, ""));

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                String content = editContent.getText().toString();

                 /*SharedPreferences存储数据*/
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_APPEND).edit();
                editor.putString("TITLE" + edit_listnum, title);
                editor.putString("CONTENT" + edit_listnum, content);
                editor.commit();

                /*跳转主活动*/
                Intent intent = new Intent(edit.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}
