package tw.ga.workshop.dao;

import java.util.Map;

import tw.ga.workshop.model.ArrangementPerDay;
import tw.ga.workshop.model.Attender;

public interface TargetDAO {

	Map<String ,Attender> inputData(String filePath , Map<String ,Attender> map) throws Exception;
	
	boolean outputArrangements(String filePath, Map<Long, ArrangementPerDay> arrangements) throws Exception;
	
	boolean outputInformations(String filePath, Map<String, Attender> attendersMap) throws Exception;
	
//	boolean outputData(String filePath , Map<String ,Target> map) throws Exception;
//	
//	boolean outputLine(String filePath , Map<String ,String> map) throws Exception;
	
//	Map<String ,User> inputUsers(String filePath , Map<String ,User> map) throws Exception;
//	
//	Map<String ,User> inputUsersWithDatas(String filePath , Map<String ,User> map) throws Exception;
	
//	boolean outputUsers(String filePath , Map<String ,User> map) throws Exception;
	
}
