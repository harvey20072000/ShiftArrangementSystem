package tw.ga.workshop.com.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import tw.ga.workshop.com.FlowController;

public class UpdateFrame extends JFrame {
	
	private JPanel contentPane;
	
	private Map<String, Components> map = new HashMap<>();
	
	private FlowController controller;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UpdateFrame frame = new UpdateFrame();
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
	public UpdateFrame() {
		setTitle("修　改　參　數");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	if(checkAndOutputInputs()){
		    		JOptionPane.showMessageDialog(null, "儲　存　成　功"
							,"",JOptionPane.INFORMATION_MESSAGE);
		    		dispose();
		    	}
		    }
		});
		setBounds(500, 400, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
	}
	
	public UpdateFrame(FlowController controller){
		this();
		this.controller = controller;
		Properties properties = controller.conditionsAttrs;
		
		int labelX = 5,y = 5, textFieldX = 160;
		for(Object key : properties.keySet()){
			if(key.toString().startsWith("custom.settings")){
				String text = "default";
				if("custom.settings.max_continue_work_days".equals(key.toString())){
					text = "連續工作最大天數";
				}else if ("custom.settings.ideal_continue_work_days".equals(key.toString())) {
					text = "理想工作天數";
				}else if ("custom.settings.ideal_continue_rest_days".equals(key.toString())) {
					text = "理想休假天數";
				}
//				else if ("custom.settings.weekend_rest_days".equals(key.toString())) {
//					text = "是否有周休二日";
//				}
				if("default".equals(text))
					continue;
				JLabel label = new JLabel(text);
				label.setBounds(labelX, y, 150, 30);
//				label.setHorizontalAlignment(JLabel.CENTER);
				contentPane.add(label);

				JTextField textField = new JTextField(properties.getProperty(key.toString()));
				textField.setBounds(textFieldX, y, 50, 30);
				contentPane.add(textField);
				
				map.put(key.toString(), new Components(label, textField));
				
				y += 35;
			}
		}
		
		JButton saveButton = new JButton("儲存");
		saveButton.setBounds(labelX, y, 70, 30);
		y += 35;
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkAndOutputInputs())
					JOptionPane.showMessageDialog(null, "儲　存　成　功"
							,"",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		contentPane.add(saveButton);
		
		setBounds(500, 400, 330, y+100);
	}
	
	private boolean checkAndOutputInputs(){
//		Pattern pattern = Pattern.compile("[\\d]{1,5}");
//		Matcher matcher;
		String text;
		Map propMap = new HashMap<>();
		for(String key : map.keySet()){
			if((text = map.get(key).textField.getText()) == null || text.equals("") || !text.matches("[\\d]{1,5}")){
				JOptionPane.showMessageDialog(null, map.get(key).label.getText()+"　只能輸入數字"
						,"",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			propMap.put(key, map.get(key).textField.getText());
		}
    	controller.updateProps(propMap);
		
		return true;
	}
	
	private class Components {
		protected JLabel label;
		protected JTextField textField;
		
		public Components(JLabel label, JTextField textField) {
			this.label = label;
			this.textField = textField;
		}
	}
}
