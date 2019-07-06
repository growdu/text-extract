package com.growdu.pdfparse;


import java.util.ArrayList;
import java.util.List;

public class Form  {
    List<Row> rows=null;

    public Form(){
        rows=new ArrayList<>();
    }

    public void add(Row r){
        rows.add(r);
    }

    public float getX(){
        float x=1000f;
        for(Row row:this.rows){
            if(row.getX()<x){
                x=row.getX();
            }
        }
        return x;
    }

    public float getY(){
        float y=0f;
        for(Row row:this.rows){
            if(row.getX()>y){
                y=row.getY();
            }
        }
        return y;
    }

    public float getWidth(){
        float width=0f;
        for(Row row:this.rows){
           if(row.getWidth()>width){
               width=row.getWidth();
           }
        }
        return width;
    }

    public float getHeight(){
        float height=0f;
        for(Row row:this.rows){
            height+=row.getHeight();
        }
        return height;
    }
}
