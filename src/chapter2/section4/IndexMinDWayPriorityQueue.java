package chapter2.section4;

import java.util.NoSuchElementException;

/**
 * Created by Rene Argento on 31/12/17.
 */
public class IndexMinDWayPriorityQueue<Key extends Comparable<Key>> {

    private Key[] keys;
    private int[] pq; // Holds the indices of the keys
    private int[] qp; // Inverse of pq -> qp[i] gives the position of i in pq[] (the index j such that pq[j] is i).
                      // qp[pq[i]] = pq[qp[i]] = i
    private int size = 0;

    private int numberOfChildrenPerNode;

    @SuppressWarnings("unchecked")
    public IndexMinDWayPriorityQueue(int size, int numberOfChildrenPerNode) {
        keys = (Key[]) new Comparable[size + 1];
        pq = new int[size + 1];
        qp = new int[size + 1];

        for(int index = 0; index < qp.length; index++) {
            qp[index] = -1;
        }

        this.numberOfChildrenPerNode = numberOfChildrenPerNode;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public boolean contains(int index) {
        return qp[index] != -1;
    }

    //Return key associated with index
    public Key keyOf(int index) {
        if(!contains(index)) {
            throw new NoSuchElementException("Index is not in the priority queue");
        }

        return keys[index];
    }

    public void insert(int index, Key key) {
        if(contains(index)) {
            throw new IllegalArgumentException("Index is already in the priority queue");
        }

        if(size != keys.length - 1) {
            size++;

            keys[index] = key;
            pq[size] = index;
            qp[index] = size;

            swim(size);
        }
    }

    // Remove a minimal key and return its index
    public int deleteMin() {
        if(size == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }

        int minElementIndex = pq[1];
        exchange(1, size);
        size--;
        sink(1);

        keys[pq[size + 1]] = null;
        qp[pq[size + 1]] = -1;

        return minElementIndex;
    }

    public void delete(int i) {
        if(!contains(i)) {
            throw new NoSuchElementException("Index is not in the priority queue");
        }

        int index = qp[i];

        exchange(index, size);
        size--;

        swim(index);
        sink(index);

        keys[i] = null; // Same thing as keys[pq[size + 1]] = null
        qp[i] = -1;     // Same thing as qp[pq[size + 1]] = -1;
    }

    // Change the key associated with index to key argument
    public void changeKey(int index, Key key) {
        if(!contains(index)) {
            throw new NoSuchElementException("Index is not in the priority queue");
        }

        keys[index] = key;

        swim(qp[index]);
        sink(qp[index]);
    }

    public void decreaseKey(int index, Key key) {
        if (!contains(index)) {
            throw new NoSuchElementException("Index is not in the priority queue");
        }
        if (key.compareTo(keys[index]) >= 0) {
            throw new IllegalArgumentException("Calling decreaseKey() with given argument would not strictly decrease the key");
        }

        keys[index] = key;
        swim(qp[index]);
    }

    public void increaseKey(int index, Key key) {
        if (!contains(index)) {
            throw new NoSuchElementException("Index is not in the priority queue");
        }
        if (key.compareTo(keys[index]) <= 0) {
            throw new IllegalArgumentException("Calling increaseKey() with given argument would not strictly increase the key");
        }

        keys[index] = key;
        sink(qp[index]);
    }

    public Key minKey() {
        if (size == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }

        return keys[pq[1]];
    }

    public int minIndex() {
        if (size == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }

        return pq[1];
    }

    // parent of n: (n + (d - 2)) / d
    private void swim(int index) {
        int parentIndex = (index + (numberOfChildrenPerNode - 2)) / numberOfChildrenPerNode;

        while (parentIndex >= 1 && more(parentIndex, index)) {
            exchange(parentIndex, index);

            index = parentIndex;
            parentIndex = (index + (numberOfChildrenPerNode - 2)) / numberOfChildrenPerNode;
        }
    }

    // children of n: ((n * d - (d - 2)), ..., (n * d + 1))
    private void sink(int index) {
        int childSmallestIndex = (index * numberOfChildrenPerNode - (numberOfChildrenPerNode - 2));
        int childHighestIndex = (index * numberOfChildrenPerNode + 1);

        while (childSmallestIndex <= size) {

            int selectedChildIndex = childSmallestIndex;

            for(int childIndex = childSmallestIndex + 1; childIndex <= childHighestIndex; childIndex++) {
                if (childIndex <= size && more(selectedChildIndex, childIndex)) {
                    selectedChildIndex = childIndex;
                }
            }

            if (less(selectedChildIndex, index)) {
                exchange(index, selectedChildIndex);
            } else {
                break;
            }

            index = selectedChildIndex;
            childSmallestIndex = (index * numberOfChildrenPerNode - (numberOfChildrenPerNode - 2));
            childHighestIndex = (index * numberOfChildrenPerNode + 1);
        }
    }

    private boolean less(int keyIndex1, int keyIndex2) {
        return keys[pq[keyIndex1]].compareTo(keys[pq[keyIndex2]]) < 0;
    }

    private boolean more(int keyIndex1, int keyIndex2) {
        return keys[pq[keyIndex1]].compareTo(keys[pq[keyIndex2]]) > 0;
    }

    private void exchange(int keyIndex1, int keyIndex2) {
        int temp = pq[keyIndex1];
        pq[keyIndex1] = pq[keyIndex2];
        pq[keyIndex2] = temp;

        qp[pq[keyIndex1]] = keyIndex1;
        qp[pq[keyIndex2]] = keyIndex2;
    }

}
