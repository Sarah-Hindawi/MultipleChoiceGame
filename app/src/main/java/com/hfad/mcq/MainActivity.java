package com.hfad.mcq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyAsyncTask.communicate {

    private TextView questiontv, result;
    private Button choice1, choice2, choice3, choice4, restartButton;
    private String question, answer, difficulty;
    private ArrayList<String> choices, questions;
    private ArrayList<Button> buttonArrayList;
    private int total, points, current, rand;
    private TextView[] firstSet, secondSet;
    private boolean restart;
    private MenuItem menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            answer = savedInstanceState.getString("answer");
            question = savedInstanceState.getString("question");
            difficulty = savedInstanceState.getString("difficulty");
            choices = savedInstanceState.getStringArrayList("choices");
            questions = savedInstanceState.getStringArrayList("questions");
            total = savedInstanceState.getInt("total");
            points = savedInstanceState.getInt("points");
            rand = savedInstanceState.getInt("random");
            current = savedInstanceState.getInt("current");
            restart = savedInstanceState.getBoolean("restart");
        }

        restartButton = (Button) findViewById(R.id.restartBtn);
        restartButton.setVisibility(View.INVISIBLE);

        questions = new ArrayList<>();
        if (points == 0) points = 1;

        questiontv = (TextView) findViewById(R.id.question);
        result = (TextView) findViewById(R.id.result);
        choice1 = (Button) findViewById(R.id.choice1);
        choice2 = (Button) findViewById(R.id.choice2);
        choice3 = (Button) findViewById(R.id.choice3);
        choice4 = (Button) findViewById(R.id.choice4);


        buttonArrayList = new ArrayList<>();
        buttonArrayList.add(choice1);
        buttonArrayList.add(choice2);
        buttonArrayList.add(choice3);
        buttonArrayList.add(choice4);

        restart = false;

        ViewGroup firstGroup = (ViewGroup) findViewById(R.id.firstQuestions);
        ViewGroup secondGroup = (ViewGroup) findViewById(R.id.lastQuestions);
        firstSet = new TextView[10];
        secondSet = new TextView[10];


        for (int i = 0; i < 10; i++) {
            firstSet[i] = (TextView) firstGroup.getChildAt(i);
            secondSet[i] = (TextView) secondGroup.getChildAt(i);
        }


        //for (int i = 0; i < 4; i++) buttonArrayList.get(i).setBackgroundColor(getResources().getColor(R.color.lightGrey));

        if (savedInstanceState == null) updateQuestion();
        else setViews(false);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("answer", answer);
        savedInstanceState.putString("question", question);
        savedInstanceState.putString("difficulty", difficulty);
        savedInstanceState.putStringArrayList("choices", choices);
        savedInstanceState.putStringArrayList("questions", questions);
        savedInstanceState.putInt("total", total);
        savedInstanceState.putInt("current", current);
        savedInstanceState.putInt("points", points);
        savedInstanceState.putInt("random", rand);
        savedInstanceState.putBoolean("restart", restart);
    }

    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.menu_main, m);
        return super.onCreateOptionsMenu(m);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        menuItem = mi;
        if (points >= 6) {
            mi.setEnabled(false);
            if (rand == 1 || rand == 4) {
                buttonArrayList.get(1).setVisibility(View.INVISIBLE);
                buttonArrayList.get(2).setVisibility(View.INVISIBLE);
            } else {
                buttonArrayList.get(0).setVisibility(View.INVISIBLE);
                buttonArrayList.get(3).setVisibility(View.INVISIBLE);
            }
        } else {
            CharSequence text = "This feature is enabled when the fifth question is completed";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
        return super.onOptionsItemSelected(mi);
    }

    public void onClick(View v) {
        if (restart) {
            restartButton.setVisibility(View.INVISIBLE);
            restart();
            return;
        }
        Button chosen = (Button) v;


        if (chosen.getText().equals(answer)) {

            //chosen.setBackgroundColor(getResources().getColor(R.color.green));
            points++;
            if (difficulty.equals("easy")) total += 1000;
            else if (difficulty.equals("medium")) total += 3000;
            else total += 5000;
            if (points <= 11)
                firstSet[points - 2].setBackgroundColor(getResources().getColor(R.color.green));
            else secondSet[points - 12].setBackgroundColor(getResources().getColor(R.color.green));

            Log.d("points", Integer.toString(points));
            if (points == 6 || points == 11 || points == 16) current = total;
            if (points == 21) {
                win();
                return;
            }

        } else {
            //chosen.setBackgroundColor(getResources().getColor(R.color.red));
            CharSequence text = "Correct Answer: " + answer;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
            endGame();
            return;
        }
//        for (int i = 0; i < 1000000; i++) for (int j = 0; j < 10000; j++)
//        chosen.setBackgroundColor(getResources().getColor(R.color.lightGrey));

        if (buttonArrayList.get(0).getVisibility() == View.INVISIBLE) {
            buttonArrayList.get(0).setVisibility(View.VISIBLE);
            buttonArrayList.get(3).setVisibility(View.VISIBLE);
        } else if (buttonArrayList.get(1).getVisibility() == View.INVISIBLE) {
            buttonArrayList.get(1).setVisibility(View.VISIBLE);
            buttonArrayList.get(2).setVisibility(View.VISIBLE);
        }
        result.setText("$" + total);
        updateQuestion();
    }

    public void endGame() {

        arrangeEnd();

        if (points > 10)
            secondSet[points - 11].setBackgroundColor(getResources().getColor(R.color.red));
        else firstSet[points - 1].setBackgroundColor(getResources().getColor(R.color.red));

        result.setText("$" + current);
        questiontv.setText(getResources().getString(R.string.over, current));
    }

    public void win() {

        arrangeEnd();
        result.setText("$500000");
        questiontv.setText(getResources().getString(R.string.win));

    }

    public void arrangeEnd() {
        for (int i = 0; i < buttonArrayList.size(); i++)
            buttonArrayList.get(i).setVisibility(View.INVISIBLE);

        restartButton.setVisibility(View.VISIBLE);
        restart = true;
    }

    public void restart() {
        if (menuItem != null) menuItem.setEnabled(true);
        restart = false;
        result.setText("");
        for (int i = 0; i < buttonArrayList.size(); i++)
            buttonArrayList.get(i).setVisibility(View.VISIBLE);

        for (int i = 0; i < 10; i++)
            firstSet[i].setBackgroundColor(getResources().getColor(R.color.lightGrey));
        for (int i = 0; i < 10; i++)
            secondSet[i].setBackgroundColor(getResources().getColor(R.color.lightGrey));
        total = current = 0;
        points = 1;
        questions.clear();
        updateQuestion();
    }


    @Override
    public void setResponse(String Question, String Answer, ArrayList<String> choicesList) {
        question = String.valueOf(Html.fromHtml(Question));
        answer = String.valueOf(Html.fromHtml(Answer));
        choices = choicesList;

        if (!questions.contains(question)) {
            questions.add(question);
            setViews(true);
        } else updateQuestion();
    }



    public void setViews(boolean isNew) {
        if (!isNew) {
            result.setText("$" + total);
            for (int i = 0; i < points; i++) {
                if (i < 10) firstSet[i].setBackgroundColor(getResources().getColor(R.color.green));
                else secondSet[i].setBackgroundColor(getResources().getColor(R.color.green));
            }
        } else rand = 1 + (int) (Math.random() * ((4 - 1) + 1));

        questiontv.setText(question);

        if (points > 10)
            secondSet[points - 11].setBackgroundColor(getResources().getColor(R.color.orange));
        else firstSet[points - 1].setBackgroundColor(getResources().getColor(R.color.orange));

        if (rand == 1) choice1.setText(answer);
        else if (rand == 2) choice2.setText(answer);
        else if (rand == 3) choice3.setText(answer);
        else choice4.setText(answer);

        int wrong = 0;
        for (int i = 0; i < 4; i++) {
            Button btn = buttonArrayList.get(i);
            if (!btn.getText().equals(answer)) {
                btn.setText(String.valueOf(Html.fromHtml(choices.get(wrong))));
                wrong++;
            }
        }
    }

    public void updateQuestion() {
        int category = 9; //general knowledge
        int randCategory = 1 + (int) (Math.random() * ((4 - 1) + 1));
        if (randCategory == 1) category = 17;//science and nature
        else if (randCategory == 2) category = 18;//computers
        else if (randCategory == 3) category = 22;//geography

        try {
            difficulty = "easy";
            if (points > 15) difficulty = "hard";
            else if (points > 5) difficulty = "medium";
            //fetching questions from the API
            URL url = new URL("https://opentdb.com/api.php?amount=1&category=" + category + "&difficulty=" + difficulty + "&type=multiple");
            // the class that will handel passing url to the API
            new MyAsyncTask(this).execute(url);
        } catch (
                MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
