package traintransfer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;

public class WindowFilter extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 832658298022037317L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	JRadioButton onlyGDC;
	JRadioButton noGDC;
	JRadioButton allTrainType;
	JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			WindowFilter dialog = new WindowFilter();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	protected void exit() {
		System.exit(0);
	}
	public WindowFilter() {
		setTitle("\u9AD8\u7EA7\u641C\u7D22");
		setBounds(100, 100, 460, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JPanel contentPane = new JPanel();
			contentPane.setBounds(268, 21, 1, 1);
			contentPane.setLayout(null);
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPanel.add(contentPane);
			{
				JLabel lblNewLabel = new JLabel("\u706B\u8F66\u6362\u4E58\u65B9\u6848\u67E5\u8BE2");
				lblNewLabel.setForeground(Color.BLACK);
				lblNewLabel.setFont(new Font("黑体", Font.BOLD, 20));
				lblNewLabel.setBounds(215, 10, 172, 46);
				contentPane.add(lblNewLabel);
			}
			{
				textField_4 = new JTextField();
				textField_4.setFont(new Font("宋体", Font.PLAIN, 20));
				textField_4.setColumns(10);
				textField_4.setBounds(74, 72, 114, 38);
				contentPane.add(textField_4);
			}
			{
				textField_5 = new JTextField();
				textField_5.setFont(new Font("宋体", Font.PLAIN, 20));
				textField_5.setColumns(10);
				textField_5.setBounds(406, 72, 114, 38);
				contentPane.add(textField_5);
			}
			{
				JButton btnNewButton = new JButton("<==>");
				btnNewButton.setFont(new Font("宋体", Font.BOLD, 14));
				btnNewButton.setBounds(261, 72, 67, 38);
				contentPane.add(btnNewButton);
			}
			{
				JLabel lblNewLabel_1 = new JLabel("\u51FA\u53D1\u7AD9");
				lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 16));
				lblNewLabel_1.setBounds(74, 48, 48, 19);
				contentPane.add(lblNewLabel_1);
			}
			{
				JLabel lblNewLabel_1_1 = new JLabel("\u5230\u8FBE\u7AD9");
				lblNewLabel_1_1.setFont(new Font("宋体", Font.PLAIN, 16));
				lblNewLabel_1_1.setBounds(472, 51, 48, 19);
				contentPane.add(lblNewLabel_1_1);
			}
			{
				JLabel lblNewLabel_1_2 = new JLabel("\u7AD9");
				lblNewLabel_1_2.setFont(new Font("宋体", Font.PLAIN, 20));
				lblNewLabel_1_2.setBounds(192, 77, 25, 33);
				contentPane.add(lblNewLabel_1_2);
			}
			{
				JLabel lblNewLabel_1_2_1 = new JLabel("\u7AD9");
				lblNewLabel_1_2_1.setFont(new Font("宋体", Font.PLAIN, 20));
				lblNewLabel_1_2_1.setBounds(521, 77, 25, 33);
				contentPane.add(lblNewLabel_1_2_1);
			}
			{
				JLabel lblNewLabel_2 = new JLabel(
						"\u540C\u4E00\u8F66\u7AD9\u6700\u5C0F\u6362\u4E58\u65F6\u95F4\uFF08\u5206\u949F\uFF09\uFF1A");
				lblNewLabel_2.setFont(new Font("宋体", Font.PLAIN, 20));
				lblNewLabel_2.setBounds(47, 145, 380, 33);
				contentPane.add(lblNewLabel_2);
			}
			{
				JLabel lblNewLabel_2_1 = new JLabel(
						"\u540C\u4E00\u57CE\u5E02\u4E0D\u540C\u8F66\u7AD9\u6700\u5C0F\u6362\u4E58\u65F6\u95F4\uFF08\u5206\u949F\uFF09\uFF1A");
				lblNewLabel_2_1.setFont(new Font("宋体", Font.PLAIN, 20));
				lblNewLabel_2_1.setBounds(47, 188, 380, 33);
				contentPane.add(lblNewLabel_2_1);
			}
			{
				textField_6 = new JTextField();
				textField_6.setFont(new Font("宋体", Font.PLAIN, 20));
				textField_6.setColumns(10);
				textField_6.setBounds(447, 145, 99, 38);
				contentPane.add(textField_6);
			}
			{
				textField_7 = new JTextField();
				textField_7.setFont(new Font("宋体", Font.PLAIN, 20));
				textField_7.setColumns(10);
				textField_7.setBounds(447, 188, 99, 38);
				contentPane.add(textField_7);
			}
			{
				JLabel lblNewLabel_2_1_1 = new JLabel("\u9700\u8981\u907F\u5F00\u7684\u8F66\u7AD9");
				lblNewLabel_2_1_1.setFont(new Font("宋体", Font.PLAIN, 20));
				lblNewLabel_2_1_1.setBounds(47, 231, 380, 33);
				contentPane.add(lblNewLabel_2_1_1);
			}
		}
		{
			JPanel contentPane = new JPanel();
			contentPane.setBounds(274, 21, 1, 1);
			contentPane.setLayout(null);
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPanel.add(contentPane);
			{
				JLabel lblNewLabel = new JLabel("\u706B\u8F66\u6362\u4E58\u65B9\u6848\u67E5\u8BE2");
				lblNewLabel.setForeground(Color.BLACK);
				lblNewLabel.setFont(new Font("黑体", Font.BOLD, 20));
				lblNewLabel.setBounds(215, 10, 172, 46);
				contentPane.add(lblNewLabel);
			}
			{
				textField = new JTextField();
				textField.setFont(new Font("宋体", Font.PLAIN, 20));
				textField.setColumns(10);
				textField.setBounds(74, 72, 114, 38);
				contentPane.add(textField);
			}
			{
				textField_1 = new JTextField();
				textField_1.setFont(new Font("宋体", Font.PLAIN, 20));
				textField_1.setColumns(10);
				textField_1.setBounds(406, 72, 114, 38);
				contentPane.add(textField_1);
			}
			{
				JButton btnNewButton = new JButton("<==>");
				btnNewButton.setFont(new Font("宋体", Font.BOLD, 14));
				btnNewButton.setBounds(261, 72, 67, 38);
				contentPane.add(btnNewButton);
			}
			{
				JLabel lblNewLabel_1 = new JLabel("\u51FA\u53D1\u7AD9");
				lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 16));
				lblNewLabel_1.setBounds(74, 48, 48, 19);
				contentPane.add(lblNewLabel_1);
			}
			{
				JLabel lblNewLabel_1_1 = new JLabel("\u5230\u8FBE\u7AD9");
				lblNewLabel_1_1.setFont(new Font("宋体", Font.PLAIN, 16));
				lblNewLabel_1_1.setBounds(472, 51, 48, 19);
				contentPane.add(lblNewLabel_1_1);
			}
			{
				JLabel lblNewLabel_1_2 = new JLabel("\u7AD9");
				lblNewLabel_1_2.setFont(new Font("宋体", Font.PLAIN, 20));
				lblNewLabel_1_2.setBounds(192, 77, 25, 33);
				contentPane.add(lblNewLabel_1_2);
			}
			{
				JLabel lblNewLabel_1_2_1 = new JLabel("\u7AD9");
				lblNewLabel_1_2_1.setFont(new Font("宋体", Font.PLAIN, 20));
				lblNewLabel_1_2_1.setBounds(521, 77, 25, 33);
				contentPane.add(lblNewLabel_1_2_1);
			}
			{
				JLabel lblNewLabel_2 = new JLabel(
						"\u540C\u4E00\u8F66\u7AD9\u6700\u5C0F\u6362\u4E58\u65F6\u95F4\uFF08\u5206\u949F\uFF09\uFF1A");
				lblNewLabel_2.setFont(new Font("宋体", Font.PLAIN, 20));
				lblNewLabel_2.setBounds(47, 145, 380, 33);
				contentPane.add(lblNewLabel_2);
			}
			{
				JLabel lblNewLabel_2_1 = new JLabel(
						"\u540C\u4E00\u57CE\u5E02\u4E0D\u540C\u8F66\u7AD9\u6700\u5C0F\u6362\u4E58\u65F6\u95F4\uFF08\u5206\u949F\uFF09\uFF1A");
				lblNewLabel_2_1.setFont(new Font("宋体", Font.PLAIN, 20));
				lblNewLabel_2_1.setBounds(47, 188, 380, 33);
				contentPane.add(lblNewLabel_2_1);
			}
			{
				textField_2 = new JTextField();
				textField_2.setFont(new Font("宋体", Font.PLAIN, 20));
				textField_2.setColumns(10);
				textField_2.setBounds(447, 145, 99, 38);
				contentPane.add(textField_2);
			}
			{
				textField_3 = new JTextField();
				textField_3.setFont(new Font("宋体", Font.PLAIN, 20));
				textField_3.setColumns(10);
				textField_3.setBounds(447, 188, 99, 38);
				contentPane.add(textField_3);
			}
			{
				JLabel lblNewLabel_2_1_1 = new JLabel("\u9700\u8981\u907F\u5F00\u7684\u8F66\u7AD9");
				lblNewLabel_2_1_1.setFont(new Font("宋体", Font.PLAIN, 20));
				lblNewLabel_2_1_1.setBounds(47, 231, 380, 33);
				contentPane.add(lblNewLabel_2_1_1);
			}
		}
		{
			JLabel lblNewLabel_3 = new JLabel("\u8F66\u6B21\u7B5B\u9009");
			lblNewLabel_3.setFont(new Font("黑体", Font.PLAIN, 16));
			lblNewLabel_3.setBounds(17, 21, 121, 23);
			contentPanel.add(lblNewLabel_3);
		}
		{
			JLabel lblNewLabel_3 = new JLabel("\u4E2D\u8F6C\u7AD9\u7B5B\u9009");
			lblNewLabel_3.setFont(new Font("黑体", Font.PLAIN, 16));
			lblNewLabel_3.setBounds(17, 71, 121, 23);
			contentPanel.add(lblNewLabel_3);
		}
		{
			JLabel lblNewLabel_4 = new JLabel(
					"\u8F93\u5165\u8981\u907F\u5F00\u7684\u4E2D\u8F6C\u8F66\u7AD9\uFF0C\u4EE5\u7A7A\u683C\u5206\u5272\uFF0C\u4ECE\u4E0B\u5217\u8F66\u7AD9\u51FA\u53D1\u7684\u5217\u8F66\u5C06\u88AB\u6392\u9664");
			lblNewLabel_4.setBounds(17, 91, 398, 15);
			contentPanel.add(lblNewLabel_4);
		}

		JButton btnNewButton_1 = new JButton("\u4FDD\u5B58\u5F53\u524D\u8BBE\u7F6E");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFilterRule();
			}

			private void saveFilterRule() {
				try {
					File f = new File("./FilterRule.txt");
					FileWriter fw = new FileWriter(f);
					BufferedWriter bw = new BufferedWriter(fw);
					if (onlyGDC.isSelected()) {
						bw.write("1");
					} else if (noGDC.isSelected()) {
						bw.write("2");
					} else if (allTrainType.isSelected()) {
						bw.write("0");
					} else {
						bw.write("error");
					}
					bw.newLine();
					bw.write(textArea.getText());
					bw.close();
				} catch (IOException e) {
					WindowErrMsg wem = new WindowErrMsg();
					wem.textArea.setText("写入筛选规则时IO异常");
					wem.setVisible(true);
					return;
				}
			}
		});
		btnNewButton_1.setBounds(17, 198, 121, 23);
		contentPanel.add(btnNewButton_1);

		JButton btnNewButton_1_1 = new JButton("\u5BFC\u5165\u5DF2\u6709\u8BBE\u7F6E");
		btnNewButton_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importFilterRule();
			}

			private void importFilterRule() {
				try {
					BufferedReader br=new BufferedReader(new FileReader(new File("./FilterRule.txt")));
					String code=br.readLine();
					if(code.equals("1")) {
						onlyGDC.setSelected(true);
					}else if(code.equals("2")) {
						noGDC.setSelected(true);
					}else if(code.equals("0")) {
						allTrainType.setSelected(true);
					}else {
						WindowErrMsg wem = new WindowErrMsg();
						wem.textArea.setText("筛选规则文件储存格式错误");
						wem.setVisible(true);
					}
					String tmp,res="";
					while((tmp=br.readLine())!=null) {
						res+=tmp;
					}
					textArea.setText(res);
					br.close();
				} catch (IOException e) {
					WindowErrMsg wem = new WindowErrMsg();
					wem.textArea.setText("读取筛选规则时IO异常");
					wem.setVisible(true);
					return;
				} catch(Exception e) {
					WindowErrMsg wem = new WindowErrMsg();
					wem.textArea.setText("读取筛选规则时出现非IO异常");
					wem.setVisible(true);
					return;
				}
				
				
			}
		});
		btnNewButton_1_1.setBounds(305, 198, 121, 23);
		contentPanel.add(btnNewButton_1_1);

		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setTabSize(4);
		textArea.setLineWrap(true);
		textArea.setBounds(17, 108, 398, 80);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("OK");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(17, 108, 398, 80);
		scrollPane.setViewportView(textArea);
		contentPanel.add(scrollPane);
		ButtonGroup bg1 = new ButtonGroup();
		{
			JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("\u53EA\u770B\u9AD8\u94C1/\u52A8\u8F66");
			rdbtnNewRadioButton_2.setFont(new Font("宋体", Font.PLAIN, 14));
			rdbtnNewRadioButton_2.setBounds(17, 42, 121, 23);
			bg1.add(rdbtnNewRadioButton_2);
			onlyGDC = rdbtnNewRadioButton_2;
			contentPanel.add(rdbtnNewRadioButton_2);
		}
		{
			JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("\u4E0D\u9650\u8F66\u6B21");
			rdbtnNewRadioButton_2.setSelected(true);
			rdbtnNewRadioButton_2.setFont(new Font("宋体", Font.PLAIN, 14));
			rdbtnNewRadioButton_2.setBounds(165, 42, 91, 23);
			bg1.add(rdbtnNewRadioButton_2);
			allTrainType = rdbtnNewRadioButton_2;
			contentPanel.add(rdbtnNewRadioButton_2);
		}
		{
			JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("\u4E0D\u770B\u9AD8\u94C1/\u52A8\u8F66");
			rdbtnNewRadioButton_2.setFont(new Font("宋体", Font.PLAIN, 14));
			rdbtnNewRadioButton_2.setBounds(305, 42, 121, 23);
			bg1.add(rdbtnNewRadioButton_2);
			noGDC = rdbtnNewRadioButton_2;
			contentPanel.add(rdbtnNewRadioButton_2);
		}
	}
}
