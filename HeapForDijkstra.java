package traintransfer;

public abstract class HeapForDijkstra<T> {
	/*Ĭ����С��*/
	private Object[] heap;
	int capacity=0;
	int size=0;
	int[] position;//����һ��������±꣬���Ԫ���ڶ����λ��
	int[] indexPos;//����Ԫ���ڶ����λ�ã����������±�
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
		/*��node�ڵ㿪ʼ����ɸ��һֱ��root�ڵ�Ϊֹ*/
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
		/*���ر�ɾ����Ԫ��*/
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

	/*a��b�󷵻�1��a��bС����-1����ȷ���0*/
	public abstract int compare(T a,T b);
	
	public boolean isEmpty() {
		return size==0;
	}
	
	public static void main(String[] args) {
		// TODO �Զ����ɵķ������

	}

}
