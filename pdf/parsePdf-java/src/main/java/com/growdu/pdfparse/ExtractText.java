package com.growdu.pdfparse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class ExtractText {
    public static void main(String[] args)throws IOException{
        String path="申万宏源.pdf";
        if(pdfSaveAs(path)){
            System.out.println("save as successfully.");
        }
        File pdf=new File("111.pdf");
        //String path=System.getProperty("user.dir");
        PDDocument document=PDDocument.load(pdf);
        int pages=document.getNumberOfPages();
        PDFTextStripper pdfTextStripper=new PDFTextStripper();
        pdfTextStripper.setSortByPosition(true);
        pdfTextStripper.setStartPage(1);
        pdfTextStripper.setEndPage(pages);
        String content = pdfTextStripper.getText(document);
        System.out.println(content);
        if(ChineseErroCode.isMessyCode(content)){
            Runtime rt=Runtime.getRuntime();
            //String path=System.getProperty("user.dir");
            String file[]=new String[2];
            file[1]="森萱医药乱码.pdf";
            file[0]="ConvertPdf.exe";
            try{
                rt.exec(file);
            }catch(Exception ex){
                //System.out.println(ex.getMessage()+ex.getStackTrace());
                ex.printStackTrace();
            }
        }
    }
    public static boolean pdfSaveAs(String path) throws IOException{
        File file=new File(path);
        PDDocument source=PDDocument.load(file);
        PDDocument target=new PDDocument();
        for(int i=0;i<source.getNumberOfPages();i++){
            PDPage page=source.getPage(i);
            target.addPage(page);
        }
        String targetPath=path.substring(0,path.lastIndexOf("."))+"副本.pdf";
        target.save(targetPath);
        target.close();
        try{
            File pdf=new File(targetPath);
        }
        catch(Exception e){

        }
        return true;
    }
}
