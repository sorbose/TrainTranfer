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
			dijk.fastestArrival(railNet, depV, depTime, sameStationInterval, diffStationInterval,filterTrainCodeType(trainCodeType),false,false,arrV);

			System.out.println("方案1：最快到达");
			String[] pathDescStr=Inquirer.getPathDesc(dijk.parTrainId, dijk.parent, arrV,false);
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
	static String[] getPathDesc(int[] nextTrainId,int[] nextVArr,int firstVId,boolean isInverse) {
		if(nextVArr[firstVId]==firstVId) {
			String[] s=new String[1];
			s[0]="不连通！";
			return s;
		}
		String[] s=new String[500];
		int cnt=0;
		for(int i=firstVId;nextVArr[i]!=i;i=nextVArr[i]) {
			int v1,v2;
			if(isInverse) {v1=i;v2=nextVArr[i];}
			else {v2=i;v1=nextVArr[i];}
			String n2=ReadStations.stationsArr[v2].name;
			String n1=ReadStations.stationsArr[v1].name;
			int trainId=nextTrainId[i];

			if(trainId==-555) {
				/*表示同城跨站换乘的专用trainID*/
				s[cnt]=n1+" 站 --市内交通--> "+n2+" 站";
				cnt++;
				continue;
			}
//			System.out.println(n1+"--"+n2);
			s[cnt]=Trains.getATrainBasicDescInf(trainId, n1, n2);
//			System.out.println(s[cnt]);
			cnt++;
		}
		String res[]=new String[cnt];
		for(int i=0;i<cnt;i++) {
			if(isInverse)res[i]=s[i];
			else res[i]=s[cnt-1-i];
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