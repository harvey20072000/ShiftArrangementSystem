package tw.ga.workshop.logic;

import java.util.Map;

import tw.ga.workshop.model.ArrangementPerDay;
import tw.ga.workshop.model.Attender;

public interface ArrangementProcesser {

	/**
	 * 生成初始排班表
	 * @param arrangements
	 * @param attendersMap
	 * @throws Exception
	 */
	void genArrangements(Map<Long, ArrangementPerDay> arrangements, Map<String, Attender> attendersMap) throws Exception;
	
}
