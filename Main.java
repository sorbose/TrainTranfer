package traintransfer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Main {
	public static void main(String[] args){
//		FileInputStream fis;
//		try {
//			fis = new FileInputStream("D:\\浏览器下载\\P3371_2.in");
//			System.setIn(fis);
//		} catch (FileNotFoundException e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();
//		}
//		try {
//			PrintStream ps=new PrintStream(new FileOutputStream("D:\\浏览器下载\\P3371_2.txt"));
//			System.setOut(ps);
//		} catch (FileNotFoundException e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();
//		}
		int gcCnt=0;
		Dijkstra d = new Dijkstra();
		Scanner sc = new Scanner(System.in);
		//while (sc.hasNext()) {
			int Nv = sc.nextInt();
			int Ne = sc.nextInt();
			int s = sc.nextInt();
			Graph g = new Graph();
			for (int i = 0; i < Nv; i++) {
				g.v[i] = new Vertex(i);
			}
			int u, v, w;
			for (int i = 0; i < Ne; i++) {
				u = sc.nextInt();
				v = sc.nextInt();
				w = sc.nextInt();
				u--;
				v--;
				if (g.v[u].E == null)
					g.v[u].E = new Edge(u, v, w);
				else {
					Edge tmpE = new Edge(u, v, w);
					tmpE.nextEdge = g.v[u].E.nextEdge;
					g.v[u].E.nextEdge = tmpE;
				}
				//if(++gcCnt%10000==0)System.gc();
			}
			
			d.dijkstra(g,s-1);
			
			for (int i = 0; i < Nv; i++) {
				System.out.print(d.dist[i] + " ");
			}
		//}
		sc.close();

	}
}