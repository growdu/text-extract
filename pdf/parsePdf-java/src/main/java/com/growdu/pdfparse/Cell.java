package com.growdu.pdfparse;

import java.awt.geom.Rectangle2D;

public class Cell extends Rectangle2D.Float {
    private String text=" ";

    public Cell(){

    }

    public Cell(float x,float y,float height,float width,String text){
        this.x=x;
        this.y=y;
        this.height=height;
        this.width=width;
        this.text=text;
    }

    public String getText(){
        return this.text;
    }

    public void setText(String text){
        this.text+=text;
    }

    /*将两个cell合并为一个
    *
    * */
    public void merge(Cell cell){
        if(cell==null)
            return;

        this.x=Math.min(this.x,cell.x);
        this.y=Math.max(this.y,cell.y);
        this.height=Math.max(this.height,cell.height);
        this.width+=cell.width;
        this.text+=cell.text;
        if(this.x<0){
            this.x=0;
        }
        if(this.y<0){
            this.y=0;
        }
    }
}
