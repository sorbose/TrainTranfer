package traintransfer;

public class Dijkstra {
	static int INF=0x3f3f3f3f;
	int dist[];
	int parent[];
	public void dijkstra(Graph g,int startV) {
		int Nv=g.Nv;
		boolean isVis[]=new boolean[Nv];
		dist=new int[Nv];
		parent=new int[Nv];
		for(int i=0;i<Nv;i++)isVis[i]=false;
		for(int i=0;i<Nv;i++)dist[i]=INF;
		for(int i=0;i<Nv;i++)parent[i]=i;
		dist[startV]=0;
		HeapForDijkstra<Edge> que=new HeapForDijkstra<Edge>(Nv,new Edge(-1, -1, -INF)) {
			
			@Override
			public int compare(Edge a, Edge b) {
				return a.w>b.w?1:-1;
			}
		};
//		for(int i=0;i<Nv;i++) {
//			que.insert(new Edge(startV, i, INF), i);
//		}
		
		
		que.insert(new Edge(startV, startV, 0),startV);
		Edge tmpE=null;
		while(!que.isEmpty()) {
			tmpE=que.top();
			que.pop();
//			System.out.println(tmpE.v1+" "+tmpE.v2+" "+tmpE.w);
			int u=tmpE.v2;
			if(isVis[u])continue;
			isVis[u]=true;
			parent[u]=tmpE.v1;
			Edge ttmp=g.v[u].E;
			for(;ttmp!=null;) {
				int v2=ttmp.v2;
				int cost=ttmp.w;
				if(!isVis[v2]&&dist[v2]>dist[u]+cost) {
					dist[v2]=dist[u]+cost;
					ttmp.w=dist[v2];
					que.insert(ttmp, v2);
				}
				ttmp=ttmp.nextEdge;
			}
		}
		
//		for(int i=0;i<Nv;i++)System.out.println(isVis[i]);

	}
	public static void main(String[] args) {
		
	}

}

class Graph{
	Vertex[] v=null;
	int Nv;
	int Ne=0;
	
	public void addEdge(Edge newE) {
		addEdgeInOrOut(newE,true);
		try {
			addEdgeInOrOut((Edge) newE.clone(),false);
		} catch (CloneNotSupportedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		this.Ne++;
	}
	private void addEdgeInOrOut(Edge newE,boolean isOutEdge) {
		/*isOutEdge为false时。建立逆邻接表*/
		int vId=isOutEdge?newE.v1:newE.v2;
		Edge E=isOutEdge?v[vId].getE_out():v[vId].getE_in();
		if(E==null) {
			if(isOutEdge)v[vId].setE_out(newE);
			else v[vId].setE_in(newE);
			newE.nextEdge=null;
		}else {
			newE.nextEdge=E.nextEdge;
			E.nextEdge=newE;
		}
	}
	void setInOrOutAdjList(boolean isOutAdjList) {
		for(int i=0;i<v.length;i++) {
			v[i].setInOrOutVertex(isOutAdjList);
		}
	}
}

class Vertex{
	int id;
	public Vertex(int id) {
		this.id=id;
	}
	Edge E=null;
	private Edge E_out=null;
	private Edge E_in=null;
	private Edge E0;
	public Edge getE_out() {
		return E_out;
	}
	public Edge getE_in() {
		return E_in;
	}
	public void setE_out(Edge e_out) {
		E_out = e_out;
	}
	public void setE_in(Edge e_in) {
		E_in = e_in;
	}
	void setInOrOutVertex(boolean isOutAdjList) {
		E=isOutAdjList?E_out:E_in;
	}
	void saveHeadNode() {
		E0=E;
	}
	void restoreHeadNode() {
		E=E0;
	}
}
class Edge implements Cloneable{
	int v1,v2,w;
	Edge nextEdge=null;
	public Edge(int v1, int v2, int w) {
		super();
		this.v1 = v1;
		this.v2 = v2;
		this.w = w;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO 自动生成的方法存根
		return super.clone();
	}
	
	
}