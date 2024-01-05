import java.util.ArrayList;

public class HashTable {
    private final ArrayList<ArrayList<String>> hashTable;
    private final Integer size;

    public HashTable(Integer size) {
        this.hashTable = new ArrayList<>();
        this.size = size;

        // initialize each element of the hashTable with a new ArrayList
        for (int i = 0; i < this.size; i++) {
            this.hashTable.add(new ArrayList<>());
        }
    }

    /**
     * Gets the size of the hash table.
     * @return an integer representing the size of the hash table.
     */
    public Integer getSize() {
        return this.size;
    }

    /**
     * Computes the hash value of an element as the sum of its chars % the size of the hash table.
     * @param elem represents the element for which the hash value has to be computed.
     * @return an integer which is the computed hash value.
     */
    private Integer hash(String elem) {
        int charsSum = 0;
        for (int i = 0; i < elem.length(); i++) {
            charsSum += elem.charAt(i);
        }
        return charsSum % this.size;
    }

    /**
     * Checks whether the elements is in the hash table.
     * @param elem represents the element that is searched.
     * @return true, if the elem is found. Otherwise, false.
     */
    public boolean contains(String elem) {
        int hashValue = hash(elem);
        return this.hashTable.get(hashValue).contains(elem);
    }

    /**
     * Finds the position of the given element as a param.
     * @param elem represents the element that has to be searched.
     * @return if elem is found, returns its position as a pair(listIndex, elemIndexInList).
     * Otherwise, returns pair(-1,-1).
     */
    public Pair<Integer, Integer> findElemPosition(String elem) {
        if (this.contains(elem)) {
            int hashValue = hash(elem);
            return new Pair<>(hashValue, hashTable.get(hashValue).indexOf(elem));
        }
        return new Pair<>(-1, -1);
    }

    /**
     * Finds the element from the position passed as param.
     * @param position represents the position where the element has to be searched.
     * @return the element found on the given position.
     * @throws IndexOutOfBoundsException if the position is not valid.
     */
    public String findByPosition(Pair<Integer, Integer> position) throws IndexOutOfBoundsException{
        int listPosition = position.getFirstItem();
        int elemPosition = position.getSecondItem();

        if (this.hashTable.size() <= listPosition || this.hashTable.get(listPosition).size() <= elemPosition) {
            throw new IndexOutOfBoundsException("Invalid position!");
        }
        return this.hashTable.get(listPosition).get(elemPosition);
    }

    /**
     * Adds a new elem in the hash table.
     * @param elem the element that needs to be added.
     * @return the position of the element that was added in the hash table.
     * @throws Exception if the hash table already contains the element that wants to be added.
     */
    public Pair<Integer, Integer> addElem(String elem) throws Exception {
        if (this.contains(elem)) {
            throw new Exception("Elem " + elem + " is already in the hash table!");
        }

        Integer position = hash(elem);
        ArrayList<String> elems = this.hashTable.get(position);
        elems.add(elem);
        return new Pair<>(position, this.hashTable.get(position).indexOf(elem));
    }

    @Override
    public String toString() {
        return "SymbolTable {size: " + this.size + ", elements: " + this.hashTable + "}";
    }
}
