package com.vijay.mf.nav;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class App {
    
    public static void main(String[] args) throws IOException {
        boolean isTesting = false;
        String[] schemeCodes = args;
        String fileName = System.getProperty("fileName");
        String amfiNavURL = System.getProperty("navUrl");
        
        System.out.println("fileName: " + fileName);
        
        if (isTesting) {
            Stream<String> stringStream = Stream.of("120564", "120847", "119060");
            schemeCodes = stringStream.toArray(size -> new String[size]);
            amfiNavURL = "https://www.amfiindia.com/spages/NAVAll.txt";
        }
        List<MutualFund> mfList = getMutualFundList(amfiNavURL, schemeCodes);
        
        createBackup(fileName);
        
        try (InputStream filein = new FileInputStream(fileName)) {
            
            XSSFWorkbook workbook = new XSSFWorkbook(filein);
            
            XSSFSheet sheet = workbook.getSheet("NAV");
            
            XSSFCellStyle style = null;
            int rowNum = 1;
            
            for (MutualFund mFund : mfList) {
                XSSFRow row = sheet.getRow(rowNum);
                
                XSSFCell schemeCodeCell = row.getCell(0);
                style = getCellStyle(workbook);
                schemeCodeCell.setCellStyle(style);
                schemeCodeCell.setCellValue(mFund.getSchemeCode());
                
                XSSFCell schemeNameCell = row.getCell(1);
                style = getCellStyle(workbook);
                schemeNameCell.setCellStyle(style);
                schemeNameCell.setCellValue(mFund.getSchemeName());
                
                XSSFCell navCell = row.getCell(2);
                style = getCellStyle(workbook);
                style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
                navCell.setCellStyle(style);
                navCell.setCellValue(Double.parseDouble(mFund.getNav()));
                
                XSSFCell dateCell = row.getCell(3);
                style = getCellStyle(workbook);
                dateCell.setCellStyle(style);
                dateCell.setCellValue(mFund.getDate());
                
                rowNum++;
            }
            
            try (OutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }
            workbook.close();
            System.out.println("\n");
            System.out.println("Completed Loading the data");
        }
    }
    
    private static XSSFCellStyle getCellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private static List<MutualFund> getMutualFundList(String amfiNavURL, String[] schemeCodes) {
        Map<String, MutualFund> mfMap = new HashMap<>(schemeCodes.length);
        List<MutualFund> mfList = new ArrayList<>(schemeCodes.length);
        
        try {
            URL website = new URL(amfiNavURL);
            System.out.println("Connecting to URL: " + amfiNavURL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(website.openStream()));
            String line = null;
            
            while ((line = reader.readLine()) != null) {
                
                line = line.trim();
                String[] tokens = line.split(";");
                //System.out.println(line + ", tokenLength: " + tokens.length);
                MutualFund mFund = null;
                if (tokens.length == 6 && Arrays.asList(schemeCodes).contains(tokens[0])) {
                    mFund = new MutualFund();
                    mFund.setSchemeCode(Integer.parseInt(tokens[0]));
                    mFund.setSchemeName(tokens[3]);
                    mFund.setNav(tokens[4]);
                    mFund.setDate(tokens[5]);
                    mfMap.put(tokens[0], mFund);
                    
                    System.out.println(mFund);
                }
            }
            
            for (String schemeCode : schemeCodes) {
                mfList.add(mfMap.get(schemeCode));
                System.out.println(mfMap.get(schemeCode));
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mfList;
    }
    
    private static void createBackup(String fileName) {
        File navFile = new File(fileName);
        OutputStream outStream = null;
        InputStream navFis = null;
        try {
            File backupFile = new File(getFileName(navFile) + "-backup" + getFileExtension(navFile));
            
            if (backupFile.exists()) {
                backupFile.delete();
            }
            navFis = new FileInputStream(navFile);
            outStream = new FileOutputStream(backupFile);
            
            byte[] buffer = new byte['Ѐ'];
            
            int length;
            
            while ((length = navFis.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
            
            navFis.close();
            outStream.close();
            
            System.out.println("File backup - complete!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf);
    }
    
    private static String getFileName(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(0, lastIndexOf);
    }
}

