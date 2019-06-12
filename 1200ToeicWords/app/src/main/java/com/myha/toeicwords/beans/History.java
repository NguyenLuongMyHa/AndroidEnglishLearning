package com.myha.toeicwords.beans;


public class History {

    private String en_word;
    private String vi_def;

    public History(String en_word,String vi_def)
    {
        this.en_word=en_word;
        this.vi_def=vi_def;
    }

    public String get_en_word()
    {
        return en_word;
    }

    public String get_def()
    {
        return vi_def;
    }

}
