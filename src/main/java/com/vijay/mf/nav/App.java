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

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTRowImpl;

public class App {

	public static void main(String[] args) throws IOException {
		String[] schemeCodes = args;
		String fileName = System.getProperty("fileName");
		System.out.println("fileName: " + fileName);
		createBackup(fileName);

		try (InputStream filein = new FileInputStream(fileName)) {

			XSSFWorkbook workbook = new XSSFWorkbook(filein);

			XSSFSheet sheet = workbook.getSheet("NAV");

			List<MutualFund> mfList = getMutualFundList(schemeCodes);
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

	public static void main1(String[] args) throws IOException {
		try (XSSFWorkbook wb = new XSSFWorkbook()) {

			XSSFSheet sheet = wb.createSheet();
			XSSFRow row = sheet.createRow(2);
			row.setHeightInPoints(30);
			for (int i = 0; i < 8; i++) {
				// column width is set in units of 1/256th of a character width
				sheet.setColumnWidth(i, 256 * 15);
			}

			createCell(wb, row, 0, HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
			createCell(wb, row, 1, HorizontalAlignment.CENTER_SELECTION, VerticalAlignment.BOTTOM);
			createCell(wb, row, 2, HorizontalAlignment.FILL, VerticalAlignment.CENTER);
			createCell(wb, row, 3, HorizontalAlignment.GENERAL, VerticalAlignment.CENTER);
			createCell(wb, row, 4, HorizontalAlignment.JUSTIFY, VerticalAlignment.JUSTIFY);
			createCell(wb, row, 5, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
			createCell(wb, row, 6, HorizontalAlignment.RIGHT, VerticalAlignment.TOP);

			// center text over B4, C4, D4
			row = sheet.createRow(3);
			centerAcrossSelection(wb, row, 1, 3, VerticalAlignment.CENTER);

			// Write the output to a file
			try (OutputStream fileOut = new FileOutputStream("xssf-align.xlsx")) {
				wb.write(fileOut);
			}
		}
	}

	/**
	 * Center a text over multiple columns using ALIGN_CENTER_SELECTION
	 *
	 * @param wb
	 *            the workbook
	 * @param row
	 *            the row to create the cell in
	 * @param start_column
	 *            the column number to create the cell in and where the
	 *            selection starts
	 * @param end_column
	 *            the column number where the selection ends
	 * @param valign
	 *            the horizontal alignment for the cell.
	 */
	private static void centerAcrossSelection(XSSFWorkbook wb, XSSFRow row, int start_column, int end_column,
			VerticalAlignment valign) {
		CreationHelper ch = wb.getCreationHelper();

		// Create cell style with ALIGN_CENTER_SELECTION
		XSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		cellStyle.setVerticalAlignment(valign);

		// Create cells over the selected area
		for (int i = start_column; i <= end_column; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(cellStyle);
		}

		// Set value to the first cell
		XSSFCell cell = row.getCell(start_column);
		cell.setCellValue(ch.createRichTextString("Align It"));

		// Make the selection
		CTRowImpl ctRow = (CTRowImpl) row.getCTRow();

		// Add object with format start_coll:end_coll. For example 1:3 will span
		// from
		// cell 1 to cell 3, where the column index starts with 0
		//
		// You can add multiple spans for one row
		Object span = start_column + ":" + end_column;

		List<Object> spanList = new ArrayList<>();
		spanList.add(span);

		// add spns to the row
		ctRow.setSpans(spanList);
	}

	private static void createCell(XSSFWorkbook wb, XSSFRow row, int column, HorizontalAlignment halign,
			VerticalAlignment valign) {
		CreationHelper ch = wb.getCreationHelper();
		XSSFCell cell = row.createCell(column);
		cell.setCellValue(ch.createRichTextString("Align It"));
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(halign);
		cellStyle.setVerticalAlignment(valign);
		cell.setCellStyle(cellStyle);
	}

	private static List<MutualFund> getMutualFundList(String[] schemeCodes) {
		Map<String, MutualFund> mfMap = new HashMap<>(schemeCodes.length);
		List<MutualFund> mfList = new ArrayList<>(schemeCodes.length);

		try {
			URL website = new URL("https://www.amfiindia.com/spages/NAVAll.txt");
			System.out.println("Connecting to URL");
			BufferedReader reader = new BufferedReader(new InputStreamReader(website.openStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				String[] tokens = line.split(";");

				MutualFund mFund = null;
				if (tokens.length == 8 && Arrays.asList(schemeCodes).contains(tokens[0])) {
					mFund = new MutualFund();
					mFund.setSchemeCode(Integer.parseInt(tokens[0]));
					mFund.setSchemeName(tokens[3]);
					mFund.setNav(tokens[4]);
					mFund.setDate(tokens[7]);
					System.out.println(mFund);
					mfMap.put(tokens[0], mFund);
				}
			}
			
			for (String schemeCode : schemeCodes) {
				mfList.add(mfMap.get(schemeCode));
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
