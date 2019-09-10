package tw.ga.workshop.com.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import tw.ga.workshop.com.FlowController;
import tw.ga.workshop.util.GoogleUtil;

public class MyFrame extends JFrame {

	private FlowController controller = FlowController.getInstance();
	
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyFrame frame = new MyFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MyFrame() {
		setTitle("侏儸紀動物醫院-排班系統");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 400, 324, 209);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton reloadAttendersButton = new JButton("重新載入排班人員表");
		reloadAttendersButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(controller.initAttendersMap()){
					JOptionPane.showMessageDialog(null, "載　入　成　功"
							,"",JOptionPane.INFORMATION_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(null, "載　入　失　敗"
							,"",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		reloadAttendersButton.setBounds(71, 23, 169, 23);
		contentPane.add(reloadAttendersButton);
		
		JButton updateAttrsButton = new JButton("修改排班邏輯參數");
		updateAttrsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							UpdateFrame updateFrame = new UpdateFrame(controller);
							updateFrame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		updateAttrsButton.setBounds(71, 72, 169, 23);
		contentPane.add(updateAttrsButton);
		
		JComboBox selectTMonthBox = new JComboBox();
		selectTMonthBox.setModel(new DefaultComboBoxModel(new String[] {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"}));
		selectTMonthBox.setSelectedIndex(controller.getTargetMonth().getMonth());
		selectTMonthBox.setBounds(39, 121, 75, 21);
		contentPane.add(selectTMonthBox);
		
		JButton genArrangementsButton = new JButton("生成班表並輸出表格");
		genArrangementsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Date date = controller.getTargetMonth();
				date.setMonth(selectTMonthBox.getSelectedIndex());
				controller.setTargetMonth(date);
				if(controller.genArrangements("jurassic_vet")){
					JOptionPane.showMessageDialog(null, "輸　出　成　功"
							,"",JOptionPane.INFORMATION_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(null, "輸　出　失　敗"
							,"",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		genArrangementsButton.setBounds(124, 120, 141, 23);
		contentPane.add(genArrangementsButton);
		if(!GoogleUtil.checkA_()){ //
			JOptionPane.showMessageDialog(null, "授權失敗，請聯絡開發人員", "系　統　初　始　化", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
}
