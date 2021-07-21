package com.gmail.ellazeoli97.photoshare;

import com.google.firebase.database.Exclude;

public class Image
{
    private String url;
    private String name;
    private String mKey;

    public Image(String name, String url )
    {
        if(name.trim().equals("")) name = "Null";

        this.name = name;
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }

    public String getName()
    {
        return name;
    }

    public void setUrl(String u)
    {
        this.url = u;
    }

    public void setName(String n)
    {
        this.name = n;
    }

    @Exclude
    public String getKey()
    {
        return mKey;
    }

    @Exclude
    public void setKey(String n)
    {
        this.mKey = n;
    }
}
