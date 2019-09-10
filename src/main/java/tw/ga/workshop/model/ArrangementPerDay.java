package tw.ga.workshop.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArrangementPerDay {
	
	private Long dateTime;													// 工作日的long時間
	
	private String note;													// 備註
	
	private Map<String, AttenderStatus> arrangeMap = new HashMap<>();		// 當天班表

	public ArrangementPerDay(){};
	
	public ArrangementPerDay(Long dateTime) {
		this.dateTime = dateTime;
	}
	
	public ArrangementPerDay(Long dateTime, Map<String, AttenderStatus> arrangeMap) {
		this.dateTime = dateTime;
		this.arrangeMap = arrangeMap;
	}
	
}
