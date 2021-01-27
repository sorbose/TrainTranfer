package traintransfer;

import java.io.IOException;

public class RailNet extends Graph {

	String stationsPath,trainsPath;
	

	public RailNet(String stationsPath, String trainsPath) throws IOException {
		super(0);
		this.stationsPath = stationsPath;
		this.trainsPath = trainsPath;
		v=ReadStations.readStations(stationsPath);
		super.Nv=v.length;
		Trains.readSchedules(trainsPath);
		Trains.addTrains(this);
	}
	public void addEdge(Trains newT) {
		super.addEdge(newT);
	}
	


	public static void main(String[] args) {
		// TODO 自动生成的方法存根

	}

}
