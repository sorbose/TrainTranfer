package traintransfer;

import java.io.IOException;
import java.util.Scanner;

public class Inquirer {

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
//		System.out.println("���복վ�ļ�·��");
//		String stationsPath=sc.next();
		String stationsPath = "E:\\ԭ���Ը���\\�ļ�\\C\\���\\Python\\�г�ʱ�̱�\\վ��\\վ���ֵ�210116.csv";
//		System.out.println("���복���ļ�·��");
//		String trainsPath=sc.next();
		String trainsPath = "E:\\ԭ���Ը���\\�ļ�\\C\\���\\Python\\�г�ʱ�̱�\\����\\�ܱ�.txt";
		RailNet railNet = new RailNet(stationsPath, trainsPath);
		System.out.println("���������վ��");
		while (sc.hasNext()) {
			String depStName = sc.next();
			System.out.println("�������ʱ�䣬����09:30");
			int depTime = Trains.strTimeToInt(sc.next(), 0);
			System.out.println("���뵽�ﳵվ��");
			String arrStName = sc.next();
			System.out.println("������С���˼������λ����");
			int interval = sc.nextInt();
			int depV = Stations.stationNameDic.get(depStName);
			int arrV = Stations.stationNameDic.get(arrStName);
			DijkstraForRail dijk = new DijkstraForRail();
			dijk.dijkstraForRail(railNet, depV, depTime, interval, interval);
//			for (int i = arrV; dijk.parent[i] != i; i = dijk.parent[i]) {
//				System.out.println(ReadStations.stationsArr[i].name + " (ʼ��"
//			+Trains.schedules[dijk.parTrainId[i]].originStName+" �գ�"+
//			Trains.schedules[dijk.parTrainId[i]].terminalStName+")");
//			}
			String[] pathDescStr=Inquirer.getPathDesc(dijk.parTrainId, dijk.parent, arrV);
			for(int ii=0;ii<pathDescStr.length;ii++) {
				System.out.println(pathDescStr[ii]);
			}
			System.out.println(Trains.intTimeToStr(dijk.dist[arrV] - depTime));
			Trains.restoreWeight(railNet);
			System.out.println("���������վ��");
		}
		sc.close();
	}
	public static String[] getPathDesc(int[] parTrainId,int[] parent,int arrV) {
		if(parent[arrV]==arrV) {
			String[] s=new String[1];
			s[0]="����ͨ��";
			return s;
		}
		String[] s=new String[500];
		int cnt=0;
		for(int i=arrV;parent[i]!=i;i=parent[i]) {
			int v2=i;
			int v1=parent[i];
			String n2=ReadStations.stationsArr[v2].name;
			String n1=ReadStations.stationsArr[v1].name;
			int trainId=parTrainId[i];
			int startT=-1,arriveT=-2;
			int stNoV1=0,stNoV2=0;
			int dayV1=-1,dayV2=-2;
			String checi=null;
			Schedule sch=Trains.schedules[trainId];
			for(int ii=0;ii<sch.lines.length;ii++) {
				if(sch.lines[ii].station_name.equals(n1)) {
					startT=sch.lines[ii].start_time;
					checi=sch.lines[ii].station_train_code;
					stNoV1=sch.lines[ii].station_no;
					dayV1=sch.lines[ii].arrive_day_diff;
					if(startT<sch.lines[ii].arrive_time)dayV1++;
				}
				if(sch.lines[ii].station_name.equals(n2)) {
					arriveT=sch.lines[ii].arrive_time;
					stNoV2=sch.lines[ii].station_no;
					dayV2=sch.lines[ii].arrive_day_diff;
				}
			}
			s[cnt]=n1+" -> "+n2+" "+checi+" "+
			Trains.intTimeToStr(startT).replace("ʱ", ":").replace("��", "")+" ~ "+
			Trains.intTimeToStr(arriveT).replace("ʱ", ":").replace("��", "");
			if(dayV2-dayV1>0) {
				s[cnt]+="(+"+(dayV2-dayV1)+") ";
			}
			s[cnt]+=" (��"+(stNoV2-stNoV1)+"վ)";
			cnt++;
		}
		String res[]=new String[cnt];
		for(int i=0;i<cnt;i++) {
			res[i]=s[cnt-1-i];
		}
		return res;
		
	}

}
