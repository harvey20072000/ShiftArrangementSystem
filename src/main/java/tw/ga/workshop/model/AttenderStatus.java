package tw.ga.workshop.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttenderStatus {
	
	private String id;									// id
	private String name;								// 姓名
	
	private String status;								// 0:休假__00:預約休假__1:上班
	private int statusSeq;								// 連續幾日
	
	private Long dateTime;
	
	public AttenderStatus(){
		// 預設為上班狀態
		this.status = "1";
	}

	public AttenderStatus(String id, String name) {
		this();
		this.id = id;
		this.name = name;
	}
	
	public AttenderStatus(String id, String name, String status) {
		this();
		this.id = id;
		this.name = name;
		this.status = status;
	}
	
	
	
}
