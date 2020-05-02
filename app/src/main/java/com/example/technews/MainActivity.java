package com.example.technews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> content = new ArrayList<>();

    ArrayAdapter arrayAdapter;
    SQLiteDatabase articlesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i=getIntent();
        String key=i.getStringExtra("key");
        getSupportActionBar().setTitle(key+" News");
        articlesDB = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        articlesDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, articleId, INTEGER, title VARCHAR, content VARCHAR)");
        DownloadTask task = new DownloadTask();
        try {

           String x =task.execute("https://newsapi.org/v2/top-headlines?country=in&category="+key+"&apiKey=bcadeae4607548d68bf1c36dffec0658").get();
           // Log.i("this", x);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ListView listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                intent.putExtra("content", content.get(i));
                startActivity(intent);
            }
        });
        updateListView();

    }

    public void updateListView() {
        Cursor c = articlesDB.rawQuery("SELECT * FROM articles", null);

        int contentIndex = c.getColumnIndex("content");
        int titleIndex = c.getColumnIndex("title");

        if (c.moveToFirst()) {
            titles.clear();
            content.clear();

            do {

                titles.add(c.getString(titleIndex));
                content.add(c.getString(contentIndex));

            } while (c.moveToNext());

            arrayAdapter.notifyDataSetChanged();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result;
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
               // Log.i("this", url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuffer sb = new StringBuffer();
                String str;
                while((str = reader.readLine())!= null){
                    sb.append(str);
                }
              result=sb.toString();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = (JSONArray)jsonObject.get("articles");

                int numberOfItems = 40;

                if (jsonArray.length() < 40) {
                    numberOfItems = jsonArray.length();
                }

                articlesDB.execSQL("DELETE FROM articles");
                for (int i=0;i < numberOfItems; i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    if (!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                        String articleTitle = jsonObject.getString("title");
                        String articleUrl = jsonObject.getString("url");

                        Log.i("HTML", articleUrl);

                        String sql = "INSERT INTO articles (articleId, title, content) VALUES (?, ?, ?)";
                        SQLiteStatement statement = articlesDB.compileStatement(sql);
                        statement.bindString(1,Integer.toString(i));
                        statement.bindString(2,articleTitle);
                        statement.bindString(3,articleUrl);
                        statement.execute();
                        Log.i("title", jsonObject.getString("title"));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            updateListView();
        }
    }
}