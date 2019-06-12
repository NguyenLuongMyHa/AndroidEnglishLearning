package com.myha.toeicwords;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.myha.toeicwords.beans.History;
import com.myha.toeicwords.databases.DatabaseHelper;

import java.util.ArrayList;
import java.util.Random;

public class GuessGameActivity extends AppCompatActivity
{
    DatabaseHelper myDbHelper;
    Cursor cursorHistory;

    boolean databaseOpened = false;
    ArrayList<History> historyList;
    ArrayList<String> words;
    ArrayList<String> hints;
    String word; //current word to guess
    Button btn_submit; //Button that submits choice
    TextView ti_guess; //Text input where the guesses are
    TextView tv_hint; //A description of the chosen word
    TextView tv_cipher; //**** representation of a word
    AlertDialog.Builder builder;
    private enum TO_TOAST { HIS_EMPTY, WORD_OK, WORD_BAD, LETTER_OK, LETTER_BAD, TOTAL_BAD }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_guessgame);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Đoán từ");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        btn_submit = findViewById(R.id.btn_guess);
        ti_guess = findViewById(R.id.ti_guess);
        tv_hint = findViewById(R.id.tv_hint);
        tv_cipher = findViewById(R.id.tv_cipher);
        words = new ArrayList<>();
        hints = new ArrayList<>();

        setWords();
        if(words.size()!=0)
        {
            initiate();
        }
        else
        {
            toaster(TO_TOAST.HIS_EMPTY);
        }
        btn_submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                submit(ti_guess.getText().toString());
            }
        });
    }

    private void initiate()
    {
        setWord();
        tv_cipher.setText(setCipher());
    }

    private void reinit()
    {
        try
        {
            wait(1000);
            initiate();
        }
        catch (Exception e)
        {
            Toast.makeText(GuessGameActivity.this, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT);
            Log.d("EXC", e.getMessage().toString());
        }
    }

    //TODO: change to database access
    private void setWords()
    {
        myDbHelper = new DatabaseHelper(this);
        try
        {
            myDbHelper.openDataBase();
            databaseOpened = true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        History h;
        historyList=new ArrayList<>();
        if(databaseOpened)
        {
            cursorHistory=myDbHelper.getHistory();
            if (cursorHistory.moveToFirst()) {
                do {
                    h= new History(cursorHistory.getString(cursorHistory.getColumnIndex("word")),cursorHistory.getString(cursorHistory.getColumnIndex("vi_definition")));
                    historyList.add(h);
                }
                while (cursorHistory.moveToNext());
            }
        }
        for(int i=0;i<historyList.size();i++)
        {
            words.add(historyList.get(i).get_en_word());
            hints.add(historyList.get(i).get_def());
        }
    }

    //sets current guess word
    private void setWord()
    {
        int pos;
        Random r = new Random();
        do
        {
            pos = r.nextInt(words.size());
        }
        while (word == words.get(pos));
        word = words.get(pos);
        tv_hint.setText(hints.get(pos));
    }

    //sets cipher for the first time
    private String setCipher()
    {
        String s = "";
        for (int i = 0; i < word.length(); i++)
        {
            s += "*";
        }
        return s;
    }

    //sets cipher opening guessed letter at a given position
    @NonNull
    private String setCipher(int position, char c)
    {
        StringBuilder s = new StringBuilder(tv_cipher.getText().toString());
        s.setCharAt(position, c);
        return s.toString();
    }

    private void submit(String s)
    {
        if (s.length() > 0)
        {
            //if text entered is a word
            if (s.length() > 1)
            {
                //if the guess is correct
                if (ti_guess.getText().toString().equals(word))
                {
                    toaster(TO_TOAST.WORD_OK);
                    tv_cipher.setText(word);
                    initiate();
                }
                //if the word guess is incorrect
                else
                {
                    toaster(TO_TOAST.WORD_BAD);
                }
            }
            //if text entered is a letter
            else
            {
                //if the letter guess is correct
                if (word.contains(s))
                {
                    char[] charword = word.toCharArray();
                    int pos = 0;
                    for (char c : charword
                    )
                    {
                        if (c == s.charAt(0))
                        {
                            tv_cipher.setText(setCipher(pos, c));
                        }
                        pos++;
                    }
                    if (tv_cipher.getText().toString().contains("*"))
                        toaster(TO_TOAST.LETTER_OK);
                    else
                    {
                        toaster(TO_TOAST.WORD_OK);
                        initiate();
                    }

                }
                //if the letter guess is incorrect
                else
                {
                    toaster(TO_TOAST.LETTER_BAD);
                }
            }
        }
        else toaster(TO_TOAST.TOTAL_BAD);
        ti_guess.setText(null);
    }

    private void toaster(TO_TOAST toToast)
    {
        try
        {
            switch (toToast)
            {
                case HIS_EMPTY:
                    Toast.makeText(GuessGameActivity.this, R.string.result_his_empty, Toast.LENGTH_SHORT).show();
                    break;
                case WORD_OK:
                    Toast.makeText(GuessGameActivity.this, R.string.result_word_ok, Toast.LENGTH_SHORT).show();
                    break;
                case WORD_BAD:
                    Toast.makeText(GuessGameActivity.this, R.string.result_word_bad, Toast.LENGTH_SHORT).show();
                    break;
                case LETTER_OK:
                    Toast.makeText(GuessGameActivity.this, R.string.result_letter_ok, Toast.LENGTH_SHORT).show();
                    break;
                case LETTER_BAD:
                    Toast.makeText(GuessGameActivity.this, R.string.result_letter_bad, Toast.LENGTH_SHORT).show();
                    break;
                case TOTAL_BAD:
                    Toast.makeText(GuessGameActivity.this, R.string.result_total_bad, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        catch (Exception e)
        {
            Log.d("EXC", e.getMessage());
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}