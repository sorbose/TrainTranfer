package traintransfer;

import java.util.Scanner;

public class Main2 extends HeapForDijkstra<Integer> {

	public Main2(int capacity, Integer sentry) {
		super(capacity, sentry);
		// TODO 自动生成的构造函数存根
	}

	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		int n=sc.nextInt();
		Main2 m1=new Main2(n,-100000);
		int cnt=0;
		while(n--!=0) {
			int op=sc.nextInt();
			if(op==1) {
				int t=sc.nextInt();
				m1.insert(t,cnt++);
			}else if(op==2) {
				System.out.println(m1.top());
			}else {
				m1.pop();
			}
		}

	}
	@Override
	public int compare(Integer a, Integer b) {
		return a>b?1:-1;
	}

}