package com.example.zengguangyi.diary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);

        editTitle = (EditText)findViewById(R.id.edit_title);
        editContent = (EditText)findViewById(R.id.edit_content);
        editBtn = (Button)findViewById(R.id.edit_btn);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                String content = editContent.getText().toString();

                 /*SharedPreferences存储数据*/
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("TITLE",title);
                editor.putString("CONTENT", content);
                editor.commit();

                /*跳转主活动*/
                Intent intent = new Intent(edit.this,MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("ID",102);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }
}
