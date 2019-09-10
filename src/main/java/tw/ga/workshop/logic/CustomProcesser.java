package tw.ga.workshop.logic;

import java.util.Map;

import tw.ga.workshop.model.ArrangementPerDay;
import tw.ga.workshop.model.Attender;

public interface CustomProcesser {

	void adjustArrangements(Map<Long, ArrangementPerDay> arrangements, Map<String, Attender> attendersMap) throws Exception;
	
}
