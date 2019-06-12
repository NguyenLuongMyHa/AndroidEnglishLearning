package com.myha.toeicwords;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.RelativeLayout;

import com.myha.toeicwords.adapter.RecyclerViewAdapterNote;
import com.myha.toeicwords.beans.Note;
import com.myha.toeicwords.databases.DatabaseHelper;

import java.util.ArrayList;

public class MyNoteActivity extends AppCompatActivity
{
    static DatabaseHelper myDbHelper;
    static boolean databaseOpened = false;

    ArrayList<Note> noteList;
    RecyclerView recyclerView;

    android.support.v7.widget.RecyclerView.LayoutManager layoutNoteManager;
    RecyclerView.Adapter noteAdapter;
    RelativeLayout emptyNote;
    Cursor cursorNote;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        myDbHelper = new DatabaseHelper(this);

        if (myDbHelper.checkDataBase())
        {
            openDatabase();
            Log.i("Open Database","Da open");
        }
        else
        {
            LoadDatabaseAsync task = new LoadDatabaseAsync(MyNoteActivity.this);
            task.execute();
        }

        emptyNote = (RelativeLayout) findViewById(R.id.empty_note);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_note);

        layoutNoteManager = new LinearLayoutManager(MyNoteActivity.this);
        recyclerView.setLayoutManager(layoutNoteManager);
        fetch_note();



    }


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

    private void fetch_note()
    {
        noteList=new ArrayList<>();
        noteAdapter = new RecyclerViewAdapterNote(this,noteList);
        recyclerView.setAdapter(noteAdapter);

        Note h;

        if(databaseOpened)
        {
            cursorNote=myDbHelper.getNote();
            if (cursorNote.moveToFirst()) {
                do {
                    h= new Note(cursorNote.getString(cursorNote.getColumnIndex("title")),cursorNote.getString(cursorNote.getColumnIndex("description")));
                    noteList.add(h);
                }
                while (cursorNote.moveToNext());
            }

            noteAdapter.notifyDataSetChanged();
        }


        if (noteAdapter.getItemCount() == 0)
        {
            emptyNote.setVisibility(View.VISIBLE);
        }
        else
        {
            emptyNote.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id = item.getItemId();
        if (id == R.id.action_addnote)
        {
            Intent intent = new Intent(MyNoteActivity.this, NoteEditorActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("mode",1);//Add
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
                Intent intent = new Intent(MyNoteActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        fetch_note();
    }


}

