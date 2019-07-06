package com.growdu.pdfparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChineseErroCode {

    public static void main(String []args) throws IOException{
        File file=new File("样本.xml");
        BufferedReader br= new BufferedReader(new FileReader(file));
        StringBuilder result = new StringBuilder();
        String s=null;
        float erroCode=0;
        float total=0;
        while((s = br.readLine())!=null){//使用readLine方法，一次读一行
            if(isMessyCode(s)){
                erroCode++;
            }
            total++;
        }
        if(total/erroCode>0.1){
            System.out.println("乱码。");
        }
//        if(isMessyCode(result.toString())){
//            System.out.println("乱码。");
//        }
    }

    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = 0 ;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (isChinese(c)) {
                    count = count + 1;
                }
                chLength++;
            }
        }
        float result = count / strName.length() ;
        if (result < 0.2) {
            return true;
        } else {
            return false;
        }
    }

}
