package traintransfer;

public class DijkstraForRail {
	static int INF=0x3f3f3f3f;
	public int dist[];
	public int parent[];
	public int parTrainId[];
	public void dijkstraForRail(Graph g,int startV,int startTime,
			int minSameStationTransferInterval,int minSameCityTransferInterval) {
		int Nv=g.Nv;
		boolean isVis[]=new boolean[Nv];
		dist=new int[Nv];
		parent=new int[Nv];
		parTrainId=new int[Nv];
		for(int i=0;i<Nv;i++)isVis[i]=false;
		for(int i=0;i<Nv;i++)dist[i]=INF;
		for(int i=0;i<Nv;i++)parent[i]=i;
		for(int i=0;i<Nv;i++)parTrainId[i]=-1;
		//dist[startV]=0;
		dist[startV]=startTime;
		HeapForDijkstra<Trains> que=new HeapForDijkstra<Trains>(Nv,new Trains(-INF)) {
			@Override
			public int compare(Trains a, Trains b) {
				return a.w>b.w?1:-1;
			}
		};
		

		
		que.insert(new Trains(startV, startV, -100, ReadStations.stationsArr[startV].name, 
				ReadStations.stationsArr[startV].name, startTime, startTime, 0),startV);
		Trains tmpE=null;
		while(!que.isEmpty()) {
			tmpE=que.top();
			que.pop();
//			System.out.println(tmpE.v1+" "+tmpE.v2+" "+tmpE.w);
			int u=tmpE.v2;
			if(isVis[u])continue;
			isVis[u]=true;
			parent[u]=tmpE.v1;
			parTrainId[u]=tmpE.trainCodeId;
			Trains ttmp=(Trains) g.v[u].E;
			/*权值是自到达上一站起，至到达下一站止，经历的时间，用分钟表示
			 * 分为两部分，一是等车时间，二是乘车时间*/
			int waitingTime,ridingTime;
			int currentTime=dist[u]%1440;
			int minIntv = minSameStationTransferInterval;/*暂时只考虑同站换乘*/
			for(;ttmp!=null;) {
				waitingTime=(ttmp.depTime-currentTime+1440)%1440;
				ridingTime = ttmp.w;/*默认的权值定义即为乘车时间*/
				if(waitingTime<minIntv) {
					/*换乘时间不够，赶不上车，原地多等一天*/
					waitingTime+=1440;
				}
				ttmp.w=waitingTime+ridingTime;
				int v2=ttmp.v2;
				int cost=ttmp.w;
				if(!isVis[v2]&&dist[v2]>dist[u]+cost) {
					dist[v2]=dist[u]+cost;
					ttmp.w=dist[v2];
					que.insert(ttmp, v2);
				}
				ttmp=(Trains) ttmp.nextEdge;
			}
		}
		
//		for(int i=0;i<Nv;i++)System.out.println(isVis[i]);

	}
	public static void main(String[] args) {
		
	}

}