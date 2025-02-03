package bstmap;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B<K , V>{
    private Node root = null;
    private int size = 0;

    public BSTMap(){

    }

    /** Removes all of the mappings from this map. */
    public void clear(){
        this.root = null;
        this.size = 0;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        return containsKeyFinding(this.root , key);
    }

    private boolean containsKeyFinding(Node x , K key){
        if(x == null) return false;

        int cmp = key.compareTo(x.key);
        if(cmp > 0) return containsKeyFinding(x.right , key);
        else if (cmp < 0) return containsKeyFinding(x.left , key);
        else return true;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key){
        return getFinding(this.root , key);
    }

    private V getFinding(Node x , K key){
        if(x == null ) return null;

        int cmp = key.compareTo(x.key);
        if(cmp > 0) return getFinding(x.right , key);
        else if (cmp < 0) return getFinding(x.left , key);
        else{
            return x.value;
        }
    }

    /* Returns the number of key-value mappings in this map. */
    public int size(){
        return this.size;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value){
        this.root = putFinding(this.root , key , value);
        this.root.color = "BLACK";
        size++;
    }

    private Node putFinding(Node x ,K key , V value){
        if(x == null ){
            return new Node(key , value ,"RED");
        }
        int cmp = key.compareTo(x.key);
        if(cmp > 0) x.right = putFinding(x.right , key , value);
        else if (cmp < 0) x.left = putFinding(x.left , key , value);
        else{
            x.value = value;
            return x;
        }

        if (isRed(x.left) && isRed(x.left.left)){
            x = rotateRight(x);
        }
        else if (!isRed(x.left) && isRed(x.right)){
            x = rotateLeft(x);
        }
        else if (isRed(x.left)  & isRed(x.right)){
            x = flipColor(x);
        }

        return x;
    }

    public void printInOrder(){
        printInOrderFinding(this.root);
    }

    private void printInOrderFinding(Node x){
        if(x == null ) return;

        printInOrderFinding(x.left);
        System.out.println(x.key + ":" +  x.value);
        printInOrderFinding(x.right);
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet(){
        throw new UnsupportedOperationException() ;
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key){
        throw new UnsupportedOperationException() ;
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value){
        throw new UnsupportedOperationException() ;
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException() ;
    }

    private boolean isRed (Node x){
        return x != null && x.color.equals("RED");
    }

    private Node rotateLeft(Node x){
        Node xRight = x.right;
        x.right = xRight.left;
        xRight.left = x;
        x.color = xRight.color;
        xRight.color = x.color;
        return xRight;
    }

    private Node rotateRight(Node x){
        Node xLeft = x.left;
        x.left = xLeft.right;
        xLeft.right = x;
        x.color = xLeft.color;
        xLeft.color = x.color;
        return xLeft;
    }

    private Node flipColor(Node x){
        if (x.color.equals("RED")) x.color = "BLACK";
        else x.color = "RED";
        x.left.color = x.right.color = "BLACK";
        return x;
    }

    private class Node{
        private K key;
        private V value;
        private Node left = null;
        private Node right = null;
        private String color;


        private Node (K key , V value , String color) {
            this.key = key;
            this.value = value;
            this.color = color;
        }
    }


}
