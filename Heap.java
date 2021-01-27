package traintransfer;

public abstract class Heap<T> {
	/*默认最小堆*/
	private Object[] heap;
	int capacity=0;
	int size=0;
	public Heap(int capacity, T sentry) {
		this.capacity=capacity;
		heap=new Object[capacity+1];
		heap[0]=sentry;
	}
	
	@SuppressWarnings("unchecked")
	public boolean buildHeap(int num,T[] arr) {
		/*以O(n)复杂度建立初始堆*/
		if(size!=0||num>capacity)
			return false;
		for(int i=0;i<num;) {
			heap[++i]=arr[i];
		}
		for(int i=size/2;i>0;i--) {
			int parent=i,child;
			T tmp=(T)heap[parent];
			while(parent*2<size) {
				child=parent*2;
				if((child!=size)&&compare((T)heap[child], (T)heap[child+1])==-1)
					child++;
				if(compare(tmp, (T)heap[child])==-1) {
					heap[parent]=heap[child];
					parent=child;
				}else break;
			}
			heap[parent]=tmp;
		}
		/*https://blog.csdn.net/wait_nothing_alone/article/details/72802586*/
		return true;
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
	
	@SuppressWarnings("unchecked")
	private void shifup(int node) {
		/*从node节点开始向上筛，一直到root节点为止*/
		boolean flag=false;
		if(node==1)return;
		while(node!=1&&!flag) {
			if(compare((T)heap[node], (T)heap[node/2])==-1) {
				swap(node,node/2);
			}else flag=true;
			node/=2;
		}
	}
	
	public boolean insert(T t) {
		if(size==capacity)return false;
		heap[++size]=t;
		shifup(size);
		return true;
	}
	@SuppressWarnings("unchecked")
	public T delete(int index) {
		/*返回被删除的元素*/
		if(index>size||size==0)return null;
		T res=(T)heap[index];
		heap[index]=heap[size--];
		if(compare((T)heap[index], res)==1)shifdown(index);
		else shifup(index);
		return res;
	}
	
	public T pop() {
		return delete(1);
	}
	
	@SuppressWarnings("unchecked")
	public T visit(int index) {
		return (T)heap[index];
	}
	
	private void swap(int i, int j) {
		Object tmp=heap[i];
		heap[i]=heap[j];
		heap[j]=tmp;
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
