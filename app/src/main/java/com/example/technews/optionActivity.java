package com.example.technews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class optionActivity extends AppCompatActivity {
    ArrayList<String> titles = new ArrayList<>
            (Arrays.asList("business", "entertainment", "health","science","sports","technology"));

    public void dropIn(View v)
    {
        ImageView counter = (ImageView)v;
        int tapcount = Integer.parseInt(counter.getTag().toString());
        Toast.makeText(getApplicationContext(),
                titles.get(tapcount)+ " news", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("key", titles.get(tapcount));
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        /*GridView grid = (GridView) findViewById(R.id.grid);


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titles);

        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText()+ " news", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("key", ((TextView) v).getText());
                startActivity(intent);
            }
        });*/


    }
}
