package com.growdu.pdfparse;

import org.apache.fontbox.encoding.Encoding;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreatePdf {

    public class Line{
        float x;
        float y;
        String text;
    }

    public static void main(String []args)throws IOException,DocumentException{
        String path="单元格跨列56.xml";
        readXml(path);
        //writePdf("C:\\Users\\duanys\\Desktop\\officework\\test\\1\\test.pdf","");
    }

    /**
     * @param path  xml file path
     **/
    public static void readXml(String path)throws DocumentException,IOException{
        File file=new File(path);
        SAXReader reader=new SAXReader();
        Document doc=reader.read(file);
        Element root=doc.getRootElement();
        System.out.println("根节点："+root.getName()+"\n");
        List<Element> pages=root.elements();
        String pdfName=path.substring(0,path.lastIndexOf('.'))+".pdf";
        PDDocument pdf=new PDDocument();
        PDType0Font font = PDType0Font.load(pdf, new File( "C:\\Windows\\Fonts\\simkai.ttf"));
        for(Element page:pages){
            PDPage pdfPage=new PDPage();
            System.out.println("一级节点："+page.getName()+"\n内容："+page.attributeValue("num")+"\n");
            List<Element> Ps=page.elements();
            for(Element P:Ps){
                CreatePdf createPdf=new CreatePdf();
                List<Line> lines=createPdf.GetPosition(P);
                PDPageContentStream contentStream=new PDPageContentStream(pdf,pdfPage);
                for(Line line:lines){
                    contentStream.beginText();
                    contentStream.newLineAtOffset(line.x,line.y);
                    contentStream.setFont(font, 12);
                    contentStream.showText(line.text);
                    contentStream.endText();
                }
                contentStream.close();
//                System.out.println("二级节点："+P.getName()+"\n内容："+P.getText()+"\n");
//                System.out.println(P.attributeValue("x")+" |"+P.attributeValue("y")
//                        +"|"+P.attributeValue("height")+"\n");
            }
            pdf.addPage(pdfPage);
        }
        pdf.save(path);
        pdf.close();
    }

    private  List<Line> GetPosition(Element paragraph){
        List<Line> lines=new ArrayList<>();
        float y=Float.parseFloat(paragraph.attributeValue("x"));
        String []coors=paragraph.attributeValue("coors").split("[|]");
        String []texts=paragraph.getText().split("[|]");
        for(int i=0;i<coors.length;i++){
            Line line=new Line();
            line.y=y;
            line.x=Float.parseFloat(coors[i]);
            line.text=texts[i];
            lines.add(line);
        }
       return lines;
    }

    /**
     * @param path pdf store path
     * @param content pdf content
     */
    public static void writePdf(String path,String content)throws IOException{
        PDDocument pdf=new PDDocument();
        PDPage page=new PDPage();
        PDPageContentStream contentStream=new PDPageContentStream(pdf,page);
        PDType0Font font1BoldRaleway =
                PDType0Font.load(pdf, new File( "C:\\Windows\\Fonts\\simkai.ttf"));
        contentStream.beginText();
        contentStream.newLineAtOffset(25,700);
        contentStream.setFont(font1BoldRaleway, 12);
        contentStream.showText("第一，第一.");
        contentStream.endText();
        contentStream.beginText();
        contentStream.newLineAtOffset(120,700);
        contentStream.setFont(font1BoldRaleway, 12);
        contentStream.showText("cancel,cancel,cancel.");
        contentStream.endText();
        contentStream.close();
        pdf.addPage(page);
        pdf.save(path);
        pdf.close();
    }


}
