package traintransfer;

import java.io.IOException;

public class RailNet extends Graph {

	String stationsPath,trainsPath;
	

	public RailNet(String stationsPath, String trainsPath,String sameStPath) throws IOException {
		super(0);
		this.stationsPath = stationsPath;
		this.trainsPath = trainsPath;
		v=ReadStations.readStations(stationsPath,sameStPath);
		super.Nv=v.length;
		Trains.readSchedules(trainsPath);
		Trains.addTrains(this);
	}
	public void addEdge(Trains newT) {
		super.addEdge(newT);
	}
	public void avoidStations(String[] stNameToAvoid) {
		if(stNameToAvoid[0].contains("#"))return;
		for(int i=0;i<stNameToAvoid.length;i++) {
			this.v[Stations.stationNameDic.get(stNameToAvoid[i])].saveHeadNode();
			this.v[Stations.stationNameDic.get(stNameToAvoid[i])].E=null;
		}
	}
	public void restoreAvoidStations(String[] stNameToAvoid) {
		if(stNameToAvoid[0].contains("#"))return;
		for(int i=0;i<stNameToAvoid.length;i++) {
			this.v[Stations.stationNameDic.get(stNameToAvoid[i])].restoreHeadNode();
		}
	}


	public static void main(String[] args) {
		// TODO 自动生成的方法存根

	}

}
