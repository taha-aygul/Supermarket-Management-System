public interface MyHashTable<K extends Comparable<K>, V extends Comparable<V> & Node<V>>  {


    public void put(K key, V value);
    public boolean isHashTableTooFull();
    public void resize();
    public V get(K key) ;
    public void remove(K key) ;

    public int GetHash(K key);

}
