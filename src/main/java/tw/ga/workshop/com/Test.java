package tw.ga.workshop.com;

import java.util.HashMap;
import java.util.Map;

import tw.ga.workshop.util.ExcelUtil;

public class Test {
	
	private static String filePath = "C:\\Users\\harvey20072000\\Desktop\\比對資料Ａ：11-12月.xlsx";
	private static String filePathB = "C:\\Users\\harvey20072000\\Desktop\\B2.xlsx";
	
    private static String xlsxFilePath = "C:\\Users\\harvey20072000\\Desktop\\the list.xlsx";

	public static void main(String[] args) throws Exception{
		Test rafe = new Test();
		System.out.println("reading excel done");
		//rafe.readExcel();
		Map<Integer, Map<Integer,Object>> excelA = ExcelUtil.readXlsx(filePath,1),
				excelB = ExcelUtil.readXlsx(filePathB,0),
				result = new HashMap<>();
		
//		for(Integer r : excelA.keySet()){
//			result.put(r, new HashMap<>(excelA.get(r)));
//		}
		for(Integer rA : excelA.keySet()){
			System.out.println("row"+rA+" "+excelA.get(rA).get(0));
			for(int i=0;i<9;i++){
				if(excelA.get(rA).get(i) == null)
					excelA.get(rA).put(i, "");
			}
			for(Integer rB : excelB.keySet()){
				for(int i=0;i<6;i++){
					if(excelB.get(rB).get(i) == null)
						excelB.get(rB).put(i, "");
				}
				if(safeGetString(excelA.get(rA).get(0)).equals(safeGetString(excelB.get(rB).get(2)))
						|| safeGetString(excelA.get(rA).get(0)).equals(safeGetString(excelB.get(rB).get(1)))){
					excelA.get(rA).put(7, excelB.get(rB).get(4));
					excelA.get(rA).put(8, excelB.get(rB).get(5));
					System.out.println("excelA row:"+rA+" is match for excelB row:"+rB);
				}
			}
			if(rA%5 == 0)
				System.out.println("progessing excelA for row : "+rA);
		}
		ExcelUtil.writeXlsx(xlsxFilePath, excelA);
		System.out.println("done");
//		System.out.println(checkEmail("harvey20072000@hotmail.com"));
	}

	private static String safeGetString(Object input){
		try {
			return input.toString();
		} catch (Exception e) {
			return "";
		}
	}
	
}
