package traintransfer;

public class DijkstraForRail {
	static int INF = 0x3f3f3f3f;
	public int dist[];
	public int parent[];
	public int parTrainId[];
	/*parent���ڽӱ��ã�child�����ڽӱ���*/
	public int child[];
	public int chiTrainId[];
	private boolean isVis[];
	private int[] transferTimes;
	private HeapForDijkstra<Trains> que;

	public void fastestArrival(Graph g, int startV, int startTime, int minSameStationTransferInterval,
			int minSameCityTransferInterval,String filterTrainType, boolean isFuzzyDepSt,boolean isFuzzyArrSt,int arrV) {
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
				dist[startV], dist[startV], 0), startV);
		if(isFuzzyDepSt) {
			for (int sameDepStId : ReadStations.stationsArr[startV].sameStationsId) {
				if(sameDepStId==-404)continue;
				dist[sameDepStId]= (startTime-minSameStationTransferInterval+1440)%1440;
				que.insert(new Trains(startV, sameDepStId, -555, ReadStations.stationsArr[startV].name,
						ReadStations.stationsArr[sameDepStId].name, 
						dist[sameDepStId], dist[sameDepStId], 0), sameDepStId);
			}
		}
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
				int virTrArrTime;
				if(u==arrV||otherStId==arrV&&isFuzzyArrSt) {
					virTrArrTime=virTrDepTime;
				}else {
				virTrArrTime=virTrDepTime+minSameCityTransferInterval-minSameStationTransferInterval;}
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
	
	private void traverseAllTheTrainsInAStation_Inverse(Trains train, int currVId, 
			int currTime,int minIntv,String filterTrainType) {
		/*��currVId�����ı߽��б��������������������ʽ������ͷ�ڵ���train*/
		for (; train != null;) {
			if(!RailNet.isTheTypeOfTrainSuitable(train, filterTrainType)) {
				train = (Trains) train.nextEdge;continue;
			}
			/*currTimeָ�����뿪currVId������ʱ��*/
			/*waitingTime�ǵȳ���ʱ�䣬������һ�����³�ʱ����
			 * ����һ�����ϳ�ʱ�䣨��currVId���������ʱ�䣬��currTime��ֹ����������ʱ��*/
			int waitingTime = (currTime-train.arrTime + 1440) % 1440;
			int ridingTime = train.w;/* Ĭ�ϵ�Ȩֵ���弴Ϊ�˳�ʱ�� */
			if (waitingTime < minIntv) {
				/* ����ʱ�䲻�����ϲ��ϳ���ԭ�ض��һ�� */
				waitingTime += 1440;
			}
			train.w = waitingTime + ridingTime;
			int v1 = train.v1;
			int cost = train.w;
			/*
			 * dist[v2]==dist[u]+costʱ�� ������񶼿��ԣ������ٱȽϻ��˴�����ѡ����ٵ�һ��
			 */
			if (!isVis[v1] && dist[v1] >= dist[currVId] + cost) {
				/* ������ȣ�ѡ�����ٵ� */
				if ((dist[v1] > dist[currVId] + cost)
						|| (dist[v1] == dist[currVId] + cost && transferTimes[currVId] < transferTimes[v1])) {
					/*
					 * �ں�ʱһ��������£�ֻ�е�����·���˸��ٵ�ʱ�򣬲Ų�������·�� ����������ϸ�С�ڣ�����ȡ�� ���絽u���軻��2�Σ���u��v2���軻��1��
					 * ����ԭ�еĵ�v2����2�εķ���
					 */
					dist[v1] = dist[currVId] + cost;
					train.w = dist[v1];
					transferTimes[v1] = transferTimes[currVId] + 1;
					/* ֻҪu�㻻�ˣ�ֻҪ�������·��������˴�����Ȼ��ԭ������+1 */
//					if(v1==3111) {
//						System.out.println(dist[v1]+" "+waitingTime+" "+currTime+" "+train.arrTime+
//								" "+train.v1+" "+train.v2);
//					}
					que.insert(train, v1);
				}
			}
			train = (Trains) train.nextEdge;
		}
	}
	
	public void lastestDeparture(Graph g, int arrV, int arrTime, int minSameStationTransferInterval,
			int minSameCityTransferInterval,String filterTrainType, boolean isFuzzyDepSt,boolean isFuzzyArrSt,int startV) {
		/*
		 * ����һ��������ʱ��arrTime���ڵ���ʱ�䲻����arrTime��ǰ���£�
		 * ���������������ʱ�䣬 ��������ж��֣������ķ���δ�������ŵģ����ٻ��ˡ�������·��
		 */
		int Nv = g.Nv;
		isVis = new boolean[Nv];
		dist = new int[Nv];/*dist[u]��ֵΪ��u��arrV������Ҫ��ǰ�����ķ�����*/
		/*��һ����Ȩֵ�Ķ���Ϊ����վ���������ʱ��Ϊt��
		һ��t1������t2ʱ�̵��ﱾվ�ıߣ�������㻻��ʱ��ģ�����Ϊt-t1,����Ϊt-t1+1440*/
		child = new int[Nv];/*u�����һ���ڵ���child[u]*/
		chiTrainId = new int[Nv];
		transferTimes = new int[Nv];/* transferTimes[i]��ʾ����i�Ļ��˴��� */
		for (int i = 0; i < Nv; i++)
			isVis[i] = false;
		for (int i = 0; i < Nv; i++)
			dist[i] = INF;
		for (int i = 0; i < Nv; i++)
			child[i] = i;
		for (int i = 0; i < Nv; i++)
			chiTrainId[i] = -1;
		for (int i = 0; i < Nv; i++)
			transferTimes[i] = 0;

		dist[arrV] = 0;
		que = new HeapForDijkstra<Trains>(Nv, new Trains(-INF)) {
			@Override
			public int compare(Trains a, Trains b) {
				return a.w > b.w ? 1 : -1;
			}
		};
		int arrTimeToInsertInit=(arrTime+minSameStationTransferInterval)%1440;
		que.insert(new Trains(arrV, arrV, -100, ReadStations.stationsArr[arrV].name,
				ReadStations.stationsArr[arrV].name, 
				arrTimeToInsertInit, 
				arrTimeToInsertInit, 0), arrV);
		if(isFuzzyArrSt) {
			for (int sameArrStId : ReadStations.stationsArr[arrV].sameStationsId) {
				if(sameArrStId==-404)continue;
				dist[sameArrStId]= 0;
				que.insert(new Trains(sameArrStId, arrV, -555, ReadStations.stationsArr[sameArrStId].name,
						ReadStations.stationsArr[arrV].name, 
						arrTimeToInsertInit, arrTimeToInsertInit, 0), sameArrStId);
			}
		}
		Trains tmpE = null;
		while (!que.isEmpty()) {
			tmpE = que.top();
			que.pop();
			int u = tmpE.v1;
			if (isVis[u])
				continue;
			isVis[u] = true;
			child[u] = tmpE.v2;
			chiTrainId[u] = tmpE.trainCodeId;
			Trains ttmp = (Trains) g.v[u].E;
			/*
			 * Ȩֵ���Ե�����һվ����������һվֹ��������ʱ�䣬�÷��ӱ�ʾ ��Ϊ�����֣�һ�ǵȳ�ʱ�䣬���ǳ˳�ʱ��
			 */
			//int currTime = (arrTime + 144000 - dist[tmpE.v2]) % 1440;
			int currTime =tmpE.depTime;
			/*��ǰʱ��Ҳ���뿪u������ʱ��*/
			traverseAllTheTrainsInAStation_Inverse
			(ttmp, u,currTime, minSameStationTransferInterval,filterTrainType);

			/* �ٿ�����վ���ˣ���վ��u��u��ÿ��ͬ�ǳ�վ�����һ������𳵰ڶ� */
			for (int i = 0; i < ReadStations.stationsArr[u].sameStationsId.length; i++) {
				int otherStId = ReadStations.stationsArr[u].sameStationsId[i];
				if(otherStId==-404)continue;
				int virTrDepTime=(currTime-minSameCityTransferInterval+1440)%1440;
				int virTrArrTime=virTrDepTime+minSameCityTransferInterval-minSameStationTransferInterval;
				int virTrArrDay=virTrArrTime/1440;
				virTrArrTime%=1440;
				if(u==startV||otherStId==startV&&isFuzzyDepSt) {
					virTrDepTime=virTrArrTime;
					virTrArrDay=0;
				}

				Trains virtualCityTrain = new Trains(otherStId, u, -555, ReadStations.stationsArr[otherStId].name,
						ReadStations.stationsArr[u].name, virTrDepTime,
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