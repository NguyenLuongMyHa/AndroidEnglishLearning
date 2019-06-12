package com.myha.toeicwords;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.myha.toeicwords.databases.DatabaseHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoteViewActivity extends AppCompatActivity
{
    String title;
    DatabaseHelper myDbHelper;
    Cursor c = null;
    boolean startedFromShare=false;

    public String description;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        //received values
        Bundle bundle = getIntent().getExtras();
        title= bundle.getString("title");
        TextView textViewTitle = (TextView) findViewById(R.id.noteview_title);
        textViewTitle.setText(title);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Toolbar toolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                startedFromShare=true;

                if (sharedText != null) {
                    Pattern p = Pattern.compile("[A-Za-z ]{1,25}");
                    Matcher m = p.matcher(sharedText);

                    if(m.matches())
                    {
                        title=sharedText;
                    }
                    else
                    {
                        title="Not Available";
                    }

                }

            }
        }



        myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }


        c = myDbHelper.getNoteDescription(title);
        TextView textViewDescription = (TextView) findViewById(R.id.noteview_description);
        if (c.moveToFirst())
        {
            description = c.getString(c.getColumnIndex("description"));
            textViewDescription.setText(description);
        }
        else
        {
            title="Title Not Available";
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            if(startedFromShare)
            {
                Intent intent = new Intent(this,MyNoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else
            {
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
