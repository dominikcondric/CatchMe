package utility;

import java.util.Iterator;
import java.util.Random;

public class ObjectPool<T extends Resetable> implements Iterable<T> {
	private T[] pool;
	private int last = 0;
	
	public interface PoolElementFactory<E> {
		E generateElement();
	}
	
	@SuppressWarnings("unchecked")
	public ObjectPool(PoolElementFactory<T> factory, int capacity) {
		pool = (T[]) new Resetable[capacity];
		for (int i = 0; i < capacity; ++i) 
			pool[i] = factory.generateElement();
	}
	
	public T useNewElement(boolean random) {
		if (last == pool.length)  
			return null;
		
		int unusedIndex = last;
		if (random) {
			unusedIndex = new Random().nextInt(pool.length - last) + last;
			swap(last, unusedIndex);
		}
		
		pool[last].reset();
		return pool[last++];
	}
	
	public void removeElement(T element) {
		if (last == 0) 
			return;
		
		for (int i = 0; i < last; ++i) {
			if (element == pool[i]) {
				swap(i, last-1);
				last--;
				break;
			}
		}
	}
	
	public int getUsedCount() {
		return last;
	}
	
	private void swap(int first, int last) {
		T tmp = pool[first];
		pool[first] = pool[last];
		pool[last] = tmp;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < last;
			}

			@Override
			public T next() {
				return pool[index++];
			}
		};
	}
	
}
