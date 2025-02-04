package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V>{

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */

    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;   /** Node 的总数 */
    private double maxLoad = 0.75;   /** Load : N/M ; N : numbers of Nodes ; M numbers of the buckets */
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this.buckets = createTable(16);
        this.size = 0;
    }


    public MyHashMap(int initialSize) {
        this.buckets = createTable(initialSize);
        this.size = 0;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.buckets = createTable(initialSize);
        this.size = 0;
        this.maxLoad = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key , value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();  /** 占位符 先用着 */
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] x = new Collection[tableSize];
        for (int i = 0 ; i < tableSize ; i++){
            x[i] = createBucket();
        }
        return x;

    }

    private int hashfunction(K key){
        int code = Math.abs(key.hashCode());
        return code % this.buckets.length;
    }

    @Override
    /** Removes all of the mappings from this map. */
    public void clear(){
        this.buckets = createTable(16);
        this.size = 0;
    }

    @Override
    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        if (key == null) return false;

        int index = hashfunction(key);
        for(Node item : this.buckets[index]){
            if(item.key.equals(key)){
                return true;
            }
        }
        return false;
    }

    @Override
    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key){
        if (key == null) return null;

        int index = hashfunction(key);
        for(Node item : this.buckets[index]){
            if(item.key.equals(key)){
                return item.value;
            }
        }
        return null;
    }

    @Override
    /** Returns the number of key-value mappings in this map. */
    public int size(){
        return size;
    }

    @Override
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value){
        if (key == null) return;

        int index = hashfunction(key);
        if(!containsKey(key)) {
            this.buckets[index].add(new Node(key , value));
            check();
        }
        else{
            for(Node item : this.buckets[index]){
                if(item.key.equals(key)){
                    item.value = value;  /** 遍历的是拷贝还是指针？ */
                }
            }
        }
    }
    private void check(){
        double nowLoad = (double)this.size / (double)this.buckets.length;
        if(nowLoad >= this.maxLoad){
            resize();
        }
    }

    private void resize(){
        Collection<Node>[] x;
        x = this.buckets.clone();
        this.buckets = createTable(x.length * 2);
        for (int i = 0 ; i < x.length ; i++){
            for(Node item : x[i]){
                put(item.key , item.value);
            }
        }
    }

    @Override
    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet(){
        Set<K> x = new HashSet<>();
        for (int i = 0 ; i < this.buckets.length ; i++){
            for(Node item : this.buckets[i]){
                x.add(item.key);
            }
        }
        return x;
    }
    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator(this.buckets);
    }


    private class MyHashMapIterator implements Iterator<K>{

        private final Collection<Node>[] ToSearchBuckets;
        private int index;
        private int numbers;

        private MyHashMapIterator(Collection<Node>[] x){
            this.ToSearchBuckets = x;
            numbers = 0;
            index = 0;
        }
        @Override
        public boolean hasNext(){
            while(index < ToSearchBuckets.length) {
               if(!ToSearchBuckets[index].isEmpty() && ToSearchBuckets[index].size() > numbers) {return true;}
               numbers = 0;
               index++;
            }
            return false;
        }

        @Override
        public K next(){
            if(!hasNext()){
                return null;
            }
            else {
                while (index < ToSearchBuckets.length) {
                    Collection<Node> currentBucket = ToSearchBuckets[index];
                    if (currentBucket.size() > numbers) {
                       Node node = (Node) currentBucket.toArray()[numbers];
                       numbers++;
                       return node.key;
                    }
                    numbers = 0;
                    index++;
                }
            }
            return null;
        }
    }

    @Override
    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key){
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value){
        throw new UnsupportedOperationException();
    }
}
