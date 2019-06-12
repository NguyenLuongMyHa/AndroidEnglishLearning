package com.myha.toeicwords;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.myha.toeicwords.adapter.RecyclerViewAdapterHistory;
import com.myha.toeicwords.beans.History;
import com.myha.toeicwords.databases.DatabaseHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    SearchView search;

    SimpleCursorAdapter suggestionAdapter;

    static DatabaseHelper myDbHelper;
    static boolean databaseOpened = false;

    ArrayList<History> historyList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter historyAdapter;

    RelativeLayout emptyHistory;
    Cursor cursorHistory;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = (SearchView) findViewById(R.id.search_view);

        search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                search.setIconified(true);
            }
        });


        myDbHelper = new DatabaseHelper(this);

        if (myDbHelper.checkDataBase())
        {
            openDatabase();
        }
        else
        {
            LoadDatabaseAsync task = new LoadDatabaseAsync(MainActivity.this);
            task.execute();
        }


        final String[] from = new String[]{"en_word"};
        final int[] to = new int[]{R.id.suggestion_text};

        suggestionAdapter = new SimpleCursorAdapter(MainActivity.this,
                R.layout.suggestion_row, null, from, to, 0)
        {
            @Override
            public void changeCursor(Cursor cursor)
            {
                super.swapCursor(cursor);
            }

        };

        search.setSuggestionsAdapter(suggestionAdapter);

        search.setOnSuggestionListener(new SearchView.OnSuggestionListener()
        {
            @Override
            public boolean onSuggestionClick(int position)
            {
                try{
                    //Bat su kien click vao suggestion
                    CursorAdapter ca = search.getSuggestionsAdapter();
                    Cursor cursor = ca.getCursor();
                    cursor.moveToPosition(position);
                    String clicked_word = cursor.getString(cursor.getColumnIndex("en_word"));
                    search.setQuery(clicked_word, false);
                    search.clearFocus();
                    search.setFocusable(false);

                    Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word", clicked_word);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    return true;
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public boolean onSuggestionSelect(int position)
            {
                return true;
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                String text = search.getQuery().toString();

                Cursor c = myDbHelper.getMeaning(text);


                if (c.getCount() == 0)
                {
                    search.setQuery("", false);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                    builder.setTitle("Word Not Found");
                    builder.setMessage("Please search again");

                    String positiveText = getString(android.R.string.ok);
                    builder.setPositiveButton(positiveText,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                }
                            });

                    String negativeText = getString(android.R.string.cancel);
                    builder.setNegativeButton(negativeText,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    search.clearFocus();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    // display dialog
                    dialog.show();
                }

                else
                {
                    //search.setQuery("",false);
                    search.clearFocus();
                    search.setFocusable(false);

                    Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word", text);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }

                return false;
            }


            @Override
            public boolean onQueryTextChange(final String s)
            {

                search.setIconifiedByDefault(false); //Give Suggestion list margins
                Cursor cursorSuggestion = myDbHelper.getSuggestions(s);
                suggestionAdapter.changeCursor(cursorSuggestion);

                return false;
            }

        });
//-----------------------------
        emptyHistory = (RelativeLayout) findViewById(R.id.empty_history);

        //recycler View
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_history);
        layoutManager = new LinearLayoutManager(MainActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        fetch_history();

    }
    private void fetch_history()
    {
        historyList=new ArrayList<>();
        historyAdapter = new RecyclerViewAdapterHistory(this,historyList);
        recyclerView.setAdapter(historyAdapter);

        History h;

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

            historyAdapter.notifyDataSetChanged();
        }


        if (historyAdapter.getItemCount() == 0)
        {
            emptyHistory.setVisibility(View.VISIBLE);
        }
        else
        {
            emptyHistory.setVisibility(View.GONE);
        }
    }
    //--------------------------
    protected static void openDatabase()
    {
        try
        {
            myDbHelper.openDataBase();
            databaseOpened = true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //them item vao action bar (Setting, WordGuess game, Exit, Note
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button
        int id = item.getItemId();
        if (id == R.id.action_addnote)
        {
            Intent intent = new Intent(MainActivity.this, MyNoteActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_learn)
        {
            Intent intent = new Intent(MainActivity.this, GuessGameActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_exit)
        {
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        fetch_history();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
