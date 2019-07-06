package com.growdu.pdfparse;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDTextState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.awt.geom.Point2D;
import java.io.IOException;

public class ParseForm extends PDFGraphicsStreamEngine {


    List<Cell> cells=null;
    Cell c=new Cell();
    public final float mistake=5f;//坐标之间允许的最大误差，主要用于判断（cell、row、form）
    public final float maxHeight=852f;//页面的最大高度
    boolean isLine=false;

    protected ParseForm(PDPage page) {
        super(page);
        cells=new ArrayList<>();
    }


    public static void main(String []args) throws IOException {
        File file = new File("C:\\Users\\duanys\\Desktop\\股东大会出席\\",
                "有表格线无边框.PDF");

        try (PDDocument doc = PDDocument.load(file))
        {
            PDPage p=doc.getPage(0);
            ParseForm parseForm = new ParseForm(p);
            parseForm.run();
        }
    }

    public void run() throws IOException
    {
        processPage(getPage());

        for (PDAnnotation annotation : getPage().getAnnotations())
        {
            showAnnotation(annotation);
        }
        GetCell();
        List<Row> rows=GetRow();
        GetForm(rows);
    }

    /**
     * 获取可能为单元格的矩形
     * */
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException
    {
        if(c.height>mistake&&c.width>mistake||c.getText().length()!=0) {//宽度和高度都大于1则识别为cell
            cells.add(c);
            c = new Cell();
        }
        c.x=(float)Math.min(Math.min(p0.getX(),p1.getX()),Math.min(p2.getX(),p3.getX()));
        c.y=/*maxHeight-*/(float)Math.min(Math.max(p0.getY(),p1.getY()),Math.max(p2.getY(),p3.getY()));
        /*version1.0-正式识别单元格*/
        if(Math.abs(p0.getY()-p1.getY())<mistake&&Math.abs(p0.getX()-p3.getX())<mistake){
            //p0-->p1连成横线,p0-->p3连成竖线
            c.width=(float)Math.abs(p0.getX()-p1.getX());
            c.height=(float)Math.abs(p1.getY()-p3.getY());
        }
        else if(Math.abs(p0.getX()-p1.getX())<mistake&&Math.abs(p0.getY()-p3.getY())<mistake){
            //p0-->p3连成横线,且p0-->p1连成竖线
            c.width=(float)Math.abs(p0.getX()-p3.getX());
            c.height=(float)Math.abs(p1.getY()-p0.getY());
        }
        else if(Math.abs(p0.getX()-p1.getX())<mistake&&Math.abs(p0.getY()-p3.getY())<mistake){
            //p0-->p2连成横线,且p1-->p3连成竖线
            c.width=(float)Math.abs(p0.getX()-p2.getX());
            c.height=(float)Math.abs(p1.getY()-p3.getY());
        }
        /*if(c.height>mistake&&c.width>mistake) {//宽度和高度都大于1则识别为cell
            cells.add(c);
            c=new Cell();
        }*/
    }

    /**
     * 去除重复节点
     * */
    public void  GetCell() {
        List<Cell> temp=new ArrayList<>();
        Cell cell=cells.get(0);
        for(int i=1;i<cells.size();i++){//将节点进行合并，坐标
            if(Math.abs(cell.getX()-cells.get(i).getX())<mistake){
                cell.merge(cells.get(i));
            }else{
                temp.add(cell);
                cell=cells.get(i);
            }
        }
        temp.add(cell);
        cells=temp;


        for (int i = 1; i < cells.size(); i++) {
            if (/*Math.abs(cells.get(i).height - cells.get(i - 1).height) < mistake &&
                    Math.abs(cells.get(i).width - cells.get(i - 1).width) < mistake &&*/
                    Math.abs(cells.get(i).x- cells.get(i - 1).x) < mistake &&
                    Math.abs(cells.get(i).y - cells.get(i - 1).y) < mistake) {
                cells.remove(i);
            }
        }
        if (cells.size() > 2 &&//判别最后两个节点
               /* Math.abs(cells.get(cells.size() - 1).height - cells.get(cells.size() - 2).height) < mistake &&
                Math.abs(cells.get(cells.size() - 1).width - cells.get(cells.size() - 2).width) < mistake &&*/
                Math.abs(cells.get(cells.size() - 1).x - cells.get(cells.size() - 2).x) < mistake &&
                Math.abs(cells.get(cells.size() - 1).y - cells.get(cells.size() - 2).y) < mistake) {
            cells.remove(cells.size() - 1);
        }
        Collections.sort(cells, new Comparator<Cell>() {
            @Override
            public int compare(Cell o1, Cell o2) {
                if(o1.y>o2.y)
                    return 1;
                if(o1.y<o2.y)
                    return -1;
                else
                    return 0;
            }
        });
    }

    private List<Row> GetRow(){
        List<Row> rows=new ArrayList<Row>();
        Row r=new Row();
            //将cell识别成row
        for(Cell cell:cells) {
            if(r.cells==null||r.cells.size()==0) {//添加行的第一个单元格
                r.add(cell);
                continue;
            }
            float y=r.getY();
            float height=r.getHeight();
            //属于行范围内的单元格,允许有一个上下波动的误差
            if(cell.y+cell.height<=r.getY()+r.getHeight()+mistake
            && cell.y+cell.height>=r.getY()-mistake
            && cell.y>=r.getY()-mistake
            && cell.y<=r.getY()+r.getHeight()+mistake
            ){
                r.add(cell);
            }
            else {//当下一个单元格不属于该行时，添加该行，并重置r
                rows.add(r);
                r=new Row();
                r.add(cell);
            }
        }
        rows.add(r);
        return rows;
    }

    private List<Form> GetForm(@NotNull List<Row> rows){
        List<Form> forms=new ArrayList<Form>();
        Form form=new Form();
        for(Row row:rows) {
            double top=row.getY();
            double bottom=top+row.getHeight();
            if(form.rows==null||form.rows.size()==0) {
                form.add(row);
                continue;
            }
            double ry=row.getY()+row.getHeight();
            double fy=form.getY()+form.getHeight();
            double y=row.getY();
            double y2=form.getY();
            if(Math.abs(row.getY()-(form.getY()+form.getHeight()))<=mistake
                    &&Math.abs(row.getWidth()-form.getWidth())<mistake) {
                form.add(row);
            }else {
                forms.add(form);
                form=new Form();
                form.add(row);
            }
        }
        if(form!=null&&form.rows.size()!=0){
            forms.add(form);
        }
        return forms;
    }

    @Override
    public void drawImage(PDImage pdImage) throws IOException
    {

    }

    @Override
    public void clip(int windingRule) throws IOException
    {
    }

    @Override
    public void moveTo(float x, float y) throws IOException
    {

    }

    @Override
    public void lineTo(float x, float y) throws IOException
    {

    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException
    {

    }

    @Override
    public Point2D getCurrentPoint() throws IOException
    {
        // if you want to build paths, you'll need to keep track of this like PageDrawer does
        return new Point2D.Float(0, 0);
    }

    @Override
    public void closePath() throws IOException
    {

    }

    @Override
    public void endPath() throws IOException
    {
        if(c.height>mistake&&c.width>mistake||c.getText().length()!=0) {//宽度和高度都大于1则识别为cell
            cells.add(c);
            c = new Cell();
        }
    }

    @Override
    public void strokePath() throws IOException
    {

    }

    @Override
    public void fillPath(int windingRule) throws IOException
    {

    }

    @Override
    public void fillAndStrokePath(int windingRule) throws IOException
    {

    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException
    {

    }

    /**
     * Overridden from PDFStreamEngine.
     */
    @Override
    public void showTextString(byte[] string) throws IOException
    {
        /*System.out.print("showTextString \"");
        super.showTextString(string);
        System.out.println("\"");*/
    }

    /**
     * Overridden from PDFStreamEngine.
     */
    @Override
    public void showTextStrings(COSArray array) throws IOException
    {
        //System.out.print("showTextStrings \"");
        super.showTextStrings(array);
        //System.out.println("\"");
    }

    /**
     * Overridden from PDFStreamEngine.
     */
    @Override
    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                             Vector displacement) throws IOException
    {
        System.out.print(unicode);
        c.setText(unicode);
        //super.showGlyph(textRenderingMatrix, font, code, unicode, displacement);
    }
}
