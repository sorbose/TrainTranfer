package traintransfer;

/*�̳���������࣬��д���г��󷽷�*/
public class MyQueue<T> {

	protected LinkedNode<T> first = null;
	protected LinkedNode<T> last = null;
	protected int size = 0;

	/* ���ض��е�Ԫ�ظ��� */
	int getSize() {
		return size;
	}

	/* �ж϶����Ƿ�Ϊ�գ��Ƿ���true */
	boolean isEmpty() {
		return size == 0;
	}

	/* ���ʣ�����ɾ�������еĵ�һ��Ԫ�أ������� */
	T front() {
		if(isEmpty())return null;
		return first.t;
	}

	/* ɾ�����еĵ�һ��Ԫ�أ��������Ϊ�գ�����false */
	boolean pop() {
		if (isEmpty())
			return false;
		first = first.next;
		size--;
		return true;
	}

	/* �ڶ��е�������һ��Ԫ��element�������ӳɹ�������true */
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
