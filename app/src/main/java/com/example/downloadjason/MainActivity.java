package com.example.downloadjason;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    TextView userInput,userOutput;
    String info="";
    public class DownloadJSon extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String res ="";
            URL url;
            HttpURLConnection httpURLConnection = null;

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    res += current;
                    data = reader.read();
                }
                reader.close();
                return res;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                info = "Weather in " + userInput.getText().toString() + ":\n";
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray jsonArray = new JSONArray(weatherInfo);
                for(int i = 0; i < jsonArray.length();i++){
                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    info += "Main: " + jsonPart.getString("main") + "\n";
                    info += "Description: " + jsonPart.getString("description") + "\n";
//                    info += "Temperature: " + jsonPart.getString("temp") + "\n";
                }
                String tempInfo = jsonObject.getString("main");
                JSONObject object = new JSONObject(tempInfo);
                info += "Temp: " + object.getString("temp") + "\n";

                userOutput.setText(info);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Invalid City's Name. Please correct the input!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Texts
        userInput = findViewById(R.id.userInput);
        userOutput = findViewById(R.id.userOutput);


    }

    public void checkWeather(View view){
        DownloadJSon downloadJSon = new DownloadJSon();
        downloadJSon.execute("https://openweathermap.org/data/2.5/weather?q="+userInput.getText().toString()+"&appid=439d4b804bc8187953eb36d2a8c26a02");
        //hide the soft keyboard
        hideSoftKeyboard(this);
    }
    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}