import java.util.Map.Entry;


public class MyEntry<K, V> implements Entry<K, V> {
	private final K key;
	private V value;
	public MyEntry(K key, V value) {
		// TODO Auto-generated constructor stub
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public V getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public V setValue(V value) {
		// TODO Auto-generated method stub
		V old = this.value;
		this.value = value;
		return old;
	}

}
