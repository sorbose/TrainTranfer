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
		 * ����һ������ʱ��startTime����startTime��ʼ��ʱ��������ʱ�䵽��Ŀ�ĵ�
		 * ��Ҳ�������絽��Ŀ�ĵأ��ķ�������������ж��֣������ķ���δ�������ŵģ����ٻ��ˡ�������·��
		 */
		int Nv = g.Nv;
		isVis = new boolean[Nv];
		dist = new int[Nv];
		parent = new int[Nv];
		parTrainId = new int[Nv];
		transferTimes = new int[Nv];/* transferTimes[i]��ʾ����i�Ļ��˴��� */
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
			 * Ȩֵ���Ե�����һվ����������һվֹ��������ʱ�䣬�÷��ӱ�ʾ ��Ϊ�����֣�һ�ǵȳ�ʱ�䣬���ǳ˳�ʱ��
			 */
			traverseAllTheTrainsInAStation(ttmp, u, dist[u]%1440, minSameStationTransferInterval,filterTrainType);

			/* �ٿ�����վ���ˣ���վ��u��u��ÿ��ͬ�ǳ�վ�����һ������𳵰ڶ� */
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
				
				/* ���������ʱ���㷨���������⣬ע���Խ�����������Ӱ��Ȩֵ���� */
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
				} /*ɸѡ����*/
				if(flag) {train = (Trains) train.nextEdge;continue;}
			}
			
			int waitingTime = (train.depTime - currTime + 1440) % 1440;
			int ridingTime = train.w;/* Ĭ�ϵ�Ȩֵ���弴Ϊ�˳�ʱ�� */
			if (waitingTime < minIntv) {
				/* ����ʱ�䲻�����ϲ��ϳ���ԭ�ض��һ�� */
				waitingTime += 1440;
			}
			train.w = waitingTime + ridingTime;
			int v2 = train.v2;
			int cost = train.w;
			/*
			 * dist[v2]==dist[u]+costʱ�� ������񶼿��ԣ������ٱȽϻ��˴�����ѡ����ٵ�һ��
			 */
			if (!isVis[v2] && dist[v2] >= dist[currVId] + cost) {
				/* ������ȣ�ѡ�����ٵ� */
				if ((dist[v2] > dist[currVId] + cost)
						|| (dist[v2] == dist[currVId] + cost && transferTimes[currVId] < transferTimes[v2])) {
					/*
					 * �ں�ʱһ��������£�ֻ�е�����·���˸��ٵ�ʱ�򣬲Ų�������·�� ����������ϸ�С�ڣ�����ȡ�� ���絽u���軻��2�Σ���u��v2���軻��1��
					 * ����ԭ�еĵ�v2����2�εķ���
					 */
					dist[v2] = dist[currVId] + cost;
					train.w = dist[v2];
					transferTimes[v2] = transferTimes[currVId] + 1;
					/* ֻҪu�㻻�ˣ�ֻҪ�������·��������˴�����Ȼ��ԭ������+1 */
					que.insert(train, v2);
				}
			}
			train = (Trains) train.nextEdge;
		}
	}

	public void minTimeOnTheTrain(Graph g, int startV) {
		/*
		 * ������ڻ��ϵ�ʱ�����ٵķ�����������򵥵�һ�������ÿ���ߵ�Ȩֵ���ǹ̶��ģ�
		 * �������ʱ��ĸı���ı䣨��Ȼ��ֻ�������ڻ��ϵ�ʱ�������������ʱ������Ҳ����Ҫ�������ܲ���
		 * ���ϳ������⣬��������startV��վ��һ�죬����վͬ�����迼���ܷ���ϵ���ĳ���
		 * ��һ�㷨���������ڲ�ѯ;��վ֮��ı�˳����Լ�չ�ֲ����ǻ���ʱ����޴�
		 */
		int Nv = g.Nv;
		boolean isVis[] = new boolean[Nv];
		dist = new int[Nv];
		parent = new int[Nv];
		parTrainId = new int[Nv];
		int[] transferTimes = new int[Nv];/* transferTimes[i]��ʾ����i�Ļ��˴��� */
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
			 * Ȩֵ�ǻ𳵵�����ʱ�䣬���˳�ʱ�䣬���𳵵�վʱ��-��վʱ�䣬�������ڻ�վ�ȳ���ʱ��
			 */
			int ridingTime;
			for (; ttmp != null;) {
				ridingTime = ttmp.w;/* Ĭ�ϵ�Ȩֵ���弴Ϊ�˳�ʱ�� */
				ttmp.w = ridingTime;
				int v2 = ttmp.v2;
				int cost = ttmp.w;
				/*
				 * dist[v2]==dist[u]+costʱ�� ������񶼿��ԣ������ٱȽϻ��˴�����ѡ����ٵ�һ��
				 */
				if (!isVis[v2] && dist[v2] >= dist[u] + cost) {
					/* ������ȣ�ѡ�����ٵ� */
					if ((dist[v2] > dist[u] + cost) || (dist[v2] == dist[u] + cost
							&& transferTimes[u] < transferTimes[v2])) {/*
																		 * �ں�ʱһ��������£�ֻ�е�����·���˸��ٵ�ʱ�򣬲Ų�������·��
																		 * ����������ϸ�С�ڣ�����ȡ�� ���絽u���軻��2�Σ���u��v2���軻��1��
																		 * ����ԭ�еĵ�v2����2�εķ���
																		 */
						dist[v2] = dist[u] + cost;
						ttmp.w = dist[v2];
						transferTimes[v2] = transferTimes[u] + 1;
						/* ֻҪu�㻻�ˣ�ֻҪ�������·��������˴�����Ȼ��ԭ������+1 */
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