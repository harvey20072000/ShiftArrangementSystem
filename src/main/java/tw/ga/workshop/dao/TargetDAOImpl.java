package tw.ga.workshop.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;
import tw.ga.workshop.com.FlowController;
import tw.ga.workshop.model.ArrangementPerDay;
import tw.ga.workshop.model.Attender;
import tw.ga.workshop.model.AttenderStatus;
import tw.ga.workshop.util.ExcelUtil;

@Slf4j
//@Service
public class TargetDAOImpl implements TargetDAO {

	private JsonParser parser = new JsonParser();
	private Gson gson = new Gson();
	private GsonBuilder gsonBuilder = new GsonBuilder();
	private String charset = "utf-8";
	
	private Map<Integer, String> dayOfWeek_ch = new HashMap<>();
	
	public TargetDAOImpl() {
		dayOfWeek_ch.put(0, "日");
		dayOfWeek_ch.put(1, "一");
		dayOfWeek_ch.put(2, "二");
		dayOfWeek_ch.put(3, "三");
		dayOfWeek_ch.put(4, "四");
		dayOfWeek_ch.put(5, "五");
		dayOfWeek_ch.put(6, "六");
	}
	
	@Override
	public Map<String ,Attender> inputData(String filePath , Map<String ,Attender> map) throws Exception{
		try {
			Map<Integer, Map<Integer, Object>> response = ExcelUtil.readXlsx(filePath);
			for (int r = 0; r < response.size(); r++) {
				String id = response.get(r).get(0).toString();
				try {
					String name = response.get(r).get(1).toString();
					Double restDaysM = Double.parseDouble(response.get(r).get(2).toString()),
							restRestDays =  Double.parseDouble(response.get(r).get(3).toString()),tempNum;
					Set<Long> targetRestDates = new HashSet<>();
					for (String s : response.get(r).get(4).toString().split(",")) {
						Date tempDate = new Date(FlowController.getTargetMonth().getTime());
						tempNum = Double.parseDouble(s);
						tempDate.setDate(tempNum.intValue());
//						System.out.println("inputData -> tempDate :"+tempDate);
						targetRestDates.add(tempDate.getTime());
					}
					map.put(id, new Attender(id, name, restDaysM.intValue(),restRestDays.intValue(), targetRestDates));
				} catch (Exception e) {
					log.error("inputData for id({}) fail, exception => {}",id,e.toString());
				}
			}
		} catch (Exception e) {
			log.error("inputData fail, exception => {}", e.toString());
		}
		System.out.println("inputData succeed! map.size() = "+map.size());
		return map;
	}
	
	private File createFileIfNotExist(String filePath) throws IOException{
		File file = new File(filePath);
		if(!file.exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return file;
	}

	@Override
	public boolean outputArrangements(String filePath, Map<Long, ArrangementPerDay> arrangements) throws Exception {
		Map<Integer, Map<Integer,Object>> output = genXlsxFormat(arrangements.size(), 7);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
		Date tempDate = new Date();
		int r = 0;
		for(Long tempTime : arrangements.keySet()){
			tempDate.setTime(tempTime);
			if(tempDate.getDay() == 0)
				r += 2;
			output.get(r).put(tempDate.getDay(),sdf.format(tempDate)+"("+dayOfWeek_ch.get(tempDate.getDay())+")");
			output.get(r+1).put(tempDate.getDay(),genArrangementString(arrangements.get(tempTime).getArrangeMap().values()));
		}
		return ExcelUtil.loadAndWriteXlsx(filePath, output);
	}
	
	private Map<Integer, Map<Integer,Object>> genXlsxFormat(int dataRowsNum, int dataColsNum){
		Map<Integer, Map<Integer,Object>> formatMap = new HashMap<>();
		for(int r=0;r<dataRowsNum*2;r++){
			for(int c=0;c<dataColsNum;c++){
				if(formatMap.get(r) == null)
					formatMap.put(r, new HashMap<>());
				formatMap.get(r).put(c, "");
			}
		}
		return formatMap;
	}
	
	private String genArrangementString(Collection<AttenderStatus> arrange){
		List<AttenderStatus> list = new ArrayList<>();
		for(AttenderStatus attenderStatus : arrange){
			if(attenderStatus.getStatus().startsWith("1")){
				list.add(0, attenderStatus);
			}else {
				list.add(attenderStatus);
			}
		}
		StringBuilder sb = new StringBuilder("");
		for(int i=0;i<list.size();i++){
			if(list.get(i).getStatus().startsWith("1")){
				sb.append(list.get(i).getName()+" 上班");
			}else if (list.get(i).getStatus().equals("00")) {
				sb.append(list.get(i).getName()+" 休假(約)");
			}else if (list.get(i).getStatus().startsWith("0")) {
				sb.append(list.get(i).getName()+" 休假(排)");
			}
			if(i != list.size()-1)
				sb.append("\r\n");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		TargetDAOImpl impl = new TargetDAOImpl();
//		for(Target target : impl.inputData("C:/Users/harvey20072000/Desktop/SideProjects/Portfolio/targets file/targets to run.txt", new HashMap<>()).values()){
//			System.out.println(target.getStockId());
//		}
		
//		Map<String, User> map = new HashMap<>();
//		map.put("root", new User("root", "1234"));
//		map.get("root").getTrackedTargets().put("2023", new TrackedTarget("2023"));
//		map.get("root").getTrackedTargets().put("1012", new TrackedTarget("1012"));
//		map.put("Harvey", new User("Harvey", "0819"));
//		map.get("Harvey").getTrackedTargets().put("2023", new TrackedTarget("2023"));
//		map.get("Harvey").getTrackedTargets().put("1012", new TrackedTarget("1012"));
//		map.get("Harvey").getTrackedTargets().put("1012", new TrackedTarget("1012"));
//		map.put("Judy", new User("Judy", "0419"));
//		map.get("Judy").getTrackedTargets().put("1012", new TrackedTarget("1012"));
//		impl.outputUsers("C:/Users/harvey20072000/Desktop/SideProjects/Portfolio/targets file/users output datas.txt", map);
//		
//		for(User user : impl.inputUsersWithDatas("C:/Users/harvey20072000/Desktop/SideProjects/Portfolio/targets file/users output datas.txt", map).values())
//			System.out.println(user.toString()+"　targets size："+user.getTrackedTargets().size());
		String[] array = new String[]{"root","1234"};
		if(array[0].matches("[a-z,A-Z,0-9]+") && array[1].matches("[a-z,A-Z,0-9]+")){
			System.out.println("match");
		}else {
			System.out.println("not match");
		}
	}

	@Override
	public boolean outputInformations(String filePath, Map<String, Attender> attendersMap) throws Exception {
		OutputStream os = null;
		try {
			File file = createFileIfNotExist(filePath);
			
			System.out.println("outputData : " + file.getAbsolutePath());
			// create a new OutputStreamWriter
			os = new FileOutputStream(filePath);
			OutputStreamWriter writer = new OutputStreamWriter(os,charset);
			StringBuilder sb = new StringBuilder("");
			for(Attender attender : attendersMap.values()){
				sb.append(attender.getId()+"\t"+attender.getName()+"\t剩餘休假日:"+attender.getRestRestDays()+"天\r\n");
			}
			
			// write something in the file
			writer.write(sb.toString());

			// flush the stream
			writer.flush();
			return true;
		} catch (Exception ex) {
			log.error("outputData fail, exception => {}", ex.toString());
			ex.printStackTrace();
			throw ex;
		}finally {
			if(os != null)
				os.close();
		}
	}

}
