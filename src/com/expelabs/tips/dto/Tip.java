package com.expelabs.tips.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
v * Date: 14.05.13
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
public class Tip implements Serializable{

    private String text;
    private String textItalic;
    private int id;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    private String categoryName;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextItalic() {
        return textItalic;
    }

    public void setTextItalic(String textItalic) {
        this.textItalic = textItalic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
