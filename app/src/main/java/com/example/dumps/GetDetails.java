package com.example.dumps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class GetDetails extends AppCompatActivity {

    EditText land_marks;
    EditText extrainfo;
    Button submit;
    //Boolean checked;
    private RadioGroup radio_group;
    private RadioButton radio_btn;
    String text1,text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_details);

        radio_group=(RadioGroup)findViewById(R.id.radio_group);
        land_marks=(EditText)findViewById(R.id.landmarks);
        extrainfo=(EditText)findViewById(R.id.extra_info);
        submit=(Button)findViewById(R.id.submit_);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text1=land_marks.getText().toString().trim();
                text2=extrainfo.getText().toString().trim();
                if(radio_group.getCheckedRadioButtonId()==-1 || text1.matches("") || text2.matches(""))
                {
                    Toast.makeText(GetDetails.this,"Please enter all the details",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    int selectedId=radio_group.getCheckedRadioButtonId();
                    radio_btn=(RadioButton)findViewById(selectedId);
                    Toast.makeText(GetDetails.this,radio_btn.getText(),Toast.LENGTH_SHORT).show();
                    finish();
                }

        }});

    }
}
