package traintransfer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;

public class WindowBuilder extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5336708415599170185L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private WindowFilter windowFilter;
	private WindowUserSettings windowUserSettings;
	public RailNet railnet = null;
	private JTextField textField_4;
	private JTextPane resTextPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WindowBuilder frame = new WindowBuilder();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	protected void exit() {
		System.exit(0);
	}
	private void mainQuery() {
		String depStName = getDepStName();
		String arrStName = getArrStName();
		String depTime = getDepTime();
		int startTime = Trains.strTimeToInt(depTime, 0);
		if (startTime < 0 || startTime >= 1440) {
			popUpErrMsg("����ʱ�������ʽ���ԣ��밴hh:mm�ĸ�ʽ���룬����00:00,09:30,23:59����Ӣ��ð�ž���");
			return;
		}
		int sameStMinIntv, sameCityMinIntv;
		try {
			sameStMinIntv = getSameStMinIntv();
			sameCityMinIntv = getSameCityMinIntv();
		} catch (NumberFormatException ex) {
			popUpErrMsg("����ʱ����������ָ�ʽ���ԣ����������");
			return;
		}
		if (sameStMinIntv < 0 || sameCityMinIntv < 0) {
			popUpErrMsg("����ʱ�䲻��Ϊ������");
			return;
		}
		if (sameStMinIntv > sameCityMinIntv) {
			popUpErrMsg("��ͬվ����ʱ�䡿Ӧ��С�ڵ��ڡ�ͬ�ǲ�ͬ��վ�Ļ���ʱ�䡿");
			return;
		}
		String avoidTrainType = null;/* ���治ѡ��ĳ��� */
		if (windowFilter.onlyGDC.isSelected()) {
			avoidTrainType = "0KTZYS";/* ���ֳ���0��ʾ */
		} else if (windowFilter.noGDC.isSelected()) {
			avoidTrainType = "GDC";
		} else if (windowFilter.allTrainType.isSelected()) {
			avoidTrainType = null;
		} else {
			popUpErrMsg("δѡ�񳵴�����ɸѡ����");
			return;
		}
		String[] stNameToAvoid;
		stNameToAvoid = windowFilter.textArea.getText().trim().split(" ");
		if (railnet == null) {
			try {
				initRailNet();
			} catch (IOException e) {
				popUpErrMsg("����·�������ļ�IO�쳣�����顾���á��е������ļ�·���Ƿ���д��ȷ�������ļ��ĸ�ʽ�Ƿ����Ҫ��");
				e.printStackTrace();
				return;
			}
			try {
				windowUserSettings.writeFilePath(windowUserSettings.getStPath(), windowUserSettings.getSchedulePath(),
						windowUserSettings.getSameStPath());
			} catch (IOException e) {
				popUpErrMsg("д�������ļ�ʱIO�쳣");
				e.printStackTrace();
				return;
			}
		}
		DijkstraForRail dijk = new DijkstraForRail();
		int startV, arrV;
		try {
			startV = Stations.stationNameDic.get(depStName);
		} catch (NullPointerException e) {
			popUpErrMsg("δ�ҵ���" + depStName + "����վ�����鳵վ�����Ƿ�������ȷ");
			return;
		}
		try {
			arrV = Stations.stationNameDic.get(arrStName);
		} catch (NullPointerException e) {
			popUpErrMsg("δ�ҵ���" + arrStName + "����վ�����鳵վ�����Ƿ�������ȷ");
			return;
		}
		if (stNameToAvoid[0].trim().equals("")) {
			stNameToAvoid[0] = "#";
		}
		try {
			railnet.avoidStations(stNameToAvoid);
		} catch (NullPointerException e) {
			popUpErrMsg("����Ҫ�ܿ��ĳ�վ��վ�������д�������ϸ��飬��ͬ��վ֮����һ����ǿո�ָ��Ҫ�ӡ�վ����");
			return;
		}
		dijk.fastestArrival(railnet, startV, startTime, sameStMinIntv, sameCityMinIntv, avoidTrainType);

		String outputRes = "";
		outputRes += "����1����쵽��\n";
		String[] pathDescStr = Inquirer.getPathDesc(dijk.parTrainId, dijk.parent, arrV);
		for (int ii = 0; ii < pathDescStr.length; ii++) {
			outputRes += pathDescStr[ii];
			outputRes += "\n";
		}
		outputRes += (Trains.intTimeToStr(dijk.dist[arrV] - dijk.dist[startV] - sameCityMinIntv));
		resTextPane.setText(outputRes);
		if (windowUserSettings.chckbxNewCheckBox.isSelected()) {
			saveQueryRecord(outputRes);
		}
		railnet.restoreAvoidStations(stNameToAvoid);/* �Ȼ�ԭ��ʱ�����õĶ��㣬�ڸ���Ȩ�أ���Ȼ��© */
		Trains.restoreWeight(railnet);
	}

	private void saveQueryRecord(String outPutRes) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./QueryRecord.txt"), true));
			bw.newLine();
			bw.write("depSt:" + getDepStName());
			bw.newLine();
			bw.write("arrSt:" + getArrStName());
			bw.newLine();
			bw.write("depTime:" + getDepTime());
			bw.newLine();
			bw.write("��Сͬվ���˼��" + getSameStMinIntv());
			bw.newLine();
			bw.write("��Сͬ�ǻ��˼��:" + getSameCityMinIntv());
			bw.newLine();
			if (windowFilter.onlyGDC.isSelected()) {
				bw.write("ֻ������");
			} else if (windowFilter.noGDC.isSelected()) {
				bw.write("��������");
			} else {
				bw.write("���޳���");
			}
			bw.newLine();
			bw.write("�������³�վ����:");
			bw.newLine();
			bw.write(windowFilter.textArea.getText());
			bw.newLine();
			bw.write(outPutRes);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			popUpErrMsg("�����ѯ��¼ʱIO����");
		}
	}

	private void initRailNet() throws IOException {
		railnet = new RailNet(windowUserSettings.getStPath(), windowUserSettings.getSchedulePath(),
				windowUserSettings.getSameStPath());
	}

	private void popUpErrMsg(String errMsg) {
		WindowErrMsg wem = new WindowErrMsg();
		wem.textArea.setText(errMsg);
		wem.setVisible(true);
	}

	private String getDepStName() {
		String res = null;
		res = textField.getText();
		return res;
	}

	private String getArrStName() {
		String res = null;
		res = textField_1.getText();
		return res;
	}

	private String getDepTime() {
		String res = null;
		res = textField_4.getText();
		return res;

	}

	private int getSameStMinIntv() throws NumberFormatException {
		int res;
		res = Integer.parseInt(textField_2.getText());
		return res;
	}

	private int getSameCityMinIntv() throws NumberFormatException {
		int res;
		res = Integer.parseInt(textField_3.getText());
		return res;
	}

	/**
	 * Create the frame.
	 */
	public WindowBuilder() {
		windowFilter = new WindowFilter();
		windowFilter.setVisible(false);
		windowUserSettings = new WindowUserSettings();
		windowUserSettings.setVisible(false);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				windowFilter.dispose();
				windowFilter.exit();
				windowUserSettings.dispose();
				windowUserSettings.exit();
				System.exit(0);
			}
		});
		setTitle("Train Transfer \u706B\u8F66\u6362\u4E58\u65B9\u6848\u67E5\u8BE2");
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 612, 497);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("\u706B\u8F66\u6362\u4E58\u65B9\u6848\u67E5\u8BE2");
		lblNewLabel.setForeground(Color.BLACK);
		lblNewLabel.setFont(new Font("����", Font.BOLD, 20));
		lblNewLabel.setBounds(215, 10, 172, 46);
		contentPane.add(lblNewLabel);

		textField = new JTextField();
		textField.setFont(new Font("����", Font.PLAIN, 20));
		textField.setBounds(74, 72, 114, 38);
		contentPane.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setFont(new Font("����", Font.PLAIN, 20));
		textField_1.setColumns(10);
		textField_1.setBounds(406, 72, 114, 38);
		contentPane.add(textField_1);

		JButton btnNewButton = new JButton("<==>");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tmp = textField.getText();
				textField.setText(textField_1.getText());
				textField_1.setText(tmp);
			}
		});
		btnNewButton.setFont(new Font("����", Font.BOLD, 14));
		btnNewButton.setBounds(261, 72, 67, 38);
		contentPane.add(btnNewButton);

		JLabel lblNewLabel_1 = new JLabel("\u51FA\u53D1\u7AD9");
		lblNewLabel_1.setFont(new Font("����", Font.PLAIN, 16));
		lblNewLabel_1.setBounds(74, 48, 48, 19);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_1_1 = new JLabel("\u5230\u8FBE\u7AD9");
		lblNewLabel_1_1.setFont(new Font("����", Font.PLAIN, 16));
		lblNewLabel_1_1.setBounds(472, 51, 48, 19);
		contentPane.add(lblNewLabel_1_1);

		JLabel lblNewLabel_1_2 = new JLabel("\u7AD9");
		lblNewLabel_1_2.setFont(new Font("����", Font.PLAIN, 20));
		lblNewLabel_1_2.setBounds(192, 77, 25, 33);
		contentPane.add(lblNewLabel_1_2);

		JLabel lblNewLabel_1_2_1 = new JLabel("\u7AD9");
		lblNewLabel_1_2_1.setFont(new Font("����", Font.PLAIN, 20));
		lblNewLabel_1_2_1.setBounds(521, 77, 25, 33);
		contentPane.add(lblNewLabel_1_2_1);

		JLabel lblNewLabel_2 = new JLabel(
				"\u540C\u4E00\u8F66\u7AD9\u6700\u5C0F\u6362\u4E58\u65F6\u95F4\uFF08\u5206\u949F\uFF09\uFF1A");
		lblNewLabel_2.setFont(new Font("����", Font.PLAIN, 16));
		lblNewLabel_2.setBounds(74, 149, 334, 33);
		contentPane.add(lblNewLabel_2);

		JLabel lblNewLabel_2_1 = new JLabel(
				"\u540C\u4E00\u57CE\u5E02\u4E0D\u540C\u8F66\u7AD9\u6700\u5C0F\u6362\u4E58\u65F6\u95F4\uFF08\u5206\u949F\uFF09\uFF1A");
		lblNewLabel_2_1.setFont(new Font("����", Font.PLAIN, 16));
		lblNewLabel_2_1.setBounds(74, 182, 334, 33);
		contentPane.add(lblNewLabel_2_1);

		textField_2 = new JTextField();
		textField_2.setFont(new Font("����", Font.PLAIN, 16));
		textField_2.setColumns(10);
		textField_2.setBounds(447, 153, 73, 26);
		contentPane.add(textField_2);

		textField_3 = new JTextField();
		textField_3.setFont(new Font("����", Font.PLAIN, 16));
		textField_3.setColumns(10);
		textField_3.setBounds(447, 186, 73, 26);
		contentPane.add(textField_3);

		JLabel lblNewLabel_3 = new JLabel("\u6362\u4E58\u65B9\u6848");
		lblNewLabel_3.setFont(new Font("����", Font.PLAIN, 16));
		lblNewLabel_3.setBounds(47, 259, 86, 26);
		contentPane.add(lblNewLabel_3);

		JTextPane txtpnwwaaaaaaaaaaaaaaaaaaaa = new JTextPane();
		txtpnwwaaaaaaaaaaaaaaaaaaaa.setFont(new Font("����", Font.PLAIN, 14));
		txtpnwwaaaaaaaaaaaaaaaaaaaa.setEditable(false);
		txtpnwwaaaaaaaaaaaaaaaaaaaa.setBounds(46, 289, 498, 160);
		resTextPane = txtpnwwaaaaaaaaaaaaaaaaaaaa;

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(46, 289, 498, 160);
		scrollPane.setViewportView(txtpnwwaaaaaaaaaaaaaaaaaaaa);
		contentPane.add(scrollPane);

		JButton btnNewButton_1 = new JButton("\u67E5\u627E");
		btnNewButton_1.setFont(new Font("����", Font.BOLD, 20));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainQuery();
			}
		});
		btnNewButton_1.setBounds(231, 213, 140, 38);
		contentPane.add(btnNewButton_1);

		JButton btnNewButton_1_1 = new JButton("\u8BBE\u7F6E");
		btnNewButton_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowUserSettings.setVisible(true);
			}
		});
		btnNewButton_1_1.setFont(new Font("����", Font.PLAIN, 14));
		btnNewButton_1_1.setBounds(440, 259, 104, 26);
		contentPane.add(btnNewButton_1_1);

		JButton btnNewButton_1_1_1 = new JButton("\u9AD8\u7EA7\u67E5\u627E");
		btnNewButton_1_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowFilter.setVisible(true);
			}
		});
		btnNewButton_1_1_1.setFont(new Font("����", Font.PLAIN, 14));
		btnNewButton_1_1_1.setBounds(322, 259, 104, 26);
		contentPane.add(btnNewButton_1_1_1);

		JLabel lblNewLabel_2_2 = new JLabel("\u51FA\u53D1\u65F6\u95F4\uFF08hh:mm\uFF09");
		lblNewLabel_2_2.setFont(new Font("����", Font.PLAIN, 16));
		lblNewLabel_2_2.setBounds(74, 120, 297, 33);
		contentPane.add(lblNewLabel_2_2);

		textField_4 = new JTextField();
		textField_4.setFont(new Font("����", Font.PLAIN, 16));
		textField_4.setColumns(10);
		textField_4.setBounds(447, 120, 73, 26);
		contentPane.add(textField_4);

	}
}
