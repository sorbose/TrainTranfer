package traintransfer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Trains extends Edge implements Cloneable {
	static Schedule[] schedules;
	int depStId;
	int arrStId;
	int trainCodeId;/* trainCodeIdҲ��ʱ�̱��ࣨ����һ�л𳵾��������п���վ��������±꣬��ͬһ���ε�Ψһ��ʶ�� */
	String depStName;
	String arrStName;
	int depTime;
	int arrTime;/* ��Ϊ24Сʱ�ƣ�ģ1440��[0,1439] */
	int arrDay;/* ���յ���Ϊ0������Ϊ1���Դ����� */
	// int weight = -1;
	/*
	 * �ڲ�ͬ������Ȩֵ���岻ͬ��������ʱ��Ϊ�˳�ʱ�䣨��վʱ��-��վʱ�䣩���Է��Ӽƣ����������ʱ����˵ Ȩֵʹ�ø���ı���w
	 */
	private int w0;

	static Schedule[] readSchedules(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		schedules = new Schedule[Integer.parseInt(br.readLine()) + 1];
		/* id��1��ʼ���������鳤��+1��0������ */
		schedules[0] = new Schedule(-1, -1, -1, "*������", "������*", null);
		String idStr;
		int id;
		int lineCnt;
		String lineStr;
		String[] tmps;
		ALineInSche[] linesTmp = new ALineInSche[100];
		while ((idStr = br.readLine()) != null) {
			lineCnt = 0;
			id = Integer.parseInt(idStr);
			while ((lineStr = br.readLine()) != null) {
				if (lineStr.equals(";"))
					break;
				tmps = lineStr.split("\t");
				linesTmp[lineCnt++] = new ALineInSche(Integer.parseInt(tmps[0]), tmps[1], tmps[2],
						strTimeToInt(tmps[3], 0), strTimeToInt(tmps[4], 0), Integer.parseInt(tmps[5]));
			}
			if (lineCnt != 0) {
				ALineInSche[] t = new ALineInSche[lineCnt];
				for (int i = 0; i < lineCnt; i++) {
					t[i] = linesTmp[i];
				}
				String originStName = t[0].station_name;
				String terminalStName = t[lineCnt - 1].station_name;
				int originStId;
				int terminalStId;
				try {
					originStId = Stations.stationNameDic.get(originStName);
					terminalStId = Stations.stationNameDic.get(terminalStName);
				} catch (NullPointerException e) {
//					System.out.println("readSchedulesδ�ҵ�  " + originStName + " �� " + terminalStName);
					continue;
				}
				schedules[id] = new Schedule(id, originStId, terminalStId, originStName, terminalStName, t);
			}
		}
		br.close();
		return schedules;
	}

	static void addTrains(RailNet railnet) {
		for (int _i = 0; _i < schedules.length; _i++) {
			Schedule sch = schedules[_i];
			if (sch == null || sch.lines == null)
				continue;
			for (int i = 0; i < sch.lines.length; i++) {
				String depStName = sch.lines[i].station_name;
				int depTime = sch.lines[i].start_time;
				int depStId;
				try {
					depStId = Stations.stationNameDic.get(depStName);
				} catch (NullPointerException e) {
//					System.out.println("addTrainsδ�ҵ�  " + depStName);
					continue;
				}

				for (int j = i + 1; j < sch.lines.length; j++) {
					String arrStName = sch.lines[j].station_name;
					int arrStId;
					try {
						arrStId = Stations.stationNameDic.get(arrStName);
					} catch (NullPointerException e) {
//						System.out.println("addTrainsδ�ҵ�  " + arrStName);
						continue;
					}
					int arrTime = sch.lines[j].arrive_time;
					int arrDay = sch.lines[j].arrive_day_diff;
					int trainCodeId = sch.trainCodeId;
					Trains newT = new Trains(depStId, arrStId, trainCodeId, depStName, arrStName, depTime, arrTime,
							arrDay);
					int depDay = sch.lines[i].arrive_day_diff;
					if (sch.lines[i].arrive_time > sch.lines[i].start_time)
						depDay++;
					newT.w = arrTime - depTime + (arrDay - depDay) * 1440;
					newT.w0 = newT.w;

//					System.out.println(newT.depStName+"  "+newT.arrStName);
					railnet.addEdge(newT);
				}
			}
		}
	}

	static String getATrainBasicDescInf(int schedulesIndex, String startStName, String arrStName) {
		Schedule sch = schedules[schedulesIndex];
		String res;
		String stTrFrom = sch.originStName, stTrTo = sch.terminalStName;
		boolean hasSelV1 = false;
		int startT = -1, arriveT = -2;
		int stNoV1 = 0, stNoV2 = 0;
		int dayV1 = -1, dayV2 = -2;
		String checi = null;
		for (int ii = 0; ii < sch.lines.length; ii++) {
			if (sch.lines[ii].station_name.equals(startStName) && hasSelV1 == false) {
				hasSelV1 = true;/* ���ǻ��߳�վ��ʱ�̱��г������ε����⣬����Ϊ��վʱ���Խ�����ֵ�Ϊ׼ */
				startT = sch.lines[ii].start_time;
				checi = sch.lines[ii].station_train_code;
				stNoV1 = sch.lines[ii].station_no;
				dayV1 = sch.lines[ii].arrive_day_diff;
				if (startT < sch.lines[ii].arrive_time)
					dayV1++;
			}
			if (sch.lines[ii].station_name.equals(arrStName)) {
				arriveT = sch.lines[ii].arrive_time;
				stNoV2 = sch.lines[ii].station_no;
				dayV2 = sch.lines[ii].arrive_day_diff;
			}
		}
		res = startStName + " -> " + arrStName + " " + checi + "(ʼ:" + stTrFrom + ",��:" + stTrTo + ") "
				+ Trains.intTimeToStr(startT).replace("ʱ", ":").replace("��", "") + "~"
				+ Trains.intTimeToStr(arriveT).replace("ʱ", ":").replace("��", "");
		if (dayV2 - dayV1 > 0) {
			res += "(+" + (dayV2 - dayV1) + ") ";
		}
		res += "(��" + (stNoV2 - stNoV1) + "վ)";
		return res;
	}

	public static void restoreWeight(RailNet railnet) {
		for (int i = 0; i < railnet.v.length; i++) {
			if (railnet.v[i] == null || railnet.v[i].E == null)
				continue;
			Trains t = (Trains) railnet.v[i].E;
			while (t != null) {
				t.w = t.w0;
				t = (Trains) t.nextEdge;
			}
		}
	}
	
	

	@Override
	public Object clone(){
		// TODO �Զ����ɵķ������
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		return null;
	}

	public Trains(int depStId, int arrStId, int trainCodeId, String depStName, String arrStName, int depTime,
			int arrTime, int arrDay) {
		super(depStId, arrStId, arrTime - depTime + 1440 * arrDay);
		this.depStId = depStId;
		this.arrStId = arrStId;
		this.trainCodeId = trainCodeId;
		this.depStName = depStName;
		this.arrStName = arrStName;
		this.depTime = depTime;
		this.arrTime = arrTime;
		this.arrDay = arrDay;
	}

	public Trains(int weight) {
		this(-1, -2, -3, "*no", "no*", -4, -5, -6);
		super.w = weight;
	}

	public static int strTimeToInt(String time, int day) {
		String[] s = time.replace("��", ":").split(":");
		if (s.length == 1)
			return -1;
		int res = Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]) + day * 1440;
		return res;

	}

	public static String intTimeToStr(int time) {
		/* ��ʱ��ת����hʱm�ֵĸ�ʽ */
		if(time<0)return "";
		if(time>0x1f3f3f3f)return "�޽��";
		int hr = time / 60;
		time %= 60;
		String res = "";
		if (hr < 10)
			res += "0";
		res += hr;
		res += " ʱ";
		if (time < 10)
			res += "0";
		res += time;
		res += " ��";
		return res;

	}

}

class Schedule {
	int trainCodeId;
	int originStId;
	int terminalStId;
	String originStName;
	String terminalStName;
	ALineInSche[] lines = null;

	public Schedule(int trainCodeId, int originStId, int terminalStId, String originStName, String terminalStName,
			ALineInSche[] lines) {
		super();
		this.trainCodeId = trainCodeId;
		this.originStId = originStId;
		this.terminalStId = terminalStId;
		this.originStName = originStName;
		this.terminalStName = terminalStName;
		this.lines = lines;
	}

}

class ALineInSche {
	int station_no;
	String station_train_code;
	String station_name;
	int arrive_time;
	int start_time;/* ȡֵ��Χ[0,1439] */
	int arrive_day_diff;

	public ALineInSche(int station_no, String station_train_code, String station_name, int arrive_time, int start_time,
			int arrive_day_diff) {
		super();
		this.station_no = station_no;
		this.station_train_code = station_train_code;
		this.station_name = station_name.replace(" ", "");
		this.arrive_time = arrive_time;
		this.start_time = start_time;
		this.arrive_day_diff = arrive_day_diff;
	}
}