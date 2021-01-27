package traintransfer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

class ReadStations {
	static Stations[] stationsArr;
	public static Stations[] readStations(String path) throws IOException {
		//String path="E:\\原电脑高中\\文件\\C\\编程\\Python\\列车时刻表\\站名\\站名字典210116.csv";
		int Nv=getLineNum(path);
		Nv--;
		Stations[] stations=new Stations[Nv];
		BufferedReader br=new BufferedReader(new FileReader(path));
		br.readLine();
		String tmp;
		String[] tmps;
		int cnt=0;
		while((tmp=br.readLine())!=null) {
			if(tmp.strip()=="")break;
			tmps=tmp.split(",");
			if(tmps[0].contains(" ")) {
				stations[cnt++]=new Stations(tmps[0], tmps[1], tmps[2], tmps[3], tmps[4], Integer.parseInt(tmps[5]));
				continue;
			}
//			System.out.println(tmps);
			stations[cnt++]=new Stations(tmps[0].replace(" ", ""), tmps[1], tmps[2], tmps[3], tmps[4], Integer.parseInt(tmps[5]));
			Stations.stationNameDic.put(tmps[0].replace(" ", ""), Integer.parseInt(tmps[5]));
		}
		br.close();
		stationsArr=stations;
		return stations;
	}
	private static int getLineNum(String path) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader(path));
		int res=0;
		String tmp;
		while((tmp=br.readLine())!=null) {
			//System.out.println("aaa");
			if(tmp.strip()!="")res++;
		}
		br.close();
//		System.out.println(res);
		return res;
	}

}
public class Stations extends Vertex{
	static Hashtable<String, Integer> stationNameDic=new Hashtable<>(4000);
	public String name;String firstLetter3;String code3;String quanpin;String abbr;int id;

	public Stations(String name, String firstLetter3, String code3, String quanpin, String abbr,int id) {
		super(id);
		this.name = name;
		this.firstLetter3 = firstLetter3;
		this.code3 = code3;
		this.quanpin = quanpin;
		this.abbr = abbr;
		this.id = id;
	}
	public static void main(String[] args) throws IOException {
//		Stations[] s=ReadStations.readStations("E:\\原电脑高中\\文件\\C\\编程\\Python\\列车时刻表\\站名\\站名字典210116.csv");
//		for(Stations ss:s) {
//			System.out.print(ss.name+" "+ss.id+"\n");
//		}
	}
	
	
}
