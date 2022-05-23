package utility;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;

public class ImmutableArray<T> implements Iterable<T> {
	private Array<T> array;
	
	public ImmutableArray(Array<T> array) {
		this.array = array;
	}
	
	public Iterator<T> iterator() {
		return array.iterator();
	}
}
