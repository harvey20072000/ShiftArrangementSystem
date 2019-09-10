package tw.ga.workshop.model;

import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Attender {
	
	private String id;									// id
	private String name;								// 姓名
	
	private int restDaysM;								// 每月規定休假日數
	private int restRestDays;							// 剩餘休假日數
	
	private Set<Long> targetRestDates;					// 計畫休假日
	
	private Map<Long, AttenderStatus> restStatusMap;	// 個人班表

	public Attender() {
	}
	
	public Attender(String id, String name, int restDaysM, Set<Long> targetRestDates) {
		this.id = id;
		this.name = name;
		this.restDaysM = restDaysM;
		this.targetRestDates = targetRestDates;
	}

	public Attender(String id, String name, int restDaysM, int restRestDays, Set<Long> targetRestDates) {
		this();
		this.id = id;
		this.name = name;
		this.restDaysM = restDaysM;
		this.restRestDays = restRestDays;
		this.targetRestDates = targetRestDates;
	}
	
}
