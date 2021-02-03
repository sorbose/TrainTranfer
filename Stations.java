package traintransfer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;


class ReadStations {
	static Stations[] stationsArr;
	public static Stations[] readStations(String stDicPath, String sameStPath) throws IOException {
		//String path="E:\\ԭ���Ը���\\�ļ�\\C\\���\\Python\\�г�ʱ�̱�\\վ��\\վ���ֵ�210116.csv";
		int Nv=getLineNum(stDicPath);
		Nv--;
		Stations[] stations=new Stations[Nv];
		BufferedReader br=new BufferedReader(new FileReader(stDicPath));
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
		setSameStations(sameStPath, stationsArr);
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
	private static void setSameStations(String sameStPath, Stations[] stationsArr) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader(sameStPath));
		/*id����վ���ֵ��ļ���id��Ӧ*/
		br.readLine();/*������ͷ*/
		String aLine;
		while((aLine=br.readLine())!=null) {
			String[] tmps=aLine.split(",");
			if(!stationsArr[Integer.parseInt(tmps[0])].name.replace(" ", "").equals(tmps[1].replace(" ", ""))) {
				System.out.println("���복վ����ƥ��  "+tmps[0]+tmps[1]);
				continue;
			}
			int[] sameStIdTmp=new int[tmps.length-3];
			for(int i=0;i<sameStIdTmp.length;i++) {
				try {
				sameStIdTmp[i]=Stations.stationNameDic.get(tmps[i+3].replace(" ", ""));
				}catch(NullPointerException ex) {
					sameStIdTmp[i]=-404;
//					System.out.println("δ�ҵ�ͬ�ǳ�վ  "+tmps[i+3]);
				}
			}
			stationsArr[Integer.parseInt(tmps[0])].sameStationsId=sameStIdTmp;
		}
		br.close();
	}

}
public class Stations extends Vertex{
	static Hashtable<String, Integer> stationNameDic=new Hashtable<>(4000);
	public String name;String firstLetter3;String code3;String quanpin;String abbr;int id;
	int[] sameStationsId;
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
//		Stations[] s=ReadStations.readStations("E:\\ԭ���Ը���\\�ļ�\\C\\���\\Python\\�г�ʱ�̱�\\վ��\\վ���ֵ�210116.csv");
//		for(Stations ss:s) {
//			System.out.print(ss.name+" "+ss.id+"\n");
//		}
	}
	
	
}
