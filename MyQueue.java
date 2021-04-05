package traintransfer;

/*继承这个抽象类，重写所有抽象方法*/
public class MyQueue<T> {

	protected LinkedNode<T> first = null;
	protected LinkedNode<T> last = null;
	protected int size = 0;

	/* 返回队列的元素个数 */
	int getSize() {
		return size;
	}

	/* 判断队列是否为空，是返回true */
	boolean isEmpty() {
		return size == 0;
	}

	/* 访问（但不删除）队列的第一个元素，并返回 */
	T front() {
		if(isEmpty())return null;
		return first.t;
	}

	/* 删除队列的第一个元素，如果队列为空，返回false */
	boolean pop() {
		if (isEmpty())
			return false;
		first = first.next;
		size--;
		return true;
	}

	/* 在队列的最后添加一个元素element，如果添加成功，返回true */
	boolean push(T element) {
		LinkedNode<T> tmp = new LinkedNode<T>(element);
		if (isEmpty()) {
			first = last = tmp;
		} else {
			last.next = tmp;
			last=tmp;
		}
		size++;
		return true;

	}

	private class LinkedNode<E> {
		LinkedNode<E> next = null;
		E t;
		LinkedNode(E element) {
			t = element;
		}
	}
}
