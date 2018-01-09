package com.example.gecko.ereader;

/**
 * Created by GecKo on 2017/12/6.
 */

public class number {
    String name;
    String name1;
    public String getName(){return  name;}
    public void setName(String name){this.name=name;}
    public String getName1(){return name1;}
    public void setName1(String name1){this.name1=name1;}
    public String toString(){return name;}
    public number(String name, String name1){
        super();
        this.name=name;
        this.name1=name1;
    }
}
