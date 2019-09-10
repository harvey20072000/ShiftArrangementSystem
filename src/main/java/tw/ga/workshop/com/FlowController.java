package tw.ga.workshop.com;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import tw.ga.workshop.dao.TargetDAO;
import tw.ga.workshop.dao.TargetDAOImpl;
import tw.ga.workshop.logic.ArrangementProcesser;
import tw.ga.workshop.logic.ArrangementProcesserImpl;
import tw.ga.workshop.logic.CustomProcesser;
import tw.ga.workshop.logic.NurseProcesserImpl;
import tw.ga.workshop.model.ArrangementPerDay;
import tw.ga.workshop.model.Attender;
import tw.ga.workshop.model.AttenderStatus;
import tw.ga.workshop.util.Const;

@Slf4j
public class FlowController {
	
	private static final FlowController controllerInstance = new FlowController();
	
	public static Map<String, Attender> attendersMap;
	// load excel to init attendersMap
	public static Map<Long, ArrangementPerDay> arrangements;
	
	public static Properties conditionsAttrs; 
	
	private TargetDAO dao = new TargetDAOImpl();
	
	private ArrangementProcesser arrangementProcesser = new ArrangementProcesserImpl();
	
	private CustomProcesser customProcesser;
	
	private final String attrsPath = ClassLoader.getSystemResource("").getPath()+"/attrs.properties";
	/*System.getProperty("user.dir")+"\\src\\attrs.properties"*/
	
	/* 以下是props */
	public String inputFilePath/* = ClassLoader.getSystemResource("").getPath()+"/排班人員.xlsx"*/;
	//"C:\\Users\\harvey20072000\\Desktop\\Attenders.xlsx"
	
	public String outputFilePath/* = ClassLoader.getSystemResource("").getPath()+"/./{MONTH} 排班表.xlsx"*/;
	//"C:\\Users\\harvey20072000\\Desktop\\test arrangements.xlsx"
	
	public String outputInformFilePath/* = ClassLoader.getSystemResource("").getPath()+"/./額外資訊.txt"*/;
	/*props end*/
	
	private static Date targetMonth;
	public static int workingDays = 0;
	public static int avgWorkerPerDay = 0;
	
	public static FlowController getInstance(){
		return controllerInstance;
	}
	
	public static Date getTargetMonth(){
		return new Date(targetMonth.getTime());
	}
	
	public static void setTargetMonth(Date date){
		targetMonth = new Date(date.getTime());
		controllerInstance.initAttendersMap();
	}

	public FlowController(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		targetMonth = new Date();
		targetMonth.setMonth(targetMonth.getMonth()+1);
		try {
			targetMonth = sdf.parse(sdf.format(targetMonth));
		} catch (Exception e) {
			System.out.println("init fail with targetMonth parsing");
		}
//		System.out.println("targetMonth:"+targetMonth);
		initProps();
		
		String rootResourcePath = ClassLoader.getSystemResource("").getPath();
		inputFilePath = rootResourcePath+conditionsAttrs.getProperty("inputFile.fileName.attenders");
		outputFilePath = rootResourcePath+conditionsAttrs.getProperty("outputFile.fileName.arrangements");
		outputInformFilePath = rootResourcePath+conditionsAttrs.getProperty("outputFile.fileName.informations");
		
		initAttendersMap();
	}
	
	/**
	 * 初始化排班人員表
	 * @return
	 */
	public boolean initAttendersMap(){
		try {
			attendersMap = new HashMap<>();
			dao.inputData(inputFilePath, attendersMap);
			initArrangements();
			return true;
		} catch (Exception e) {
			log.error("initAttendersMap fail, exception => {}",e.toString());
			System.out.println("initAttendersMap fail, exception => "+e.toString());
			return false;
		}
	}
	
	private void initArrangements() throws Exception{
//		try {
		arrangements = new TreeMap<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getTargetMonth().getTime());
		System.out.println(calendar.getTime());
		for (int i = 0; i <= 40; i++) {
			if (calendar.get(Calendar.MONTH) > getTargetMonth().getMonth())
				break;
			arrangements.put(calendar.getTimeInMillis(), new ArrangementPerDay(calendar.getTimeInMillis()));
			for (Attender attender : attendersMap.values()) {
				arrangements.get(calendar.getTimeInMillis()).getArrangeMap().put(attender.getId(),
						new AttenderStatus(attender.getId(), attender.getName()));
			}
			calendar.add(Calendar.DATE, 1);
		}
//		} catch (Exception e) {
//			log.error("initArrangements fail, exception => {}",e.toString());
//			System.out.println("initArrangements fail, exception => "+e.toString());
//		}
	}
	
	/**
	 * 初始化屬性
	 * @return
	 */
	public boolean initProps(){
		FileInputStream fis = null;
		try {
			conditionsAttrs = new Properties();
			fis = new FileInputStream(attrsPath);
			InputStreamReader isr = new InputStreamReader(fis, Const.DEFAULT_CHARSET);
			conditionsAttrs.load(isr);
			return true;
		} catch (Exception e) {
			log.error("initProps fail, exception => {}",e.toString());
			System.out.println("initProps fail, exception => "+e.toString());
			return false;
		}finally {
			try {
				if(fis != null)
					fis.close();
			} catch (Exception e2) {}
		}
	}
	
	/**
	 * 修改屬性
	 * @param properties
	 * @return
	 */
	public boolean updateProps(Map properties){
		FileOutputStream fos = null;
		try {
			for(Object key : properties.keySet()){
				conditionsAttrs.put(key, properties.get(key));
			}
			fos = new FileOutputStream(attrsPath);
			OutputStreamWriter writer = new OutputStreamWriter(fos, Const.DEFAULT_CHARSET);
			conditionsAttrs.store(writer,"");
			return true;
		} catch (Exception e) {
			log.error("updateProps fail, exception => {}",e.toString());
			System.out.println("updateProps fail, exception => "+e.toString());
			return false;
		}finally {
			try {
				if(fos != null)
					fos.close();
			} catch (Exception e2) {}
		}
	}
	
	/**
	 * 生成班表並輸出表單
	 * @param customProcessType__客戶種類
	 * @return
	 */
	public boolean genArrangements(String customProcessType){
		try {
			arrangementProcesser.genArrangements(arrangements, attendersMap);
			
			switch (customProcessType) {
			case "jurassic_vet":
				customProcesser = new NurseProcesserImpl();
				customProcesser.adjustArrangements(arrangements, attendersMap);
				break;

			default:
				System.err.println("unknown customProcessType："+customProcessType);
				break;
			}
			
			dao.outputArrangements(outputFilePath.replace("{MONTH}", new SimpleDateFormat("MMM").format(getTargetMonth())), arrangements);
			dao.outputInformations(outputInformFilePath, attendersMap);
		} catch (Exception e) {
			log.error("genArrangements fail, exception => {}",e.toString());
			System.out.println("genArrangements fail, exception => "+e.toString());
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) throws Exception{
		FlowController controller = new FlowController();
		
//		conditionsAttrs.put("hi", "你好");
//		System.out.println(controller.updateProps(conditionsAttrs));
		controller.genArrangements("jurassic_vet");
//		
		System.out.println("done");
		System.out.println(new Gson().toJson(arrangements));
	}

}
