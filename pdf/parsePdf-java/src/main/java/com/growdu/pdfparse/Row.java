package com.growdu.pdfparse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Row {
    List<Cell> cells=null;
    boolean isSort=false;

    public Row(){
        cells=new ArrayList<>();
    }

    public void add(Cell cell){
        cells.add(cell);
    }

    public Cell getCell(int i){
        return cells.get(i);
    }

    public void sort(){
        Collections.sort(cells, new Comparator<Cell>() {
            @Override
            public int compare(Cell o1, Cell o2) {
                if(o1.x<o2.x)
                    return -1;

                if(o1.x>o2.x)
                    return 1;

                else
                    return 0;
            }
        });
        isSort=true;
    }

    public int size(){
        return this.cells.size();
    }

    public float getX(){
        if(!isSort){
            this.sort();
        }
        return cells.get(0).x;
    }

    public float getY(){
        float y=0f;
        for(Cell cell:this.cells){
            if(cell.y>y){
                y=cell.y;
            }
        }
        return y;
    }

    public float getHeight(){
        Float height = Float.MIN_VALUE;
        for(Cell cell:this.cells){
            if (height < cell.height)
                height = cell.height;
        }
        return height;
    }

    public float getWidth(){
        if(!isSort){
            this.sort();
        }
        float width=0f;
        return this.getCell(this.cells.size()-1).x
                                 +this.getCell(this.cells.size()-1).width
                                 -this.getCell(0).x;
    }

    public String getText(){
        String text="";
        for(Cell cell:cells){
            text+=cell.getText();
        }
        return text;
    }
}
