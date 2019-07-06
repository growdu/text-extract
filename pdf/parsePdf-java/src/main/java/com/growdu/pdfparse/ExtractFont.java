package com.growdu.pdfparse;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ExtractFont {
    private static final String OUTPUT_DIR = "";

    public static void main(String[] args) throws Exception{

        String filePath = "058052.pdf";
        try (final PDDocument document = PDDocument.load(new File(filePath))){
            for (PDPage page : document.getPages()) {
                PDResources resources = page.getResources();
                processResources(resources);
            }
        } catch (IOException e){
            System.err.println("Exception while trying to read pdf document - " + e);
        }
    }

    private static void processResources(PDResources resources) throws IOException {
        if (resources == null) {
            return;
        }

        for (COSName key : resources.getFontNames()) {
            PDFont font = resources.getFont(key);
            if (font instanceof PDTrueTypeFont) {
                writeFont(font.getFontDescriptor(), font.getName());
            } else if (font instanceof PDType0Font) {
                PDCIDFont descendantFont = ((PDType0Font) font).getDescendantFont();
                if (descendantFont instanceof PDCIDFontType2) {
                    writeFont(descendantFont.getFontDescriptor(), font.getName());
                }
            }
        }

        for (COSName name : resources.getXObjectNames()) {
            PDXObject xobject = resources.getXObject(name);
            if (xobject instanceof PDFormXObject) {
                PDFormXObject xObjectForm = (PDFormXObject) xobject;
                PDResources formResources = xObjectForm.getResources();
                processResources(formResources);
            }
        }

    }

    private static void writeFont(PDFontDescriptor fd, String name) throws IOException {
        if (fd != null) {
            PDStream ff2Stream = fd.getFontFile2();
            if (ff2Stream != null) {
                String fontFile = OUTPUT_DIR + name + ".ttf";
                try (FileOutputStream fos = new FileOutputStream(new File(fontFile))) {
                    System.out.println("Writing font:" + fontFile);
                    IOUtils.copy(ff2Stream.createInputStream(), fos);
                }
            }
        }
    }
}
