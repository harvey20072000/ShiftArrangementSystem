package tw.ga.workshop.logic;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;
import tw.ga.workshop.com.FlowController;
import tw.ga.workshop.model.ArrangementPerDay;
import tw.ga.workshop.model.Attender;
import tw.ga.workshop.model.AttenderStatus;

@Slf4j
//@Service
public class ArrangementProcesserImpl implements ArrangementProcesser {

	//private Map<String, Integer> restRestDaysRecords = new HashMap<>();		// 剩餘休假日紀錄
	
	
//	private JsonParser parser = new JsonParser();
//	private Gson gson = new Gson();
//	private String charset = "utf-8";
//	private final String GOODINFO_URL = "https://www.cmoney.tw/finance/technicalanalysis.aspx?s={STOCK_ID}";
	
	

	public static void main(String[] args) throws Exception {
		ArrangementProcesserImpl impl = new ArrangementProcesserImpl();
//		String string = WebUtil.sendGet("https://statementdog.com/api/v1/fundamentals/1525/2016/1/2017/4/?queried_by_user=false&_=1499219282720");
		//System.out.println(new SimpleDateFormat("MM").parse(12+""));
		//System.out.println(impl.calWorkingDays(false,new Date()));
		
//		Map<String, Integer> restRestDaysRecords = new HashMap<>();
//		restRestDaysRecords.put("0", 8);
//		restRestDaysRecords.put("1", 6);
//		restRestDaysRecords.put("3", 8);
//		restRestDaysRecords.put("4", 10);
//		restRestDaysRecords.put("5", 10);
//		int workingDays = impl.calWorkingDays(false, new Date());
//		System.out.println(impl.calTotalWorkDays(workingDays, restRestDaysRecords)/workingDays);

//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		DecimalFormat df = new DecimalFormat("00");
//		Long dateTime;
//		
//		Map<String, Attender> attendersMap = new HashMap<>();
//		Set<Long> set = new HashSet<>();
//		int randomNum = new Random().nextInt(8);
//		for(int i = 0;i<randomNum;i++){
//			set.add(sdf.parse("201801"+df.format(new Random().nextInt(7)+1)).getTime());
//		}
//		attendersMap.put("111",new Attender("111", "Harvey", 15,set));
//		
//		set = new HashSet<>();
//		randomNum = new Random().nextInt(8);
//		for(int i = 0;i<randomNum;i++){
//			set.add(sdf.parse("201801"+df.format(new Random().nextInt(7)+8)).getTime());
//		}
//		attendersMap.put("112",new Attender("112", "Sam", 15,set));
//		
//		set = new HashSet<>();
//		randomNum = new Random().nextInt(8);
//		for(int i = 0;i<randomNum;i++){
//			set.add(sdf.parse("201801"+df.format(new Random().nextInt(7)+15)).getTime());
//		}
//		attendersMap.put("113",new Attender("113", "Jess", 15,set));
//		
//		set = new HashSet<>();
//		randomNum = new Random().nextInt(8);
//		for(int i = 0;i<randomNum;i++){
//			set.add(sdf.parse("201801"+df.format(new Random().nextInt(7)+22)).getTime());
//		}
//		attendersMap.put("114",new Attender("114", "Lily", 15,set));
//		Map<String,AttenderStatus> attenderStatuss = new HashMap<>();
//		attenderStatuss.put("111",new AttenderStatus("111", "Harvey", "1"));
//		attenderStatuss.put("112",new AttenderStatus("112", "Sam", "1"));
//		attenderStatuss.put("113",new AttenderStatus("113", "Jess", "1"));
//		attenderStatuss.put("114",new AttenderStatus("114", "Lily", "1"));
//		Map<Long, ArrangementPerDay> arrangements = new TreeMap<>();
//		
//		for(int i = 1;i<= 31;i++){
//			try {
//				arrangements.put((dateTime = sdf.parse("201801"+df.format(i)).getTime()), new ArrangementPerDay(dateTime, new HashMap<>(attenderStatuss)));
//			} catch (Exception e) {}
//			attenderStatuss = new HashMap<>();
//			attenderStatuss.put("111",new AttenderStatus("111", "Harvey", "1"));
//			attenderStatuss.put("112",new AttenderStatus("112", "Sam", "1"));
//			attenderStatuss.put("113",new AttenderStatus("113", "Jess", "1"));
//			attenderStatuss.put("114",new AttenderStatus("114", "Lily", "1"));
//		}
//		impl.genArrangements(arrangements, attendersMap);
//		
//		System.out.println(new Gson().toJson(arrangements));
		for(Entry<Integer, Double> entry:impl.genProbabilities(6,3,3).entrySet()){
			System.out.println(entry.getKey() +"："+entry.getValue());
		}
		
	}



	@Override
	public void genArrangements(Map<Long, ArrangementPerDay> arrangements, Map<String, Attender> attendersMap)
			throws Exception {
		Map<String, Integer> restRestDaysRecords = inputRecords(attendersMap);		// 剩餘休假日紀錄
		int maxContinueWorkDays = Integer.parseInt(FlowController.conditionsAttrs.getProperty("custom.settings.max_continue_work_days")),
			idealContinueWorkDays = Integer.parseInt(FlowController.conditionsAttrs.getProperty("custom.settings.ideal_continue_work_days")),
			idealContinueRestDays = Integer.parseInt(FlowController.conditionsAttrs.getProperty("custom.settings.ideal_continue_rest_days")),
			weekendRestDays = Integer.parseInt(FlowController.conditionsAttrs.getProperty("custom.settings.weekend_rest_days")), 
			workingDays = (FlowController.workingDays = calWorkingDays(weekendRestDays > 0 ? true : false, FlowController.getTargetMonth())),
			avgWorkerPerDay = (FlowController.avgWorkerPerDay = calTotalWorkDays(workingDays, restRestDaysRecords)/workingDays);
			
		Map<Integer, Double> probMap = genProbabilities(maxContinueWorkDays, idealContinueWorkDays, idealContinueRestDays);
		
		
		//塞入期望假日
		for(Attender attender : attendersMap.values()){
			for(Long targetRestDate : attender.getTargetRestDates()){
				arrangements.get(targetRestDate).getArrangeMap().get(attender.getId()).setStatus("00");
//				System.out.println(new Gson().toJson(arrangements));
			}
		}
		
		//開始塞剩下的假
		Map<String, Integer> statusSeq = new HashMap<>();
		for(Attender attender : attendersMap.values()){	// TODO 弄個能引進history arrangement來看上個月底的排班情況
			statusSeq.put(attender.getId(), 0);
		}
		
		for(Long arrangeDate : arrangements.keySet()){
			for (AttenderStatus tempStatus : arrangements.get(arrangeDate).getArrangeMap().values()) {
				if(tempStatus.getStatus().equals("00")){	// 若今天為預約休假，則無須排假
					restRestDaysRecords.put(tempStatus.getId(), restRestDaysRecords.get(tempStatus.getId()) - 1);
				}else if(weekendRestDays > 0 && (new Date(arrangeDate).getDay() == 0 || new Date(arrangeDate).getDay() == 6)){	// TODO 若是有規定周休的，不一定是二日，要再改
					tempStatus.setStatus("0");
					restRestDaysRecords.put(tempStatus.getId(), restRestDaysRecords.get(tempStatus.getId()) - 1);
				}else if (restRestDaysRecords.get(tempStatus.getId()) > -5) {	// 先讓系統排定的時後有預支假可以排，校正的時候再補回來
					int potentialWorkerNum = 0;
					for (AttenderStatus temp : arrangements.get(arrangeDate).getArrangeMap().values()) {
						if (temp.getStatus().startsWith("1"))
							potentialWorkerNum++;
					}
					// 若是當天未請假人數超過平均每天所需工作人數，則考慮排假
					if (potentialWorkerNum > avgWorkerPerDay) {
						if(probMap.containsKey(statusSeq.get(tempStatus.getId()))){
							if(Math.random() < probMap.get(statusSeq.get(tempStatus.getId()))){
								tempStatus.setStatus("0");
								restRestDaysRecords.put(tempStatus.getId(), restRestDaysRecords.get(tempStatus.getId()) - 1);
							}
						}else if(statusSeq.get(tempStatus.getId()) >= maxContinueWorkDays){
							tempStatus.setStatus("0");
							restRestDaysRecords.put(tempStatus.getId(), restRestDaysRecords.get(tempStatus.getId()) - 1);
						}else if (statusSeq.get(tempStatus.getId()) <= idealContinueRestDays) {
							tempStatus.setStatus("1");
						}
												
//						if(statusSeq.get(tempStatus.getId()) >= 4){
//							tempStatus.setStatus("0");
//							restRestDaysRecords.put(tempStatus.getId(), restRestDaysRecords.get(tempStatus.getId()) - 1);
//						}else if (statusSeq.get(tempStatus.getId()) >= 3) {
//							if(new Random().nextInt(100) >= 15){
//								tempStatus.setStatus("0");
//								restRestDaysRecords.put(tempStatus.getId(), restRestDaysRecords.get(tempStatus.getId()) - 1);
//							}else {
//								tempStatus.setStatus("1");
//							}
//						}else if (statusSeq.get(tempStatus.getId()) >= 2) {
//							if(new Random().nextInt(100) >= 30){
//								tempStatus.setStatus("0");
//								restRestDaysRecords.put(tempStatus.getId(), restRestDaysRecords.get(tempStatus.getId()) - 1);
//							}else {
//								tempStatus.setStatus("1");
//							}
//						}else if (statusSeq.get(tempStatus.getId()) >= 1) {
//							if(new Random().nextInt(100) >= 95){
//								tempStatus.setStatus("0");
//								restRestDaysRecords.put(tempStatus.getId(), restRestDaysRecords.get(tempStatus.getId()) - 1);
//							}else {
//								tempStatus.setStatus("1");
//							}
//						}else if(statusSeq.get(tempStatus.getId()) == 0){
//							if(new Random().nextInt(100) >= 15){	
//								tempStatus.setStatus("0");
//								restRestDaysRecords.put(tempStatus.getId(), restRestDaysRecords.get(tempStatus.getId()) - 1);
//							}else {
//								tempStatus.setStatus("1");
//							}
//						}else if(statusSeq.get(tempStatus.getId()) >= -1){
//							if(new Random().nextInt(100) >= 90){	
//								tempStatus.setStatus("0");
//								restRestDaysRecords.put(tempStatus.getId(), restRestDaysRecords.get(tempStatus.getId()) - 1);
//							}else {
//								tempStatus.setStatus("1");
//							}
//						}else if(statusSeq.get(tempStatus.getId()) <= -2){
//							tempStatus.setStatus("1");
//						}else {
//							if(new Random().nextInt(100) < 30){
//								tempStatus.setStatus("0");
//								restRestDaysRecords.put(tempStatus.getId(), restRestDaysRecords.get(tempStatus.getId()) - 1);
//							}else {
//								tempStatus.setStatus("1");
//							}
//						}
					}
				}

				// 改變statusSeq
				updateStatusSeq(statusSeq, arrangeDate, arrangements, tempStatus.getId());
			}
		}
		
		// 把剩餘假日寫入個人資料
		for(String id : restRestDaysRecords.keySet()){
			for(Attender attender : attendersMap.values()){
				if(id.equals(attender.getId())){
					attender.setRestRestDays(restRestDaysRecords.get(id));
				}
			}
		}
	}
	
	private Map<String, Integer> inputRecords(Map<String, Attender> attendersMap){
		Map<String, Integer> temp = new HashMap<>();
		for(Attender attender : attendersMap.values()){
			temp.put(attender.getId(), new Integer(attender.getRestDaysM()+attender.getRestRestDays()));
		}
		return temp;
	}
	
	private int calWorkingDays(boolean isSkipWeekend, Date targetDate){
		DecimalFormat monthFormat = new DecimalFormat("00"),yearFormat = new DecimalFormat("0000");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		Date tempDate = null;
		try {
			tempDate = dateFormat.parse(yearFormat.format(targetDate.getYear()+1900)+monthFormat.format(targetDate.getMonth()+2));
			tempDate.setTime(tempDate.getTime() - 24*60*60*1000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(isSkipWeekend){
			int count = 0;
			try {
				Date tempDate2 = new Date(tempDate.getTime());
				for(int i=tempDate.getDate();i>0;i--){
					tempDate2.setDate(i);
					if(tempDate2.getDay() != 0 && tempDate2.getDay() != 6){
						count++;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return count != 0 ? count : -1;
		}else {
			return tempDate != null? tempDate.getDate(): -1;
		}
	}
	
	private int calTotalWorkDays(int workingDays, Map<String, Integer> restDays){
		int total = 0;
		for(Integer i : restDays.values()){
			total += workingDays - i;
		}
		return total;
	}
	
	private Map<Integer, Double> genProbabilities(int maxNum, int threshold, int threshold2){
		Map<Integer, Double> result = new HashMap<>();
		if(maxNum > 2){
			Double maxDouble = new Double(maxNum);
			for(Double i=1.0;i<=maxNum;i++){
				if(i<threshold){
					result.put(i.intValue(), i*0.05);
				}else {
					result.put(i.intValue(), 1-Math.pow(1 - Math.pow((maxDouble*12+i)/(maxDouble*13), 2), 0.5));
				}
			}
		}else {
			result.put(1, 0.7);
			result.put(2, 1.0);
		}
		Double maxDouble = new Double(threshold2);
		for(Double i=0.0;i<=threshold2;i++){
			if(i < threshold2-1){
				result.put(-1*i.intValue(), 1.0-(i+1)*0.15);
			}else {
				result.put(-1*i.intValue(), Math.pow(1 - Math.pow((maxDouble*12+i)/(maxDouble*13), 2), 0.5));
			}
		}
		return result;
	}
	
//	private Attender getOtherRandomAttender(Map<String, Attender> attendersMap , String ids){
//		Map<String, Attender> tempAttenders = new HashMap<>(attendersMap);
//		for(Attender attender : attendersMap.values()){
//			for(String id : ids.split(",")){
//				if(attender.getId().equals(id)){
//					tempAttenders.remove(id);
//				}
//			}
//		}
//		Random random = new Random();
//		int i = 0, theIndex = random.nextInt(tempAttenders.size());
//		for(Attender attender : attendersMap.values()){
//			if(i == theIndex)
//				return attender;
//			i++;
//		}
//		return null;
//	}
	
	private void updateStatusSeq(Map<String, Integer> statusSeq, Long currentDate
			, Map<Long, ArrangementPerDay> arrangements, String id){
		List<Long> dates = new LinkedList<>();
		for(Long date : arrangements.keySet()){
			dates.add(date);
		}
		int previousIndex = dates.indexOf(currentDate) - 1;
		
		String currentStatus = arrangements.get(currentDate).getArrangeMap().get(id).getStatus(),previousStatus;
		if (previousIndex >= 0) {
			previousStatus = arrangements.get(dates.get(previousIndex)).getArrangeMap().get(id).getStatus();
			if (previousStatus.startsWith("1")) {
				if (currentStatus.startsWith("1")) {
					statusSeq.put(id, statusSeq.get(id) + 1);
				} else {
					statusSeq.put(id, 0);
				}
			} else {
				if (currentStatus.startsWith("1")) {
					statusSeq.put(id, 1);
				} else {
					statusSeq.put(id, statusSeq.get(id) - 1);
				}
			}
		}else {
			// TODO 從上個月的最後一天班表來找
			if (currentStatus.startsWith("1")) {
				statusSeq.put(id, 1);
			} else {
				statusSeq.put(id, 0);
			}
		}
		arrangements.get(currentDate).getArrangeMap().get(id).setStatusSeq(statusSeq.get(id));
	}
}
