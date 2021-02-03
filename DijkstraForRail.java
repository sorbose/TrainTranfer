package traintransfer;

public class DijkstraForRail {
	static int INF = 0x3f3f3f3f;
	public int dist[];
	public int parent[];
	public int parTrainId[];
	private boolean isVis[];
	private int[] transferTimes;
	private HeapForDijkstra<Trains> que;

	public void fastestArrival(Graph g, int startV, int startTime, int minSameStationTransferInterval,
			int minSameCityTransferInterval,String filterTrainType) {
		/*
		 * 给定一个出发时间startTime，自startTime开始计时，求出最短时间到达目的地
		 * （也就是最早到达目的地）的方案，如果方案有多种，给出的方案未必是最优的（最少换乘、最少绕路）
		 */
		int Nv = g.Nv;
		isVis = new boolean[Nv];
		dist = new int[Nv];
		parent = new int[Nv];
		parTrainId = new int[Nv];
		transferTimes = new int[Nv];/* transferTimes[i]表示到达i的换乘次数 */
		for (int i = 0; i < Nv; i++)
			isVis[i] = false;
		for (int i = 0; i < Nv; i++)
			dist[i] = INF;
		for (int i = 0; i < Nv; i++)
			parent[i] = i;
		for (int i = 0; i < Nv; i++)
			parTrainId[i] = -1;
		for (int i = 0; i < Nv; i++)
			transferTimes[i] = 0;
		// dist[startV]=0;
		dist[startV] = (startTime-minSameStationTransferInterval+1440)%1440;
		que = new HeapForDijkstra<Trains>(Nv, new Trains(-INF)) {
			@Override
			public int compare(Trains a, Trains b) {
				return a.w > b.w ? 1 : -1;
			}
		};

		que.insert(new Trains(startV, startV, -100, ReadStations.stationsArr[startV].name,
				ReadStations.stationsArr[startV].name, 
				(startTime-minSameStationTransferInterval+1440)%1440
				, (startTime-minSameStationTransferInterval+1440)%1440, 0), startV);
		Trains tmpE = null;
		while (!que.isEmpty()) {
			tmpE = que.top();
			que.pop();
			int u = tmpE.v2;
			if (isVis[u])
				continue;
			isVis[u] = true;
			parent[u] = tmpE.v1;
			parTrainId[u] = tmpE.trainCodeId;
			Trains ttmp = (Trains) g.v[u].E;
			/*
			 * 权值是自到达上一站起，至到达下一站止，经历的时间，用分钟表示 分为两部分，一是等车时间，二是乘车时间
			 */
			traverseAllTheTrainsInAStation(ttmp, u, dist[u]%1440, minSameStationTransferInterval,filterTrainType);

			/* 再考虑异站换乘，在站点u与u的每个同城车站间添加一趟虚拟火车摆渡 */
			for (int i = 0; i < ReadStations.stationsArr[u].sameStationsId.length; i++) {
				int otherStId = ReadStations.stationsArr[u].sameStationsId[i];
				if(otherStId==-404)continue;
				int virTrDepTime=dist[u] % 1440;
				int virTrArrTime=virTrDepTime+minSameCityTransferInterval-minSameStationTransferInterval;
				int virTrArrDay=virTrArrTime/1440;
				virTrArrTime%=1440;
				Trains virtualCityTrain = new Trains(u, otherStId, -555, ReadStations.stationsArr[u].name,
						ReadStations.stationsArr[otherStId].name, virTrDepTime,
						virTrArrTime, virTrArrDay);
				
				/* ！！这里的时间算法可能有问题，注意跨越零点的情况，或影响权值计算 */
				int cost = virtualCityTrain.w;
				if (!isVis[otherStId] && dist[otherStId] >= dist[u] + cost) {
					if ((dist[otherStId] > dist[u] + cost)
							|| (dist[otherStId] == dist[u] + cost && transferTimes[u] < transferTimes[otherStId])) {
						dist[otherStId] = dist[u] + cost;
						virtualCityTrain.w = dist[otherStId];
						transferTimes[otherStId] = transferTimes[u] + 1;
						que.insert(virtualCityTrain, otherStId);
					}
				}

			}
//			}
		}

	}

	private void traverseAllTheTrainsInAStation(Trains train, int currVId, 
			int currTime,int minIntv,String filterTrainType) {

		for (; train != null;) {
			if(filterTrainType!=null&&train.trainCodeId>=0&&Trains.schedules[train.trainCodeId].lines!=null) {
				char trainCodeFirstChar=Trains.schedules[train.trainCodeId].lines[0].station_train_code.charAt(0);
				if(trainCodeFirstChar>='0'&&trainCodeFirstChar<='9')trainCodeFirstChar='0';
				boolean flag=false;
				for(int i=0;i<filterTrainType.length();i++) {
					if(filterTrainType.charAt(i)==trainCodeFirstChar) flag=true;
				} /*筛选车次*/
				if(flag) {train = (Trains) train.nextEdge;continue;}
			}
			
			int waitingTime = (train.depTime - currTime + 1440) % 1440;
			int ridingTime = train.w;/* 默认的权值定义即为乘车时间 */
			if (waitingTime < minIntv) {
				/* 换乘时间不够，赶不上车，原地多等一天 */
				waitingTime += 1440;
			}
			train.w = waitingTime + ridingTime;
			int v2 = train.v2;
			int cost = train.w;
			/*
			 * dist[v2]==dist[u]+cost时， 更新与否都可以，可以再比较换乘次数，选择较少的一种
			 */
			if (!isVis[v2] && dist[v2] >= dist[currVId] + cost) {
				/* 两者相等，选换乘少的 */
				if ((dist[v2] > dist[currVId] + cost)
						|| (dist[v2] == dist[currVId] + cost && transferTimes[currVId] < transferTimes[v2])) {
					/*
					 * 在耗时一样的情况下，只有当新线路换乘更少的时候，才采用新线路， 这里必须是严格小于，不能取等 例如到u点需换乘2次，从u到v2还需换乘1次
					 * 则保留原有的到v2换乘2次的方案
					 */
					dist[v2] = dist[currVId] + cost;
					train.w = dist[v2];
					transferTimes[v2] = transferTimes[currVId] + 1;
					/* 只要u点换乘，只要采用了新方案，换乘次数必然在原基础上+1 */
					que.insert(train, v2);
				}
			}
			train = (Trains) train.nextEdge;
		}
	}

	public void minTimeOnTheTrain(Graph g, int startV) {
		/*
		 * 求出坐在火车上的时间最少的方案，这是最简单的一种情况，每条边的权值都是固定的，
		 * 不随出发时间的改变而改变（既然你只关心坐在火车上的时间而不关心总历时，那我也不需要考虑你能不能
		 * 赶上车的问题，大不了你在startV车站等一天，换乘站同样不予考虑能否赶上当天的车）
		 * 这一算法的意义在于查询途径站之间的标杆车，以及展现不考虑换乘时间的愚蠢
		 */
		int Nv = g.Nv;
		boolean isVis[] = new boolean[Nv];
		dist = new int[Nv];
		parent = new int[Nv];
		parTrainId = new int[Nv];
		int[] transferTimes = new int[Nv];/* transferTimes[i]表示到达i的换乘次数 */
		for (int i = 0; i < Nv; i++)
			isVis[i] = false;
		for (int i = 0; i < Nv; i++)
			dist[i] = INF;
		for (int i = 0; i < Nv; i++)
			parent[i] = i;
		for (int i = 0; i < Nv; i++)
			parTrainId[i] = -1;
		for (int i = 0; i < Nv; i++)
			transferTimes[i] = 0;
		// dist[startV]=0;
		dist[startV] = 0;
		HeapForDijkstra<Trains> que = new HeapForDijkstra<Trains>(Nv, new Trains(-INF)) {
			@Override
			public int compare(Trains a, Trains b) {
				return a.w > b.w ? 1 : -1;
			}
		};

		que.insert(new Trains(startV, startV, -100, ReadStations.stationsArr[startV].name,
				ReadStations.stationsArr[startV].name, 0, 0, 0), startV);
		Trains tmpE = null;
		while (!que.isEmpty()) {
			tmpE = que.top();
			que.pop();
//			System.out.println(tmpE.v1+" "+tmpE.v2+" "+tmpE.w);
			int u = tmpE.v2;
			if (isVis[u])
				continue;
			isVis[u] = true;
			parent[u] = tmpE.v1;
			parTrainId[u] = tmpE.trainCodeId;
			Trains ttmp = (Trains) g.v[u].E;
			/*
			 * 权值是火车的运行时间，即乘车时间，即火车到站时间-发站时间，不考虑在火车站等车的时间
			 */
			int ridingTime;
			for (; ttmp != null;) {
				ridingTime = ttmp.w;/* 默认的权值定义即为乘车时间 */
				ttmp.w = ridingTime;
				int v2 = ttmp.v2;
				int cost = ttmp.w;
				/*
				 * dist[v2]==dist[u]+cost时， 更新与否都可以，可以再比较换乘次数，选择较少的一种
				 */
				if (!isVis[v2] && dist[v2] >= dist[u] + cost) {
					/* 两者相等，选换乘少的 */
					if ((dist[v2] > dist[u] + cost) || (dist[v2] == dist[u] + cost
							&& transferTimes[u] < transferTimes[v2])) {/*
																		 * 在耗时一样的情况下，只有当新线路换乘更少的时候，才采用新线路，
																		 * 这里必须是严格小于，不能取等 例如到u点需换乘2次，从u到v2还需换乘1次
																		 * 则保留原有的到v2换乘2次的方案
																		 */
						dist[v2] = dist[u] + cost;
						ttmp.w = dist[v2];
						transferTimes[v2] = transferTimes[u] + 1;
						/* 只要u点换乘，只要采用了新方案，换乘次数必然在原基础上+1 */
						que.insert(ttmp, v2);
					}

				}
				ttmp = (Trains) ttmp.nextEdge;
			}
		}

//		for(int i=0;i<Nv;i++)System.out.println(isVis[i]);

	}

	public static void main(String[] args) {

	}

}