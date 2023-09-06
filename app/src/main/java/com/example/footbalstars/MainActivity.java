package com.example.footbalstars;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;
    private ImageView imageViewFoot;
    private final String URL = "https://football-fun-live.com/reiting-futbolistov/p-top-100-futbolistov-po-versii-the-guardian";
    private ArrayList<String> urls;
    private ArrayList<String> names;
    private ArrayList<String> urls2;
    private int numberOfQuestion;
    private int numberOfRightAns;
    private ArrayList<Button> buttons;
    private int schet = 0;
    private int rigthAnswer = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 =findViewById(R.id.button3);
        buttons = new ArrayList<>();
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        imageViewFoot = findViewById(R.id.imageViewFootball);
        urls = new ArrayList<>();
        names = new ArrayList<>();
        urls2 = new ArrayList<>();
        try {
            getContent();
            playGames();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void playGames() throws ExecutionException, InterruptedException {
generateQuestion();
    DownloadImageTask downloadImageTask = new DownloadImageTask();
    Bitmap bitmap = downloadImageTask.execute(urls2.get(numberOfQuestion)).get();
    imageViewFoot.setImageBitmap(bitmap);
    for (int i = 0 ; i< buttons.size();i++){
        if (i == numberOfRightAns){
            buttons.get(i).setText(names.get(numberOfQuestion));
        }
        else {
            int wrongans = generateWrongAnswer();
            buttons.get(i).setText(names.get(wrongans));
        }
    }
    }

    private void generateQuestion(){
        numberOfQuestion = (int) (Math.random() * names.size());
        numberOfRightAns = (int) (Math.random() * buttons.size());
    }
    private int generateWrongAnswer(){
        return (int) (Math.random() * names.size());
    }
    private void getContent() throws ExecutionException, InterruptedException {
DownloadContentTask taskcontent = new DownloadContentTask();
String content = taskcontent.execute(URL).get();
String start = "<th>Рабочия Нога</th>";
String finish = "<img src=\"https://football-fun-live.com/assets/img/icons/share/post-facebook-white.png\" alt=\"facebook\">";
        Pattern pattern = Pattern.compile(start + "(.*?)" + finish );
        Matcher matcher = pattern.matcher(content);
        String splitCont ="";
        while (matcher.find()){
            splitCont = matcher.group(1);
        }
        Pattern patternImage = Pattern.compile("<img src=\"(.*?)\"");
        Pattern patternName = Pattern.compile("<span class=\"a-table2__text a-table2__nowrap\">(.*?)</span>");
        Matcher matcherImage = patternImage.matcher(splitCont);
        Matcher matcherName = patternName.matcher(splitCont);
        while (matcherImage.find()){
            urls.add(matcherImage.group(1));
        }
        while (matcherName.find()){
            names.add(matcherName.group(1));
        }

        for (int a = 0; a<urls.size(); a++){
          if (urls.get(a).contains("players") || urls.get(a).contains("37")){
              urls2.add(urls.get(a));
          }
        }
        for (String f:urls2){
            Log.i("URLS",f);
        }
        Log.i("URLS", String.valueOf(urls2.size()));
        Log.i("URLS", String.valueOf(names.size()));
    }

    public void onClickAnswers(View view) throws ExecutionException, InterruptedException {

        Button button = (Button) view;
        String tag = button.getTag().toString();
        if (Integer.parseInt(tag) == numberOfRightAns){
            Toast.makeText(this,"Верно", Toast.LENGTH_SHORT).show();
            rigthAnswer++;
        }
        else Toast.makeText(this,"Неверно, правильный ответ "+ names.get(numberOfQuestion), Toast.LENGTH_SHORT).show();
        playGames();
        schet++;
        if (schet>=25){
Intent intent = new Intent(this,ResultActivity.class);
intent.putExtra("result",rigthAnswer);
startActivity(intent);
        }
    }


    private static class DownloadContentTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection httpURLConnection = null;
            StringBuilder result = new StringBuilder();

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while(line != null){
                result.append(line);
                line = bufferedReader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
               if (httpURLConnection != null) {
                   httpURLConnection.disconnect();
               }
            }
            return null;
        }
    }

    private static class DownloadImageTask extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection httpURLConnection = null;
            StringBuilder result = new StringBuilder();

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }
    }
}