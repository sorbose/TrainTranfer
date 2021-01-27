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
	public Graph(int nv) {
		Nv=nv;
		v=new Vertex[Nv];

	}
	public void addEdge(Edge newE) {
		int v1=newE.v1;
//		System.out.println(v[v1]);
//		System.out.println(v1);
		if(v[v1].E==null) {
			v[v1].E=newE;
			newE.nextEdge=null;
		}else {
			newE.nextEdge=v[v1].E.nextEdge;
			v[v1].E.nextEdge=newE;
		}
		this.Ne++;
	}
	
}
class Vertex{
	int id;
	public Vertex(int id) {
		this.id=id;
	}
	Edge E=null;
}
class Edge{
	int v1,v2,w;
	Edge nextEdge=null;
	public Edge(int v1, int v2, int w) {
		super();
		this.v1 = v1;
		this.v2 = v2;
		this.w = w;
	}
	
	
}