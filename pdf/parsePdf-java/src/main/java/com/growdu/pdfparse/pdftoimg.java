package com.growdu.pdfparse;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class pdftoimg {
    public static void main(String[] args) throws IOException {
        String path="I:\\pdf\\有表格线空格问题\\";
        File file=new File(path+"2.pdf");
        PDDocument pdf=PDDocument.load(file);
        PDDocumentCatalog cata=pdf.getDocumentCatalog();
        PDFTextStripper stripper=new PDFTextStripper();
        String content=stripper.getText(pdf);
        System.out.println(content);
        cata.getPages().get(0).getResources();

        PDFRenderer render=new PDFRenderer(pdf);
        int count=pdf.getNumberOfPages();
        for (int i = 0; i <count; i++) {
            PDPage page = pdf.getPage(i);
            page.getContents();
            page.getCOSObject();
            PDResources res=page.getResources();
            Iterable<COSName> xos=res.getXObjectNames();
            if(xos!=null){
                for (COSName key:xos
                     ) {
                    if(res.isImageXObject(key)){
                        System.out.println("has image");
                        break;
                    }
                }
            }

            COSDictionary resdic=res.getCOSObject();
            COSBase jpg=resdic.getItem(COSName.IMAGE);
            res.getResourceCache();
            if(null!=page){
                BufferedImage image = render.renderImageWithDPI(i, 96);
                File im=new File(path+String.valueOf(i)+".jpg");
                ImageIO.write(image,"jpg",im);
            }
        }
    }
    public static boolean convert(String path) throws  IOException{
        File file=new File(path);
        if(!file.isFile()||GetExtension(file)!=".pdf")
            return false;

        PDDocument pdf=PDDocument.load(file);
        PDFRenderer render=new PDFRenderer(pdf);
        int count=pdf.getNumberOfPages();

        for(int i=0;i<count;i++){
            PDPage page=pdf.getPage(i);
            PDResources res=page.getResources();
            boolean hasImage=false;
            Iterable<COSName> xos=res.getXObjectNames();
            if(xos!=null){
                for (COSName key:xos
                ) {
                    if(res.isImageXObject(key)){
                        hasImage=true;
                        break;
                    }
                }
            }
            if(hasImage){
                BufferedImage image = render.renderImageWithDPI(i, 96);
                File im=new File(path+String.valueOf(i)+".jpg");
                ImageIO.write(image,"jpg",im);
            }
        }
        return true;
    }

    public static String GetExtension(File file){
        String name=file.getName();
        return name.substring(name.lastIndexOf('.')).toLowerCase();
    }
}
