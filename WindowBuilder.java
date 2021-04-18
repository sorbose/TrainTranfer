package traintransfer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

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
	private JRadioButton isLastestArrTimeRadioBtn;
	private JRadioButton isEarliestDepTimeRadioBtn;
	private JRadioButton isTransRadioBtn;
	private JRadioButton isS2SRadioBtn;
	private JRadioButton isStaRadioBtn;
	private JCheckBox isFuzzyArrCheckBox;
	private JCheckBox isFuzzyDepCheckBox;

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

	private void disableSomeControls() {
		textField_2.setEnabled(false);
		textField_3.setEnabled(false);
		textField_4.setEnabled(false);
		isLastestArrTimeRadioBtn.setEnabled(false);
		isEarliestDepTimeRadioBtn.setEnabled(false);
	}

	private void enableSomeControls() {
		textField_2.setEnabled(true);
		textField_3.setEnabled(true);
		textField_4.setEnabled(true);
		isLastestArrTimeRadioBtn.setEnabled(true);
		isEarliestDepTimeRadioBtn.setEnabled(true);
	}

	private String getAvoidTrainType() {
		String avoidTrainType;
		if (windowFilter.onlyGDC.isSelected()) {
			avoidTrainType = "0KTZYS";/* 数字车用0表示 */
		} else if (windowFilter.noGDC.isSelected()) {
			avoidTrainType = "GDC";
		} else if (windowFilter.allTrainType.isSelected()) {
			avoidTrainType = null;
		} else {
			popUpErrMsg("未选择车次类型筛选条件");
			avoidTrainType = "error";
		}
		return avoidTrainType;
	}

	private int getVertexId(String stName) {
		int vId;
		try {
			vId = Stations.stationNameDic.get(stName);
		} catch (NullPointerException e) {
			popUpErrMsg("未找到【" + stName + "】车站，请检查车站名称是否输入正确");
			return -404;/* 表示未找到的错误代码 */
		}
		return vId;
	}

	private void mainQuery() {
		String depStName = getDepStName();
		String arrStName = getArrStName();
		String depTime = getDepTime();
		String outputRes = "";
		int startTime = Trains.strTimeToInt(depTime, 0);
		if (startTime < 0 || startTime >= 1440) {
			popUpErrMsg("出发时间输入格式不对，请按hh:mm的格式输入，例如00:00,09:30,23:59，中英文冒号均可");
			return;
		}
		int sameStMinIntv, sameCityMinIntv;
		try {
			sameStMinIntv = getSameStMinIntv();
			sameCityMinIntv = getSameCityMinIntv();
		} catch (NumberFormatException ex) {
			popUpErrMsg("换乘时间输入的数字格式不对！请检查后重试");
			return;
		}
		if (sameStMinIntv < 0 || sameCityMinIntv < 0) {
			popUpErrMsg("换乘时间不能为负数！");
			return;
		}
		if (sameStMinIntv > sameCityMinIntv) {
			popUpErrMsg("【同站换乘时间】应当小于等于【同城不同车站的换乘时间】");
			return;
		}
		String avoidTrainType = null;/* 储存不选择的车次 */
		avoidTrainType = getAvoidTrainType();
		if (avoidTrainType != null && avoidTrainType.equals("error"))
			return;
		if (avoidTrainType == null)
			outputRes += "不限车次\n";
		else if (avoidTrainType.equals("GDC")) {
			outputRes += "不看高铁/动车\n";
		} else if (avoidTrainType.equals("0KTZYS")) {
			outputRes += "只看高铁/动车\n";
		}

		String[] stNameToAvoid;
		stNameToAvoid = windowFilter.textArea.getText().trim().split(" ");
		if (railnet == null) {
			if (!createRailNet())
				return;
		}
		DijkstraForRail dijk = new DijkstraForRail();
		int startV, arrV;
		startV = getVertexId(depStName);
		arrV = getVertexId(arrStName);
		if (startV == -404 || arrV == -404)
			return;
		if (stNameToAvoid[0].trim().equals("")) {
			stNameToAvoid[0] = "#";
		}

		boolean isFuzzyDepCheckBoxSelected = isFuzzyDepCheckBox.isSelected();
		boolean isFuzzyArrCheckBoxSelected = isFuzzyArrCheckBox.isSelected();
		boolean isEarliestDepTimeRadioBtnSelected = isEarliestDepTimeRadioBtn.isSelected();
		boolean isLastestArrTimeRadioBtnSelected = isLastestArrTimeRadioBtn.isSelected();
		if (isLastestArrTimeRadioBtnSelected)
			railnet.setInOrOutAdjList(false);
		try {
			railnet.avoidStations(stNameToAvoid);
		} catch (NullPointerException e) {
			popUpErrMsg("【需要避开的车站】站名输入有错误，请仔细检查，不同车站之间以一个半角空格分割，不要加“站”字");
			return;
		}

		if (isEarliestDepTimeRadioBtnSelected) {
//			railnet.setInOrOutAdjList(true);
			dijk.fastestArrival(railnet, startV, startTime, sameStMinIntv, sameCityMinIntv, avoidTrainType,
					isFuzzyDepCheckBoxSelected, isFuzzyArrCheckBoxSelected, arrV);
		} else if (isLastestArrTimeRadioBtnSelected) {
			int arrTime = startTime;/* 因为他们用的都是同一个文本框，这里借用同样的变量 */
			dijk.lastestDeparture(railnet, arrV, arrTime, sameStMinIntv, sameCityMinIntv, avoidTrainType,
					isFuzzyDepCheckBoxSelected, isFuzzyArrCheckBoxSelected, startV);
		}

		outputRes += "方案1：最快到达\n";
		String[] pathDescStr;

		if (isEarliestDepTimeRadioBtnSelected) {
			pathDescStr = Inquirer.getPathDesc(dijk.parTrainId, dijk.parent, arrV, false);
			outputRes += (Trains.intTimeToStr(dijk.dist[arrV] - dijk.dist[startV] - sameStMinIntv) + "\n");
		} else {
			pathDescStr = Inquirer.getPathDesc(dijk.chiTrainId, dijk.child, startV, true);
			railnet.restoreAvoidStations(stNameToAvoid);
			outputRes += (Trains.intTimeToStr(dijk.dist[startV]) + "\n");
			Trains.restoreWeight(railnet);
			railnet.setInOrOutAdjList(true);
		}
		for (int ii = 0; ii < pathDescStr.length; ii++) {
			outputRes += pathDescStr[ii];
			outputRes += "\n";
		}
//		if (isFuzzyArrCheckBoxSelected) {
//			outputRes += (Trains.intTimeToStr(dijk.dist[arrV] - dijk.dist[startV] - sameStMinIntv));
//		} else {
//			outputRes += (Trains.intTimeToStr(dijk.dist[arrV] - dijk.dist[startV] - sameStMinIntv));
//		}

		outputRes += "\n\n";

//		if (isFuzzyArrCheckBox.isSelected()) {
//			for (int sameArrStId : ReadStations.stationsArr[arrV].sameStationsId) {
//				pathDescStr = Inquirer.getPathDesc(dijk.parTrainId, dijk.parent, sameArrStId);
//				for (int ii = 0; ii < pathDescStr.length; ii++) {
//					outputRes += pathDescStr[ii];
//					outputRes += "\n";
//				}
//				outputRes += (Trains.intTimeToStr(dijk.dist[sameArrStId] - dijk.dist[startV] - sameStMinIntv));
//				outputRes+="\n";
//			}
//		}
		resTextPane.setText(outputRes);
		if (windowUserSettings.chckbxNewCheckBox.isSelected()) {
			saveQueryRecord(outputRes);
		}

		outputRes += "\n\n方案二：最少换乘\n";
		BFS bfs = new BFS();
		bfs.bfs(railnet, startV, arrV, 10, 6, avoidTrainType,
				isFuzzyDepCheckBoxSelected,isFuzzyArrCheckBoxSelected);
		int[] parent = bfs.parent.clone();
		int[] parTrainId = bfs.parTrainId.clone();
		String[][] BFSRess = readBFSRes(bfs.bfsRes, parent, parTrainId);
		if (BFSRess != null) {
			for (int i = 0; i < BFSRess.length; i++) {
				for (int j = 0; j < BFSRess[i].length; j++) {
					outputRes += BFSRess[i][j];
					outputRes += "\n";
				}
				outputRes += "\n";
			}
		}
		resTextPane.setText(outputRes);

		railnet.restoreAvoidStations(stNameToAvoid);/* 先还原暂时被禁用的顶点，在更新权重，不然会漏 */
		Trains.restoreWeight(railnet);
	}

	private String[][] readBFSRes(BFSStation bfsRes, int[] parent, int[] parTrainId) {
		int arrV;
		String[][] paths = null;
		int bfsResCnt = 0;
		BFSStation bfsRes0 = bfsRes;
		while (bfsRes != null) {
			bfsResCnt++;
			bfsRes = bfsRes.next;
		}
		bfsRes = bfsRes0;
		paths = new String[bfsResCnt][];
		for (int i = paths.length-1; i >=0; i--) {
			arrV=bfsRes.arrV;
			parent[arrV] = bfsRes.vId;
			parTrainId[arrV] = bfsRes.trainCodeId;
			paths[i] = Inquirer.getPathDesc(parTrainId, parent, arrV, false);
			bfsRes = bfsRes.next;
		}
		return paths;
	}

	private void s2SQuery() {
		String outputRes = "";
		String depStName = getDepStName();
		String arrStName = getArrStName();
		String avoidTrainType = getAvoidTrainType();

		if (avoidTrainType != null && avoidTrainType.equals("error"))
			return;

		if (avoidTrainType == null)
			outputRes += "不限车次\n";
		else if (avoidTrainType.equals("GDC")) {
			outputRes += "不看高铁/动车\n";
		} else if (avoidTrainType.equals("0KTZYS")) {
			outputRes += "只看高铁/动车\n";
		}
		if (railnet == null) {
			if (!createRailNet())
				return;
		}
		int startV, arrV;
		int[] startVs, arrVs;
		startV = getVertexId(depStName);
		arrV = getVertexId(arrStName);
		if (isFuzzyDepCheckBox.isSelected()) {
			startVs = getSameStArray(startV);
		} else {
			startVs = new int[1];
			startVs[0] = startV;
		}
		if (isFuzzyArrCheckBox.isSelected()) {
			arrVs = getSameStArray(arrV);
		} else {
			arrVs = new int[1];
			arrVs[0] = arrV;
		}
		boolean isEmptyRes = true;

		for (int i = 0; i < startVs.length; i++) {
			startV = startVs[i];
			if (startV == -404)
				continue;
			for (int j = 0; j < arrVs.length; j++) {
				arrV = arrVs[j];
				if (arrV == -404)
					continue;
				Trains train = railnet.getTrainsBetweenTwoStas(startV, arrV, avoidTrainType);
				for (; train != null; train = (Trains) train.nextEdge) {
					outputRes += (Trains.getATrainBasicDescInf(train.trainCodeId, ReadStations.stationsArr[startV].name,
							ReadStations.stationsArr[arrV].name) + "\n");
					isEmptyRes = false;
				}
			}
		}
		if (isEmptyRes)
			outputRes += "两站间无直达车次！\n";
		resTextPane.setText(outputRes);
		/* 对于此函数以下两句没有必要 */
//		railnet.restoreAvoidStations(stNameToAvoid);/* 先还原暂时被禁用的顶点，在更新权重，不然会漏 */
//		Trains.restoreWeight(railnet);
	}

	private int[] getSameStArray(int vId) {
		int[] res;
		int[] tmpVs = ReadStations.stationsArr[vId].sameStationsId;
		res = new int[tmpVs.length + 1];
		res[0] = vId;
		for (int i = 1; i <= tmpVs.length; i++) {
			res[i] = tmpVs[i - 1];
		}
		return res;
	}

	private void aStationQuery() {
		String outputRes = "";
		String stationName = getDepStName();/* 获取第一个文本框的站名 */

		String avoidTrainType = getAvoidTrainType();

		if (avoidTrainType != null && avoidTrainType.equals("error"))
			return;

		if (avoidTrainType == null)
			outputRes += "不限车次\n";
		else if (avoidTrainType.equals("GDC")) {
			outputRes += "不看高铁/动车\n";
		} else if (avoidTrainType.equals("0KTZYS")) {
			outputRes += "只看高铁/动车\n";
		}
		if (railnet == null) {
			if (!createRailNet())
				return;
		}
		int staV;
		int[] staVs;
		staV = getVertexId(stationName);
		if (isFuzzyDepCheckBox.isSelected()) {
			staVs = getSameStArray(staV);
		} else {
			staVs = new int[1];
			staVs[0] = staV;
		}
		int resDepCnt = 0, resArrCnt = 0;
		railnet.setInOrOutAdjList(true);
		outputRes += "\n【出发】\n";
		for (int v : staVs) {
			if (v == -404)
				continue;
			Trains train = (Trains) railnet.v[v].E;
			for (; train != null; train = (Trains) train.nextEdge) {
				if (!RailNet.isTheTypeOfTrainSuitable(train, avoidTrainType))
					continue;
				outputRes += (Trains.getATrainBasicDescInf(train.trainCodeId, ReadStations.stationsArr[v].name,
						ReadStations.stationsArr[train.arrStId].name) + "\n");
				resDepCnt++;
			}
			outputRes += "\n";
		}
		outputRes += "\n【到达】\n";
		railnet.setInOrOutAdjList(false);
		for (int v : staVs) {
			if (v == -404)
				continue;
			Trains train = (Trains) railnet.v[v].E;
			for (; train != null; train = (Trains) train.nextEdge) {
				if (!RailNet.isTheTypeOfTrainSuitable(train, avoidTrainType))
					continue;
				outputRes += (Trains.getATrainBasicDescInf(train.trainCodeId,
						ReadStations.stationsArr[train.depStId].name, ReadStations.stationsArr[v].name) + "\n");
				resArrCnt++;
			}
			outputRes += "\n";
		}
		railnet.setInOrOutAdjList(true);

		outputRes += ("共查询到 " + resDepCnt + " 条出发结果，" + resArrCnt + " 条到达结果\n");
		resTextPane.setText(outputRes);

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
			bw.write("最小同站换乘间隔" + getSameStMinIntv());
			bw.newLine();
			bw.write("最小同城换乘间隔:" + getSameCityMinIntv());
			bw.newLine();
			if (windowFilter.onlyGDC.isSelected()) {
				bw.write("只看高铁");
			} else if (windowFilter.noGDC.isSelected()) {
				bw.write("不看高铁");
			} else {
				bw.write("不限车次");
			}
			bw.newLine();
			bw.write("不在以下车站出发:");
			bw.newLine();
			bw.write(windowFilter.textArea.getText());
			bw.newLine();
			bw.write(outPutRes);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			popUpErrMsg("保存查询记录时IO错误");
		}
	}

	private void initRailNet() throws IOException {
		railnet = new RailNet(windowUserSettings.getStPath(), windowUserSettings.getSchedulePath(),
				windowUserSettings.getSameStPath());
	}

	private boolean createRailNet() {
		try {
			initRailNet();
		} catch (IOException e) {
			popUpErrMsg("导入路网数据文件IO异常！请检查【设置】中的数据文件路径是否填写正确，数据文件的格式是否符合要求");
			e.printStackTrace();
			return false;
		}
		try {
			windowUserSettings.writeFilePath(windowUserSettings.getStPath(), windowUserSettings.getSchedulePath(),
					windowUserSettings.getSameStPath());
		} catch (IOException e) {
			popUpErrMsg("写入配置文件时IO异常");
			e.printStackTrace();
			return false;
		}
		return true;
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
		setBounds(100, 100, 709, 556);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(224, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("\u706B\u8F66\u6362\u4E58\u65B9\u6848\u67E5\u8BE2");
		lblNewLabel.setForeground(Color.BLACK);
		lblNewLabel.setFont(new Font("黑体", Font.BOLD, 20));
		lblNewLabel.setBounds(215, 10, 172, 46);
		contentPane.add(lblNewLabel);

		textField = new JTextField();
		textField.setText("\u6E58\u6F6D");
		textField.setFont(new Font("宋体", Font.PLAIN, 20));
		textField.setBounds(74, 72, 114, 38);
		contentPane.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setText("\u6E58\u6F6D");
		textField_1.setFont(new Font("宋体", Font.PLAIN, 20));
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
		btnNewButton.setFont(new Font("宋体", Font.BOLD, 14));
		btnNewButton.setBounds(261, 72, 67, 38);
		contentPane.add(btnNewButton);

		JLabel lblNewLabel_1 = new JLabel("\u51FA\u53D1\u7AD9");
		lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 16));
		lblNewLabel_1.setBounds(74, 48, 48, 19);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_1_1 = new JLabel("\u5230\u8FBE\u7AD9");
		lblNewLabel_1_1.setFont(new Font("宋体", Font.PLAIN, 16));
		lblNewLabel_1_1.setBounds(472, 51, 48, 19);
		contentPane.add(lblNewLabel_1_1);

		JLabel lblNewLabel_1_2 = new JLabel("\u7AD9");
		lblNewLabel_1_2.setFont(new Font("宋体", Font.PLAIN, 20));
		lblNewLabel_1_2.setBounds(192, 77, 25, 33);
		contentPane.add(lblNewLabel_1_2);

		JLabel lblNewLabel_1_2_1 = new JLabel("\u7AD9");
		lblNewLabel_1_2_1.setFont(new Font("宋体", Font.PLAIN, 20));
		lblNewLabel_1_2_1.setBounds(521, 77, 25, 33);
		contentPane.add(lblNewLabel_1_2_1);

		JLabel lblNewLabel_2 = new JLabel(
				"\u540C\u4E00\u8F66\u7AD9\u6700\u5C0F\u6362\u4E58\u65F6\u95F4\uFF08\u5206\u949F\uFF09\uFF1A");
		lblNewLabel_2.setFont(new Font("宋体", Font.PLAIN, 16));
		lblNewLabel_2.setBounds(74, 149, 334, 33);
		contentPane.add(lblNewLabel_2);

		JLabel lblNewLabel_2_1 = new JLabel(
				"\u540C\u4E00\u57CE\u5E02\u4E0D\u540C\u8F66\u7AD9\u6700\u5C0F\u6362\u4E58\u65F6\u95F4\uFF08\u5206\u949F\uFF09\uFF1A");
		lblNewLabel_2_1.setFont(new Font("宋体", Font.PLAIN, 16));
		lblNewLabel_2_1.setBounds(74, 182, 334, 33);
		contentPane.add(lblNewLabel_2_1);

		textField_2 = new JTextField();
		textField_2.setText("40");
		textField_2.setFont(new Font("宋体", Font.PLAIN, 16));
		textField_2.setColumns(10);
		textField_2.setBounds(447, 153, 73, 26);
		contentPane.add(textField_2);

		textField_3 = new JTextField();
		textField_3.setText("90");
		textField_3.setFont(new Font("宋体", Font.PLAIN, 16));
		textField_3.setColumns(10);
		textField_3.setBounds(447, 186, 73, 26);
		contentPane.add(textField_3);

		JLabel lblNewLabel_3 = new JLabel("\u6362\u4E58\u65B9\u6848");
		lblNewLabel_3.setFont(new Font("黑体", Font.PLAIN, 16));
		lblNewLabel_3.setBounds(47, 259, 219, 26);
		contentPane.add(lblNewLabel_3);

		JTextPane outPutResText = new JTextPane();
		outPutResText.setText("\u7AD9\u7AD9\u67E5\u8BE2\u5230\u7AD9\u529F\u80FD\u5F85\u53CD\u5411\u56FE\u5EFA\u597D");
		outPutResText.setFont(new Font("宋体", Font.PLAIN, 15));
		outPutResText.setEditable(false);
		outPutResText.setBounds(46, 289, 626, 200);
		resTextPane = outPutResText;

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(46, 289, 626, 200);
		scrollPane.setViewportView(outPutResText);
		contentPane.add(scrollPane);

		JButton btnNewButton_1 = new JButton("\u67E5\u8BE2");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				btnNewButton_1.setBackground(new Color(175, 238, 238));
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				btnNewButton_1.setBackground(new Color(111, 222, 255));
			}
		});
		btnNewButton_1.setBackground(new Color(175, 238, 238));
		
		btnNewButton_1.setFont(new Font("黑体", Font.BOLD, 20));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isTransRadioBtn.isSelected())
					mainQuery();
				else if (isS2SRadioBtn.isSelected())
					s2SQuery();
				else if (isStaRadioBtn.isSelected())
					aStationQuery();
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
		btnNewButton_1_1.setFont(new Font("宋体", Font.PLAIN, 14));
		btnNewButton_1_1.setBounds(571, 260, 101, 26);
		contentPane.add(btnNewButton_1_1);

		JButton btnNewButton_1_1_1 = new JButton("\u9AD8\u7EA7\u67E5\u8BE2");
		btnNewButton_1_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowFilter.setVisible(true);
			}
		});
		btnNewButton_1_1_1.setFont(new Font("宋体", Font.PLAIN, 14));
		btnNewButton_1_1_1.setBounds(460, 260, 101, 26);
		contentPane.add(btnNewButton_1_1_1);

		JLabel lblNewLabel_2_2 = new JLabel("\u6700\u65E9\u51FA\u53D1\u65F6\u95F4\uFF08hh:mm\uFF09");
		lblNewLabel_2_2.setFont(new Font("宋体", Font.PLAIN, 16));
		lblNewLabel_2_2.setBounds(74, 120, 297, 33);
		contentPane.add(lblNewLabel_2_2);

		textField_4 = new JTextField();
		textField_4.setText("07:00");
		textField_4.setFont(new Font("宋体", Font.PLAIN, 16));
		textField_4.setColumns(10);
		textField_4.setBounds(447, 120, 73, 26);
		contentPane.add(textField_4);

		isFuzzyDepCheckBox = new JCheckBox("\u6A21\u7CCA\u53D1\u7AD9");
		isFuzzyDepCheckBox.setBackground(new Color(224, 255, 255));
		isFuzzyDepCheckBox.setSelected(true);
		isFuzzyDepCheckBox.setFont(new Font("宋体", Font.PLAIN, 16));
		isFuzzyDepCheckBox.setBounds(552, 41, 122, 33);
		contentPane.add(isFuzzyDepCheckBox);

		isFuzzyArrCheckBox = new JCheckBox("\u6A21\u7CCA\u5230\u7AD9");
		isFuzzyArrCheckBox.setBackground(new Color(224, 255, 255));
		isFuzzyArrCheckBox.setSelected(true);
		isFuzzyArrCheckBox.setFont(new Font("宋体", Font.PLAIN, 16));
		isFuzzyArrCheckBox.setBounds(552, 72, 122, 26);
		contentPane.add(isFuzzyArrCheckBox);

		ButtonGroup bg_QueryTimeMode = new ButtonGroup();

		isEarliestDepTimeRadioBtn = new JRadioButton("\u6307\u5B9A\u51FA\u53D1\u65F6\u95F4");
		isEarliestDepTimeRadioBtn.setBackground(new Color(224, 255, 255));
		isEarliestDepTimeRadioBtn.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				lblNewLabel_2_2.setText("最早出发时间 (hh:mm)");
			}
		});
		isEarliestDepTimeRadioBtn.setSelected(true);
		isEarliestDepTimeRadioBtn.setFont(new Font("宋体", Font.PLAIN, 16));
		isEarliestDepTimeRadioBtn.setBounds(552, 197, 121, 23);
		contentPane.add(isEarliestDepTimeRadioBtn);
		bg_QueryTimeMode.add(isEarliestDepTimeRadioBtn);

		ButtonGroup bg_QueryStaMode = new ButtonGroup();
		isTransRadioBtn = new JRadioButton("\u6362\u4E58\u67E5\u8BE2");
		isTransRadioBtn.setBackground(new Color(224, 255, 255));
		isTransRadioBtn.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				enableSomeControls();
				textField_1.setEnabled(true);
				isFuzzyArrCheckBox.setEnabled(true);
				isFuzzyDepCheckBox.setText("模糊发站");
				lblNewLabel_3.setText("换乘方案");
				lblNewLabel_1.setText("出发站");
				windowFilter.textArea.setEnabled(true);
			}
		});
		isTransRadioBtn.setSelected(true);
		isTransRadioBtn.setFont(new Font("宋体", Font.PLAIN, 16));
		isTransRadioBtn.setBounds(552, 107, 121, 23);
		contentPane.add(isTransRadioBtn);
		bg_QueryStaMode.add(isTransRadioBtn);

		isS2SRadioBtn = new JRadioButton("\u7AD9\u7AD9\u67E5\u8BE2");
		isS2SRadioBtn.setBackground(new Color(224, 255, 255));
		isS2SRadioBtn.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				disableSomeControls();
				textField_1.setEnabled(true);
				isFuzzyArrCheckBox.setEnabled(true);
				isFuzzyDepCheckBox.setText("模糊发站");
				lblNewLabel_3.setText("两站间直达列车");
				lblNewLabel_1.setText("出发站");
				windowFilter.textArea.setEnabled(false);
			}
		});
		isS2SRadioBtn.setFont(new Font("宋体", Font.PLAIN, 16));
		isS2SRadioBtn.setBounds(551, 130, 121, 23);
		contentPane.add(isS2SRadioBtn);
		bg_QueryStaMode.add(isS2SRadioBtn);

		isStaRadioBtn = new JRadioButton("\u8F66\u7AD9\u67E5\u8BE2");
		isStaRadioBtn.setBackground(new Color(224, 255, 255));
		isStaRadioBtn.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				disableSomeControls();
				textField_1.setEnabled(false);
				isFuzzyArrCheckBox.setEnabled(false);
				isFuzzyDepCheckBox.setText("查询同城车站");
				lblNewLabel_3.setText("途径本站的所有列车");
				lblNewLabel_1.setText("车站");
				windowFilter.textArea.setEnabled(false);
			}
		});
		isStaRadioBtn.setFont(new Font("宋体", Font.PLAIN, 16));
		isStaRadioBtn.setBounds(552, 154, 121, 23);
		contentPane.add(isStaRadioBtn);
		bg_QueryStaMode.add(isStaRadioBtn);

		isLastestArrTimeRadioBtn = new JRadioButton("\u6307\u5B9A\u5230\u8FBE\u65F6\u95F4");
		isLastestArrTimeRadioBtn.setBackground(new Color(224, 255, 255));
		isLastestArrTimeRadioBtn.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				lblNewLabel_2_2.setText("最晚到达时间 (hh:mm)");
			}
		});
		isLastestArrTimeRadioBtn.setFont(new Font("宋体", Font.PLAIN, 16));
		isLastestArrTimeRadioBtn.setBounds(552, 222, 121, 23);
		contentPane.add(isLastestArrTimeRadioBtn);
		bg_QueryTimeMode.add(isLastestArrTimeRadioBtn);

		JButton btnNewButton_1_1_1_1 = new JButton("\u8BD5\u8BD5\u624B\u6C14");
		btnNewButton_1_1_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (railnet == null) {
					if (!createRailNet())
						return;
				}
				textField.setText(ReadStations.stationsArr[new Random().nextInt(ReadStations.stationsArr.length)].name);
				textField_1
						.setText(ReadStations.stationsArr[new Random().nextInt(ReadStations.stationsArr.length)].name);
			}
		});
		btnNewButton_1_1_1_1.setFont(new Font("宋体", Font.PLAIN, 14));
		btnNewButton_1_1_1_1.setBounds(350, 260, 100, 26);
		contentPane.add(btnNewButton_1_1_1_1);

	}
}
