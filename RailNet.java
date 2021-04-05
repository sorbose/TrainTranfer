package traintransfer;

import java.io.IOException;

public class RailNet extends Graph {

	String stationsPath, trainsPath;

	public RailNet(String stationsPath, String trainsPath, String sameStPath) throws IOException {
		this.stationsPath = stationsPath;
		this.trainsPath = trainsPath;
		v = ReadStations.readStations(stationsPath, sameStPath);
		super.Nv = v.length;
		Trains.readSchedules(trainsPath);
		Trains.addTrains(this);
		this.setInOrOutAdjList(true);

	}

	public void addEdge(Trains newT) {
		super.addEdge(newT);
	}

	public void avoidStations(String[] stNameToAvoid) {
		if (stNameToAvoid[0].contains("#"))
			return;
		for (int i = 0; i < stNameToAvoid.length; i++) {
			this.v[Stations.stationNameDic.get(stNameToAvoid[i])].saveHeadNode();
			this.v[Stations.stationNameDic.get(stNameToAvoid[i])].E = null;
		}
	}

	public void restoreAvoidStations(String[] stNameToAvoid) {
		if (stNameToAvoid[0].contains("#"))
			return;
		for (int i = 0; i < stNameToAvoid.length; i++) {
			this.v[Stations.stationNameDic.get(stNameToAvoid[i])].restoreHeadNode();
		}
	}

	public static boolean isTheTypeOfTrainSuitable(Trains aTrain, String avoidTrainType) {
		if (avoidTrainType != null && aTrain.trainCodeId >= 0 && Trains.schedules[aTrain.trainCodeId].lines != null) {
			char trainCodeFirstChar = Trains.schedules[aTrain.trainCodeId].lines[0].station_train_code.charAt(0);
			if (trainCodeFirstChar >= '0' && trainCodeFirstChar <= '9')
				trainCodeFirstChar = '0';
			boolean flag = false;
			for (int i = 0; i < avoidTrainType.length(); i++) {
				if (avoidTrainType.charAt(i) == trainCodeFirstChar)
					flag = true;
			} /* 筛选车次 */
			if (flag)
				return false;
		}
		/* Trains.schedules[aTrain.trainCodeId].lines != null 貌似永真，为验证猜想，设置以下测试代码*/
		if (avoidTrainType != null && aTrain.trainCodeId >= 0 && Trains.schedules[aTrain.trainCodeId].lines == null) {
			System.out.println(aTrain.trainCodeId);
			System.out.println(aTrain.depStName);
			System.out.println(aTrain.arrStName);
		}
		return true;
	}

	public Trains getTrainsBetweenTwoStas(int startV, int arrV, String avoidTrainType) {
		Trains headTrains = null;
		Trains aTrain = null;
		for (aTrain = (Trains) this.v[startV].E; aTrain != null; aTrain = (Trains) aTrain.nextEdge) {
			if(aTrain.arrStId!=arrV)continue;
			if(!isTheTypeOfTrainSuitable(aTrain, avoidTrainType)) {continue;}
			if(headTrains==null) {
				headTrains=(Trains) aTrain.clone();
				headTrains.nextEdge=null;
				continue;
			}
			Edge tmp=(Edge) aTrain.clone();
			tmp.nextEdge=headTrains.nextEdge;
			headTrains.nextEdge=tmp;
		}
		return headTrains;
	}

	public static void main(String[] args) {
		// TODO 自动生成的方法存根

	}

}
