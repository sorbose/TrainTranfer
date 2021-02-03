package traintransfer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTree;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

public class WindowUserSettings extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2780013945687516655L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	JCheckBox chckbxNewCheckBox;
	/**
	 * Launch the application.
	 */
	
	public String getStPath() {
		return textField.getText();
	}
	public String getSchedulePath() {
		return textField_1.getText();
	}
	public String getSameStPath() {
		return textField_2.getText();
	}
	private File chooseFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setBounds(0, 0, 0, 0);
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new FileNameExtensionFilter("file(.*txt, .*csv)","txt","csv"));
		if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fileChooser.getSelectedFile();
		return file;
		}
		
		return null;
		
	}
	
	public static void main(String[] args) {
		try {
			WindowUserSettings dialog = new WindowUserSettings();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 * @throws IOException 
	 */
	
	private void loadFilePath() {
		File dataPaths=new File("./dataPaths.txt");
		if(!dataPaths.exists()) return;
		BufferedReader br;
		try {
		br=new BufferedReader(new FileReader(dataPaths));
		textField.setText(br.readLine());
		textField_1.setText(br.readLine());
		textField_2.setText(br.readLine());
		br.close();
		}catch(IOException ex) {
			WindowErrMsg wem=new WindowErrMsg();
			wem.textArea.setText("dataPaths文件IO异常！！！");
			wem.setVisible(true);
		}
	}
	public void writeFilePath(String StPath,String SchPath,String sameStPath) throws IOException {
		BufferedWriter bw;
		bw=new BufferedWriter(new FileWriter("./dataPaths.txt"));
		bw.write(StPath);
		bw.newLine();
		bw.write(SchPath);
		bw.newLine();
		bw.write(sameStPath);
		bw.close();
	}
	protected void exit() {
		System.exit(0);
	}
	public WindowUserSettings() {
		setTitle("\u8BBE\u7F6E");
		setBounds(100, 100, 450, 293);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNewLabel = new JLabel("\u8BBE\u7F6E\u8F66\u7AD9\u4FE1\u606F\u6587\u4EF6\u8DEF\u5F84");
			lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 16));
			lblNewLabel.setBounds(10, 10, 294, 19);
			contentPanel.add(lblNewLabel);
		}
		{
			JLabel lblNewLabel = new JLabel("\u8BBE\u7F6E\u5217\u8F66\u65F6\u523B\u8868\u4FE1\u606F\u6587\u4EF6\u8DEF\u5F84");
			lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 16));
			lblNewLabel.setBounds(10, 72, 294, 19);
			contentPanel.add(lblNewLabel);
		}
		{
			JLabel lblNewLabel = new JLabel("\u8BBE\u7F6E\u540C\u57CE\u8F66\u7AD9\u6587\u4EF6\u8DEF\u5F84");
			lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 16));
			lblNewLabel.setBounds(10, 138, 294, 19);
			contentPanel.add(lblNewLabel);
		}
		
		textField = new JTextField();
		textField.setBounds(10, 28, 339, 33);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("\u6D4F\u89C8");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
				textField.setText(chooseFile().getAbsolutePath());
				}catch(NullPointerException ex) {}
			}
		});
		btnNewButton.setBounds(350, 28, 76, 33);
		contentPanel.add(btnNewButton);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(10, 95, 339, 33);
		contentPanel.add(textField_1);
		
		JButton btnNewButton_1 = new JButton("\u6D4F\u89C8");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
				textField_1.setText(chooseFile().getAbsolutePath());
				}catch(NullPointerException ex) {}
			}
		});
		btnNewButton_1.setBounds(350, 95, 76, 33);
		contentPanel.add(btnNewButton_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(10, 158, 339, 33);
		contentPanel.add(textField_2);
		
		JButton btnNewButton_2 = new JButton("\u6D4F\u89C8");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
				textField_2.setText(chooseFile().getAbsolutePath());
				}catch(NullPointerException ex) {}
			}
		});
		btnNewButton_2.setBounds(350, 158, 76, 33);
		contentPanel.add(btnNewButton_2);
		
		chckbxNewCheckBox = new JCheckBox("\u5C06\u67E5\u8BE2\u8BB0\u5F55\u4FDD\u5B58\u5728\u7A0B\u5E8F\u76EE\u5F55\u4E0B");
		chckbxNewCheckBox.setBounds(10, 197, 416, 23);
		contentPanel.add(chckbxNewCheckBox);
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
		loadFilePath();
	}
}
