package com.myha.toeicwords.beans;

public class Note
{
    private String title;
    private String description;

    public Note(String title, String desc)
    {
        this.title = title;
        this.description = desc;
    }

    public String get_title()
    {
        return title;
    }

    public String get_description()
    {
        return description;
    }

}
