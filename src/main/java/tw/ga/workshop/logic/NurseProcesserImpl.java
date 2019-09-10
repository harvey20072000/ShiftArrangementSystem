package tw.ga.workshop.logic;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import tw.ga.workshop.com.FlowController;
import tw.ga.workshop.model.ArrangementPerDay;
import tw.ga.workshop.model.Attender;
import tw.ga.workshop.model.AttenderStatus;

@Slf4j
//@Service
public class NurseProcesserImpl implements CustomProcesser {

	//private Map<String, Integer> restRestDaysRecords = new HashMap<>();		// 剩餘休假日紀錄
	
	
//	private JsonParser parser = new JsonParser();
//	private Gson gson = new Gson();
//	private String charset = "utf-8";
//	private final String GOODINFO_URL = "https://www.cmoney.tw/finance/technicalanalysis.aspx?s={STOCK_ID}";
	
	

	public static void main(String[] args) throws Exception {
		NurseProcesserImpl impl = new NurseProcesserImpl();
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		DecimalFormat df = new DecimalFormat("00");
		Long dateTime;
		
		Map<String, Attender> attendersMap = new HashMap<>();
		Set<Long> set = new HashSet<>();
		int randomNum = new Random().nextInt(8);
		for(int i = 0;i<randomNum;i++){
			set.add(sdf.parse("201801"+df.format(new Random().nextInt(7)+1)).getTime());
		}
		attendersMap.put("111",new Attender("111", "Harvey", 15,set));
		
		set = new HashSet<>();
		randomNum = new Random().nextInt(8);
		for(int i = 0;i<randomNum;i++){
			set.add(sdf.parse("201801"+df.format(new Random().nextInt(7)+8)).getTime());
		}
		attendersMap.put("112",new Attender("112", "Sam", 15,set));
		
		set = new HashSet<>();
		randomNum = new Random().nextInt(8);
		for(int i = 0;i<randomNum;i++){
			set.add(sdf.parse("201801"+df.format(new Random().nextInt(7)+15)).getTime());
		}
		attendersMap.put("113",new Attender("113", "Jess", 15,set));
		
		set = new HashSet<>();
		randomNum = new Random().nextInt(8);
		for(int i = 0;i<randomNum;i++){
			set.add(sdf.parse("201801"+df.format(new Random().nextInt(7)+22)).getTime());
		}
		attendersMap.put("114",new Attender("114", "Lily", 15,set));
		Map<String,AttenderStatus> attenderStatuss = new HashMap<>();
		attenderStatuss.put("111",new AttenderStatus("111", "Harvey", "1"));
		attenderStatuss.put("112",new AttenderStatus("112", "Sam", "1"));
		attenderStatuss.put("113",new AttenderStatus("113", "Jess", "1"));
		attenderStatuss.put("114",new AttenderStatus("114", "Lily", "1"));
		Map<Long, ArrangementPerDay> arrangements = new TreeMap<>();
		
		for(int i = 1;i<= 31;i++){
			try {
				arrangements.put((dateTime = sdf.parse("201801"+df.format(i)).getTime()), new ArrangementPerDay(dateTime, new HashMap<>(attenderStatuss)));
			} catch (Exception e) {}
			attenderStatuss = new HashMap<>();
			attenderStatuss.put("111",new AttenderStatus("111", "Harvey", "1"));
			attenderStatuss.put("112",new AttenderStatus("112", "Sam", "1"));
			attenderStatuss.put("113",new AttenderStatus("113", "Jess", "1"));
			attenderStatuss.put("114",new AttenderStatus("114", "Lily", "1"));
		}
		impl.adjustArrangements(arrangements, attendersMap);
		
		System.out.println(new Gson().toJson(arrangements));
	}



	@Override
	public void adjustArrangements(Map<Long, ArrangementPerDay> arrangements, Map<String, Attender> attendersMap)
			throws Exception {
		Map<String, Integer> restRestDaysMap = inputRecords(attendersMap);		// 剩餘休假日紀錄
		int avgWorkerPerDay = FlowController.avgWorkerPerDay;
		
		Map<String, List<AttenderStatus>> arrangeMap = new HashMap<>();
		Map<Long, Integer> workersNumMap = new HashMap<>();
		//list.sort();
		for(String id : restRestDaysMap.keySet()){
			arrangeMap.put(id, new LinkedList<>());
		}
		for(ArrangementPerDay perDay : arrangements.values()){
			int potentialWorkerNum = 0;
			for(AttenderStatus status : perDay.getArrangeMap().values()){
				// 員工-工作數關係表
				for(String id:arrangeMap.keySet()){
					if(id.equals(status.getId())){
						status.setDateTime(perDay.getDateTime());
						arrangeMap.get(id).add(status);
					}
				}
				// 日期-工作人數對應表
				if(status.getStatus().startsWith("1"))
					potentialWorkerNum++;
			}
			workersNumMap.put(perDay.getDateTime(), new Integer(potentialWorkerNum));
		}
		for(String id : arrangeMap.keySet()){
			arrangeMap.get(id).sort(new MyComparator(restRestDaysMap.get(id)));
//			System.out.println("sort list for id:"+id+" restRestDays:"+restRestDaysMap.get(id));
//			for(AttenderStatus testStatus : arrangeMap.get(id))
//				System.out.println(testStatus.getStatusSeq());
		}
		
		int totalRestRestDays = 0,statusChanged=0;
		while((totalRestRestDays = calTotalRestRestDays(restRestDaysMap)) > 0){	// 有剩餘假日的再塞假進去
			System.out.println("totalRestRestDays："+totalRestRestDays);
			for(String id : arrangeMap.keySet()){
				if(statusChanged == -1){
					for(AttenderStatus tempStatus : arrangeMap.get(id)){
						if(restRestDaysMap.get(id) > 0 && tempStatus.getStatus().startsWith("1")){
							if(workersNumMap.get(tempStatus.getDateTime()) >= avgWorkerPerDay){
								arrangements.get(tempStatus.getDateTime()).getArrangeMap().get(tempStatus.getId()).setStatus("0");
								restRestDaysMap.put(tempStatus.getId(), restRestDaysMap.get(tempStatus.getId()) - 1);
								workersNumMap.put(tempStatus.getDateTime(),workersNumMap.get(tempStatus.getDateTime())-1);
							}
						}else if (restRestDaysMap.get(id) < 0 && tempStatus.getStatus().startsWith("0") && !tempStatus.getStatus().equals("00")) {
							if(workersNumMap.get(tempStatus.getDateTime()) <= avgWorkerPerDay){
								statusChanged++;
								arrangements.get(tempStatus.getDateTime()).getArrangeMap().get(tempStatus.getId()).setStatus("1");
								restRestDaysMap.put(tempStatus.getId(), restRestDaysMap.get(tempStatus.getId()) + 1);
								workersNumMap.put(tempStatus.getDateTime(),workersNumMap.get(tempStatus.getDateTime())+1);
							}
						}
					}
				}else {
					for(AttenderStatus tempStatus : arrangeMap.get(id)){
						if(restRestDaysMap.get(id) > 0 && tempStatus.getStatus().startsWith("1")){
							if(workersNumMap.get(tempStatus.getDateTime()) > avgWorkerPerDay){
								statusChanged++;
								arrangements.get(tempStatus.getDateTime()).getArrangeMap().get(tempStatus.getId()).setStatus("0");
								restRestDaysMap.put(tempStatus.getId(), restRestDaysMap.get(tempStatus.getId()) - 1);
								workersNumMap.put(tempStatus.getDateTime(),workersNumMap.get(tempStatus.getDateTime())-1);
							}
						}else if (restRestDaysMap.get(id) < 0 && tempStatus.getStatus().startsWith("0") && !tempStatus.getStatus().equals("00")) {
							if(workersNumMap.get(tempStatus.getDateTime()) < avgWorkerPerDay){
								statusChanged++;
								arrangements.get(tempStatus.getDateTime()).getArrangeMap().get(tempStatus.getId()).setStatus("1");
								restRestDaysMap.put(tempStatus.getId(), restRestDaysMap.get(tempStatus.getId()) + 1);
								workersNumMap.put(tempStatus.getDateTime(),workersNumMap.get(tempStatus.getDateTime())+1);
							}
						}
					}
				}
			}
			statusChanged = -1;
		}
		// 多排假日的再補回來
		// 把剩餘假日寫入個人資料
		for (String id : restRestDaysMap.keySet()) {
			for (Attender attender : attendersMap.values()) {
				if (id.equals(attender.getId())) {
					attender.setRestRestDays(restRestDaysMap.get(id));
				}
			}
		}
		
	}
	
	private int calTotalRestRestDays(Map<String, Integer> restRestDaysMap){
		int result = 0;
		for(Integer integer : restRestDaysMap.values())
			result += (integer > 0 ? integer : -1*integer);
		return result;
	}
	
	private class MyComparator implements Comparator<AttenderStatus> {
		
		private int processType;
		
		MyComparator(){}
		
		MyComparator(int processType){
			this();
			this.processType = processType;
		}
		
		@Override
		public int compare(AttenderStatus o1, AttenderStatus o2) {
			return (o2.getStatusSeq()-o1.getStatusSeq())*processType;
		}
	}
	
	private Map<String, Integer> inputRecords(Map<String, Attender> attendersMap){
		Map<String, Integer> temp = new HashMap<>();
		int restRestDays;
		for(Attender attender : attendersMap.values()){
			if((restRestDays = attender.getRestRestDays()) != 0)
				temp.put(attender.getId(), restRestDays);
		}
		return temp;
	}
	
	private Attender getOtherRandomAttender(Map<String, Attender> attendersMap , String ids){
		Map<String, Attender> tempAttenders = new HashMap<>(attendersMap);
		for(Attender attender : attendersMap.values()){
			for(String id : ids.split(",")){
				if(attender.getId().equals(id)){
					tempAttenders.remove(id);
				}
			}
		}
		Random random = new Random();
		int i = 0, theIndex = random.nextInt(tempAttenders.size());
		for(Attender attender : attendersMap.values()){
			if(i == theIndex)
				return attender;
			i++;
		}
		return null;
	}
	
	private void updateStatusSeqs(Map<Long, ArrangementPerDay> arrangements, Map<String, Attender> attendersMap){
		List<Long> dates = new LinkedList<>();
		for(Long date : arrangements.keySet()){
			dates.add(date);
		}
		Map<String, String> previousStatusMap = new HashMap<>();
		for(int i=0,j=i-1;i<arrangements.size();i++){
			if(i == 0){
				for(AttenderStatus tempStatus : arrangements.get(dates.get(i)).getArrangeMap().values()){
					if(tempStatus.getStatus().startsWith("1")){	// TODO 這邊之後也要改成load上個月的方式
						tempStatus.setStatusSeq(1);
					}else {
						tempStatus.setStatusSeq(0);
					}
				}
			}else {
				for(AttenderStatus tempStatus : arrangements.get(dates.get(j)).getArrangeMap().values()){
					if(previousStatusMap.get(tempStatus.getId()) == null)
						previousStatusMap.put(tempStatus.getId(), tempStatus.getStatus());
					for(AttenderStatus innertempStatus : arrangements.get(dates.get(i)).getArrangeMap().values()){
						if(tempStatus.getId().equals(innertempStatus.getId())){
							String currentStatus = innertempStatus.getStatus(), previousStatus = tempStatus.getStatus();
							if (previousStatus.startsWith("1")) {
								if (currentStatus.startsWith("1")) {
									innertempStatus.setStatusSeq(innertempStatus.getStatusSeq() + 1);
								} else {
									innertempStatus.setStatusSeq(0);
								}
							} else {
								if (currentStatus.startsWith("1")) {
									innertempStatus.setStatusSeq(1);
								} else {
									innertempStatus.setStatusSeq(innertempStatus.getStatusSeq() - 1);
								}
							}
						}
					}
				}
			}
		}
	}
}
