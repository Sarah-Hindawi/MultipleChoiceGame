package com.hfad.mcq;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MyAsyncTask extends android.os.AsyncTask<URL, Void, String> {

    WeakReference<MyAsyncTask.communicate> d;

    public MyAsyncTask(MyAsyncTask.communicate dd) {
        d = new WeakReference<>(dd);
    }

    public interface communicate {

        //will pass into MainActivity the new String to update the TextView with
        void setResponse(String question, String answer, ArrayList<String> choices);

    }

    protected String doInBackground(URL... objects) {


        String str = "";
        try {
            HttpURLConnection h = (HttpURLConnection) objects[0].openConnection();
            h.setRequestMethod("POST");
            InputStream is = h.getInputStream();
            BufferedReader b = new BufferedReader(new InputStreamReader(is));

            String line;

            while ((line = b.readLine()) != null) str += line + "\n";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;

    }

    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            JSONObject j = new JSONObject(s);


            JSONArray jsonArray = (JSONArray) j.get("results");
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            String question = (String) jsonObject.get("question");
            String answer = (String) jsonObject.get("correct_answer");
            JSONArray choicesArray = (JSONArray) jsonObject.get("incorrect_answers");
            ArrayList<String> choices = new ArrayList<>();
            Log.d("currentWord", answer);


            for (int i = 0; i < 3; i++) choices.add(choicesArray.getString(i));

            MyAsyncTask.communicate a = d.get();


            if (a != null) {
                a.setResponse(question, answer, choices);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
