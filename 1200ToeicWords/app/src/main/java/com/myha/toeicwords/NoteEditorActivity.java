package com.myha.toeicwords;


import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.myha.toeicwords.databases.DatabaseHelper;

public class NoteEditorActivity extends AppCompatActivity
{
    int action;//Add hay Edit
    DatabaseHelper myDbHelper;
    String title;
    String description;
    String oldtitle;
    private EditText textTitle;
    private EditText textDescription;
    private static final int MODE_CREATE = 1;
    private static final int MODE_EDIT = 2;

    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        //received values
        Bundle bundle = getIntent().getExtras();
        action= bundle.getInt("mode");
        title = bundle.getString("title");
        description = bundle.getString("description");
        this.textTitle = (EditText) findViewById(R.id.note_edit_title);
        this.textDescription = (EditText) findViewById(R.id.note_edit_description);

        if(action== 1)  {
            this.mode = MODE_CREATE;
        } else
        {
            this.mode = MODE_EDIT;
            this.textTitle.setText(title);
            this.textDescription.setText(description);
            this.oldtitle = this.textTitle.getText().toString();
        }

        myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }
    public void buttonSaveClicked(View view)  {
        String title = this.textTitle.getText().toString();
        String desc = this.textDescription.getText().toString();

        if(title.equals("") || desc.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Vui lòng nhập title và description", Toast.LENGTH_LONG).show();
            return;
        }

        if(mode==MODE_CREATE ){

            myDbHelper.addNote(title, desc);
            Toast.makeText(getApplicationContext(),
                    "Đã thêm một note", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(NoteEditorActivity.this, MyNoteActivity.class);
            startActivity(intent);


        } else  {

            myDbHelper.updateNote(oldtitle,title, desc);
            Toast.makeText(getApplicationContext(),
                    "Đã sửa một note", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(NoteEditorActivity.this, MyNoteActivity.class);
            startActivity(intent);

        }
    }
}
