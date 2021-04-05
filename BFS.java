package traintransfer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BFS {
	private boolean[] isVisited;
	public int[] parent;
	public int[] parTrainId;
	public BFSStation bfsRes;

	public void bfs(Graph g, int startV, int arrV, int maxTransCnt, int maxResNum, 
			String avoidTrainType,boolean isFuzzyDepSt,boolean isFuzzyArrSt) {
		isVisited = new boolean[g.Nv];
		parent = new int[g.Nv];
		parTrainId = new int[g.Nv];
		for (int i = 0; i < isVisited.length; i++)
			isVisited[i] = false;
		for (int i = 0; i < parent.length; i++)
			parent[i] = i;
		for (int i = 0; i < parTrainId.length; i++)
			parTrainId[i] = -1;
		MyQueue<VIdAndTransCnt> que = new MyQueue<>();
		que.push(new VIdAndTransCnt(startV, 0));
		if(isFuzzyDepSt) {
			for(int vi:ReadStations.stationsArr[startV].sameStationsId) {
				if(vi==-404)continue;
				que.push(new VIdAndTransCnt(vi, 0));
				isVisited[vi]=true;
			}
		}
		isVisited[startV] = true;
		// parent[startV]=-1;
		int transCnt, resCnt = 0;
		int currV;
		int[] arrVs;
		Heap<BFSStation> heap = new Heap<BFSStation>(5000, new BFSStation(-1, -1, null, -1)) {

			@Override
			public int compare(BFSStation a, BFSStation b) {

				if (a.weight == b.weight)
					return 0;
				return a.weight > b.weight ? 1 : -1;
			}
		};
		if(isFuzzyArrSt) {
			arrVs=new int[ReadStations.stationsArr[arrV].sameStationsId.length+1];
			for(int i=0;i<ReadStations.stationsArr[arrV].sameStationsId.length;i++) {
				arrVs[i]=ReadStations.stationsArr[arrV].sameStationsId[i];
			}
			arrVs[ReadStations.stationsArr[arrV].sameStationsId.length]=arrV;
		}else {
			arrVs=new int[1];
			arrVs[0]=arrV;
		}
		int currTransCnt = 0;
		while (!que.isEmpty()) {
			currV = que.front().vId;
			transCnt = que.front().transCnt;
			if (transCnt > maxTransCnt || (resCnt > maxResNum && transCnt > currTransCnt))
				break;
			currTransCnt = transCnt;

			que.pop();

			Trains tmpE = (Trains) g.v[currV].E;
			while (tmpE != null) {
				// System.out.println(que.size);
//				System.out.println(tmpE.depStName+" "+tmpE.arrStName);
//				if(transCnt > maxTransCnt&&resCnt>=maxResNum)break;
				if (!RailNet.isTheTypeOfTrainSuitable(tmpE, avoidTrainType)) {
					tmpE = (Trains) tmpE.nextEdge;
					continue;
				}
				int v2 = tmpE.v2;
				if (isArrStation(v2,arrVs)) {
					if (!heap.insert(new BFSStation(currV, tmpE.trainCodeId, this, v2)))
						break;
					resCnt++;
					tmpE = (Trains) tmpE.nextEdge;
					continue;
				}
				if (!isVisited[v2]) {
					isVisited[v2] = true;
					que.push(new VIdAndTransCnt(v2, transCnt + 1));
					parent[v2] = currV;
					parTrainId[v2] = tmpE.trainCodeId;
				}
				tmpE = (Trains) tmpE.nextEdge;
			}
		}
		bfsRes=createBFSRes(heap,maxResNum);
	}

	private boolean isArrStation(int v,int[] Vs) {
		if(v<0) {
			System.out.println("except BFS class: vId <0");
			return false;
		}
		for(int vi:Vs) {
			if(vi==v)return true;
		}
		return false;
	}

	private BFSStation createBFSRes(Heap<BFSStation> heap, int resNum) {
		BFSStation head=null,tail;
		if (heap.isEmpty())
			return null;
		for (int i = 0; i < resNum; i++) {
			if(heap.isEmpty())break;
			if (head == null) {
				head = heap.pop();
				head.next = null;
			} else {
				head.insert(heap.pop());
			}
		}
		for(tail=head;tail.next!=null;tail=tail.next);
		tail.next=head;
		BFSStation res=head.next;
		head.next=null;
		/*生成一个倒序链表*/
		return res;

	}

	public static void main(String[] args) {

	}

}

class VIdAndTransCnt {
	int vId, transCnt;

	public VIdAndTransCnt(int vId, int transCnt) {
		super();
		this.vId = vId;
		this.transCnt = transCnt;
	}

}

class BFSStation {
	/* 保存距到达站最后一站的id和前往最终目的地的车次id */
	int vId;
	int trainCodeId;
	int weight;
	int arrV;
	BFSStation next = null;

	public BFSStation(int vId, int trainCodeId, BFS bfs, int arrV) {
		super();
		this.vId = vId;
		this.trainCodeId = trainCodeId;
		if (vId >= 0)
			weight = getWeight(vId, trainCodeId, bfs, arrV);
		else
			weight = -100000000;
	}

	private int getWeight(int vId, int trainCodeId, BFS bfs, int arrV) {
		this.arrV=arrV;
		bfs.parent[arrV] = vId;
		bfs.parTrainId[arrV] = trainCodeId;
		if (vId == arrV) {
			return 0;
		}
		int resWeight = 0;
		String aTrainBasicDescInf;
		String pattern = ".+(\\d{2}:\\d{2})~(\\d{2}:\\d{2}).*";
		Pattern r = Pattern.compile(pattern);
		int depTime, arrTime;
		for (int i = arrV; bfs.parent[i] != i; i = bfs.parent[i]) {
//			System.out.println(ReadStations.stationsArr[bfs.parent[i]].name);
//			System.out.println(ReadStations.stationsArr[i].name);
			aTrainBasicDescInf = Trains.getATrainBasicDescInf(bfs.parTrainId[i],
					ReadStations.stationsArr[bfs.parent[i]].name, ReadStations.stationsArr[i].name);
			aTrainBasicDescInf = aTrainBasicDescInf.replace(" ", "").replace("：", ":").replace("（", "(").replace("）",
					")");
			Matcher m = r.matcher(aTrainBasicDescInf);
//			System.out.println(r.pattern());
//			System.out.println(aTrainBasicDescInf);
//			System.out.println(m.find());
			if (m.find()) {
//				System.out.println(m.groupCount());
//				System.out.println(m.group(0));
//				System.out.println(aTrainBasicDescInf);
				depTime = Trains.strTimeToInt(m.group(1), 0);
				int index=aTrainBasicDescInf.indexOf('+');
				int day;
				if(index>0) {
					day=aTrainBasicDescInf.charAt(index+1)-'0';
				}else {
					day=0;
				}
				arrTime = Trains.strTimeToInt(m.group(2), day);
//				System.out.println(m.group(1)+" "+m.group(2)+" "+day);
//				System.out.println(depTime+" "+arrTime);
//				if (m.groupCount() == 4) {
//					System.out.println(m.group(1)+" "+m.group(2)+" "+m.group(3)+" "+m.group(4));
//					arrTime = Trains.strTimeToInt(m.group(2), Integer.parseInt(m.group(3)));
//				} else {
//					System.out.println(m.group(1)+" "+m.group(2)+" "+m.group(3));
//					arrTime = Trains.strTimeToInt(m.group(2), 0);
//				}
				resWeight += (arrTime - depTime);
			}
		}
//		System.out.println("total: "+resWeight);
		return resWeight;
	}

	void insert(BFSStation li) {
		li.next = this.next;
		this.next = li;
	}

}
