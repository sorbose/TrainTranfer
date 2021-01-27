package traintransfer;

public abstract class HeapForDijkstra<T> {
	/*默认最小堆*/
	private Object[] heap;
	int capacity=0;
	int size=0;
	int[] position;//根据一个不变的下标，查出元素在堆里的位置
	int[] indexPos;//根据元素在堆里的位置，查出不变的下标
	public HeapForDijkstra(int capacity, T sentry) {
		this.capacity=capacity;
		heap=new Object[capacity+1];
		heap[0]=sentry;
		position=new int[capacity+1];
		indexPos=new int[capacity+1];
		for(int i=0;i<=capacity;i++)position[i]=-1;
		for(int i=0;i<=capacity;i++)indexPos[i]=-1;
	}
	
	@SuppressWarnings("unchecked")
	private void shifup(int node) {
		/*从node节点开始向上筛，一直到root节点为止*/
		boolean flag=false;
		if(node==1)return;
		while(node!=1&&!flag) {
			if(compare((T)heap[node], (T)heap[node/2])==-1) {
//				System.out.println(node+" "+(node/2));
				swap(node,node/2);
			}else flag=true;
			node/=2;
		}
	}
	
	public boolean insert(T t,int indexKey) {
		if(position[indexKey]!=-1) {
			heap[position[indexKey]]=t;
			shifup(position[indexKey]);
			return true;
		}
		if(size==capacity)return false;
		heap[++size]=t;
		position[indexKey]=size;
		indexPos[size]=indexKey;
		shifup(size);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public T visit(int index) {
		return (T)heap[index];
	}
	
	public T top() {
		if(size==0)return null;
		return visit(1);
	}
	
	@SuppressWarnings("unchecked")
	public T pop() {
		/*返回被删除的元素*/
		if(1>size||size==0)return null;
		T res=(T)heap[1];
		swap(1,size);
		position[indexPos[size]]=-1;
		indexPos[size]=-1;
		size--;
		if(compare((T)heap[1], res)==1)shifdown(1);
		else shifup(1);
		return res;
	}
	
	@SuppressWarnings("unchecked")
	private void shifdown(int root) {
		int tmp;
		boolean flag=false;
		while(root*2<=size&&!flag) {
			if(compare((T)heap[root], (T)heap[root*2])==1)
				tmp=root*2;
			else
				tmp=root;
			if(root*2+1<=size) {
				if(compare((T)heap[tmp], (T)heap[root*2+1])==1)
					tmp=root*2+1;
			}
			if(tmp!=root) {
				swap(tmp,root);
				root=tmp;
			}else flag=true;
		}
	}
	
	private void swap(int i, int j) {
		Object tmp=heap[i];
		heap[i]=heap[j];
		heap[j]=tmp;
		int t=position[indexPos[i]];
//		System.out.println("swap "+indexPos[i]+" "+indexPos[j]);
		position[indexPos[i]]=position[indexPos[j]];
		position[indexPos[j]]=t;
		t=indexPos[i];
		indexPos[i]=indexPos[j];
		indexPos[j]=t;
	}

	
	/*https://blog.csdn.net/caipengbenren/article/details/86680768*/

	/*a比b大返回1，a比b小返回-1，相等返回0*/
	public abstract int compare(T a,T b);
	
	public boolean isEmpty() {
		return size==0;
	}
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

	}

}
