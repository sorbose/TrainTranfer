package traintransfer;

import java.io.IOException;
import java.util.Scanner;

public class Inquirer {

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		String stationsPath = "E:\\原电脑高中\\文件\\C\\编程\\Python\\列车时刻表\\站名\\站名字典210116.csv";
		String trainsPath = "E:\\原电脑高中\\文件\\C\\编程\\Python\\列车时刻表\\车次\\总表.txt";
		String sameStationsPath="E:\\原电脑高中\\文件\\C\\编程\\Python\\列车时刻表\\站名\\同城车站\\sameSt.csv";
		RailNet railNet = new RailNet(stationsPath, trainsPath,sameStationsPath);
		System.out.println("输入出发车站名");
		while (sc.hasNext()) {
			String depStName = sc.next();
			System.out.println("输入出发时间，例如09:30");
			int depTime = Trains.strTimeToInt(sc.next(), 0);
			System.out.println("输入到达车站名");
			String arrStName = sc.next();
			System.out.println("输入同站最小换乘间隔，单位分钟");
			int sameStationInterval = sc.nextInt();
			System.out.println("输入同城异站市内交通时间，单位分钟");
			int diffStationInterval = sc.nextInt();
			System.out.println("输入要避开的车站名，以空格分隔，如不需要避开，输入#");
			sc.nextLine();
			String[] stNameToAvoid=sc.nextLine().trim().split(" ");
			System.out.println("只看高铁/动车输入1，不看高铁/动车输入2，不筛选输入0");
			int trainCodeType=sc.nextInt();
			int depV = Stations.stationNameDic.get(depStName);
			int arrV = Stations.stationNameDic.get(arrStName);
			DijkstraForRail dijk = new DijkstraForRail();
			Trains.restoreWeight(railNet);
			railNet.avoidStations(stNameToAvoid);
			dijk.fastestArrival(railNet, depV, depTime, sameStationInterval, diffStationInterval,filterTrainCodeType(trainCodeType));

			System.out.println("方案1：最快到达");
			String[] pathDescStr=Inquirer.getPathDesc(dijk.parTrainId, dijk.parent, arrV);
			for(int ii=0;ii<pathDescStr.length;ii++) {
				System.out.println(pathDescStr[ii]);
			}
			//System.out.println(dijk.parent[arrV]+"  "+dijk.parTrainId[arrV]);
			System.out.println(Trains.intTimeToStr(dijk.dist[arrV] - dijk.dist[depV]-sameStationInterval));
			/*为了使首发站豁免同站换乘间隔，dist[startV]的时间为出发时间-换乘间隔*/
//			Trains.restoreWeight(railNet);
//			System.out.println("方案2：最少乘车时间");
//			dijk.minTimeOnTheTrain(railNet, depV);
//			pathDescStr=Inquirer.getPathDesc(dijk.parTrainId, dijk.parent, arrV);
//			for(int ii=0;ii<pathDescStr.length;ii++) {
//				System.out.println(pathDescStr[ii]);
//			}
//			System.out.println("在火车上待 "+Trains.intTimeToStr(dijk.dist[arrV]));
			railNet.restoreAvoidStations(stNameToAvoid);/*先还原暂时被禁用的顶点，在更新权重，不然会漏*/
			Trains.restoreWeight(railNet);
			System.out.println("输入出发车站名");
		}
		sc.close();
	}
	static String[] getPathDesc(int[] parTrainId,int[] parent,int arrV) {
		if(parent[arrV]==arrV) {
			String[] s=new String[1];
			s[0]="不连通！";
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
			if(trainId==-555) {
				/*表示同城跨站换乘的专用trainID*/
				s[cnt]=n1+" 站 --市内交通--> "+n2+" 站";
				cnt++;
				continue;
			}
			Schedule sch=Trains.schedules[trainId];
			String stTrFrom=sch.originStName,stTrTo=sch.terminalStName;
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
			s[cnt]=n1+" -> "+n2+" "+checi+"(始:"+stTrFrom+",终:"+stTrTo+") "+
			Trains.intTimeToStr(startT).replace("时", ":").replace("分", "")+"~"+
			Trains.intTimeToStr(arriveT).replace("时", ":").replace("分", "");
			if(dayV2-dayV1>0) {
				s[cnt]+="(+"+(dayV2-dayV1)+") ";
			}
			s[cnt]+="(共"+(stNoV2-stNoV1)+"站)";
			cnt++;
		}
		String res[]=new String[cnt];
		for(int i=0;i<cnt;i++) {
			res[i]=s[cnt-1-i];
		}
		return res;
	}
	private static String filterTrainCodeType(int trainCodeType) {
		String res;/*res里储存的车次是不要的车次*/
		if(trainCodeType==1) {
			res = "0KTZYS";/*只看高铁，纯数字车次用0表示*/ 
		}else if(trainCodeType==2) {
			res = "GDC";/*只看普速*/
		}else {
			res=null;
		}
		return res;
		
	}

}
