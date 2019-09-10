package tw.ga.workshop.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.EmptyFileException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelUtil {

    private String filePath = "C:\\Users\\harvey20072000\\Desktop\\test.xls";
    private String xlsxFilePath = "C:\\Users\\harvey20072000\\Desktop\\the list.xlsx";
    private String charset = "utf-8";
    
//	public static void readExcel(String filePath, Object sheetParam) throws IOException {
//		FileInputStream fis = new FileInputStream(filePath);
//		POIFSFileSystem fs = new POIFSFileSystem(fis);
//		HSSFWorkbook wb = new HSSFWorkbook(fs);
//		HSSFSheet sheet = wb.getSheetAt(0); // 取得Excel第一個sheet(從0開始)
//		HSSFCell cell;
//
//		// getPhysicalNumberOfRows這個比較好 //getLastRowNum:這個好像會差1筆
//		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) { // 由於第 0 Row為 title,故 i 從 1開始
//			HSSFRow row = sheet.getRow(i); // 取得第 i Row
//			for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
//				cell = row.getCell(j);
//				strData[j] = cell.toString();
//			}
//			System.out.println("Name = " + strData[0] + ", Passwd = " + strData[1] + ", Email = " + strData[2]);
//		}
//
//		fis.close();
//	}

    public static Map<Integer, Map<Integer,Object>> readXlsx(String filePath) throws Exception {
		return readXlsx(filePath, 0);
	}
    
	public static Map<Integer, Map<Integer,Object>> readXlsx(String filePath, Object sheetParam) throws Exception {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet;
			if(sheetParam instanceof String){
				sheet = wb.getSheet((String)sheetParam);
			}else if (sheetParam instanceof Integer) {
				sheet = wb.getSheetAt((Integer)sheetParam);
			}else {
				throw new Exception("unknown sheetParam　: "+sheetParam.toString());
			}
			
			XSSFCell cell;
			Map<Integer, Map<Integer,Object>> result = new TreeMap<>();
			// getPhysicalNumberOfRows這個比較好 //getLastRowNum:這個好像會差1筆
			for (int i = 0; i < sheet.getLastRowNum()/*sheet.getPhysicalNumberOfRows()*/; i++) { // 由於第 0 Row為 title,故 i 從 1開始
				XSSFRow row = sheet.getRow(i); // 取得第 i Row
				if(row != null){
					result.put(i, new TreeMap<>());
					for (int j = 0; j < row.getLastCellNum()/*row.getPhysicalNumberOfCells()*/; j++) {
						cell = row.getCell(j);
						result.get(i).put(j, safeGetString(cell));
					}
				}
			}
			return result;
		} catch (Exception e) {
//			log.error("readXlsx fail, exception => ");
			throw e;
		} finally {
			fis.close();
		}
	}
	
	private static String safeGetString(Object input){
		if(input != null)
			return input.toString();
		return "";
	}
	
	private static File createFileIfNotExist(String filePath) throws IOException{
		File file = new File(filePath);
		if(!file.exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return file;
	}
	
	public static boolean writeXlsx(String filePath, Map<Integer, Map<Integer,Object>> data) throws Exception{
		return writeXlsx(filePath, null, data, 0);
	}
	
	public static boolean writeXlsx(String filePath, List<String> columnNames, Map<Integer, Map<Integer,Object>> data, Object sheetParam) throws Exception{
		XSSFWorkbook wb = new XSSFWorkbook();
		writeXlsx(wb, columnNames, data, sheetParam);
		return outputXlsx(filePath, wb);
	}
	
	public static boolean loadAndWriteXlsx(String filePath, Map<Integer, Map<Integer,Object>> data) throws Exception{
		return loadAndWriteXlsx(filePath, null, data, 0);
	}
	
	public static boolean loadAndWriteXlsx(String filePath, List<String> columnNames, Map<Integer, Map<Integer,Object>> data, Object sheetParam) throws Exception{
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(createFileIfNotExist(filePath));
			XSSFWorkbook wb;
			try {
				wb = new XSSFWorkbook(fis);
			} catch (EmptyFileException e) {
				wb = new XSSFWorkbook();
			}
			writeXlsx(wb, columnNames, data, sheetParam);
			return outputXlsx(filePath, wb);
		} catch (Exception e) {
			throw e;
		}finally {
			if(fis != null)
				fis.close();
		}
		
	}
	
	private static void writeXlsx(XSSFWorkbook wb, List<String> columnNames, Map<Integer, Map<Integer,Object>> data, Object sheetParam) throws Exception{

		XSSFSheet sheet;
		XSSFCell cell;
			
		XSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setWrapText(true);
		cellStyle.setShrinkToFit(true);
		
		try {
			if(sheetParam instanceof String){
				if((sheet = wb.getSheet((String)sheetParam)) == null){
					sheet = wb.createSheet((String)sheetParam);
				}
			}else if (sheetParam instanceof Integer) {
				if((sheet = wb.getSheetAt((Integer)sheetParam)) == null)
					sheet = wb.createSheet();
			}else {
				throw new IOException("unknown sheetParam　: "+sheetParam.toString());
			}
		} catch (Exception e) {
			System.out.println("recognizing sheetParam fail, exception => "+e.toString());
			sheet = wb.createSheet();
		}
		
		synchronized (data) {
			for (int r = 0; r < data.size(); r++) {
				XSSFRow row;
				if((row = sheet.getRow(r)) == null){
					row = sheet.createRow(r);
				}
				if (columnNames != null && columnNames.size() > 0 && r == 0) {
					for (int c = 0; c < columnNames.size(); c++) {
						if((cell = row.getCell(c)) == null)
							cell = row.createCell(c);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(columnNames.get(c));
					}
				} else {
					for (int c = 0; c < data.get(r).size(); c++) {
						if((cell = row.getCell(c)) == null)
							cell = row.createCell(c);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(Util.safeGetString(data.get(r).get(c)));
					}
				}
			}
		}

		//Read more: http://www.java67.com/2014/09/how-to-read-write-xlsx-file-in-java-apache-poi-example.html#ixzz53lYaWjRg
	}
	
	private static boolean outputXlsx(String filePath,XSSFWorkbook wb) throws Exception{
		File file = null;
		OutputStream os = null;
		try {
			file = createFileIfNotExist(filePath);
			os = null;
		
			System.out.println("outputXlsx : " + file.getAbsolutePath());
			// create a new OutputStreamWriter
			os = new FileOutputStream(filePath);
			
			wb.write(os);
//			String outputString = gson.toJson(new LinkedList<>(map.values()));

			// flush the stream
			System.out.println("Writing on XLSX file Finished ...");
			return true;
		} catch (Exception ex) {
//			log.error("outputLine fail, exception => {}", ex.toString());
			throw ex;
		}finally {
			if(os != null)
				os.close();
		}
	}
	
	public static void main(String[] args) throws Exception {
	}
	
}
