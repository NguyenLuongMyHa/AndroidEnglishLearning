package com.myha.toeicwords.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.myha.toeicwords.databases.DatabaseHelper;
import com.myha.toeicwords.MyNoteActivity;
import com.myha.toeicwords.NoteEditorActivity;
import com.myha.toeicwords.NoteViewActivity;
import com.myha.toeicwords.R;
import com.myha.toeicwords.beans.Note;

import java.util.ArrayList;


public class RecyclerViewAdapterNote extends RecyclerView.Adapter<RecyclerViewAdapterNote.NoteViewHolder>
{
    private ArrayList<Note> notes;
    private Context context;
    private static final int MENU_ITEM_EDIT = 111;
    private static final int MENU_ITEM_DELETE = 222;
    public RecyclerViewAdapterNote(Context context, ArrayList<Note> notes)
    {
        this.notes = notes;
        this.context = context;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder  implements View.OnCreateContextMenuListener
    {
        TextView title;
        TextView description;


        public NoteViewHolder(View v)
        {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            description = (TextView) v.findViewById(R.id.description);

            v.setOnCreateContextMenuListener(this);

            v.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int position = getAdapterPosition();
                    String text = notes.get(position).get_title();

                    Intent intent = new Intent(context, NoteViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", text);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==111)//EDIT
                {
                    int position = getAdapterPosition();
                    String title = notes.get(position).get_title();
                    String desc = notes.get(position).get_description();
                    Intent intent = new Intent(context, NoteEditorActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", title);
                    bundle.putString("description", desc );
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
                return true;
            }
        };
        private final MenuItem.OnMenuItemClickListener onDeleteMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==222)//delete
                {
                    int position = getAdapterPosition();
                    String title = notes.get(position).get_title();
                    DatabaseHelper myDbHelper;
                    myDbHelper = new DatabaseHelper(context);

                    try {
                        myDbHelper.openDataBase();
                    } catch (
                            SQLException sqle) {
                        throw sqle;
                    }
                    myDbHelper.deleteNote(title);
                    Toast.makeText(context,
                            "Đã xóa một note", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, MyNoteActivity.class);
                    context.startActivity(intent);
                }
                return true;
            }
        };

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view,
                                        ContextMenu.ContextMenuInfo menuInfo)
        {
            menu.setHeaderTitle("Edit or Delete");

            // groupId, itemId, order, title
            MenuItem Edit = menu.add(0, MENU_ITEM_EDIT , 0, "Edit Note");
            MenuItem Delete = menu.add(0, MENU_ITEM_DELETE , 1, "Delete Note");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onDeleteMenu);
        }

    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_layout, parent, false);
        return new NoteViewHolder(view);
    }


    @Override
    public void onBindViewHolder(NoteViewHolder holder, final int position)
    {

        holder.title.setText(notes.get(position).get_title());
        holder.description.setText(notes.get(position).get_description());
    }

    @Override
    public int getItemCount()
    {
        return notes.size();
    }


}
