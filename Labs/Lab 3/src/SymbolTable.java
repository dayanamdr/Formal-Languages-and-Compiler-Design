public class SymbolTable {
    private final Integer size;
    private final HashTable identifierHashTable;
    private final HashTable intConstHashTable;
    private final HashTable stringConstHashTable;

    /**
     * Constructor used to instantiate a SymbolTable.
     * @param size represents the size of the SymbolTable.
     */
    public SymbolTable(Integer size) {
        this.identifierHashTable = new HashTable(size);
        this.intConstHashTable = new HashTable(size);
        this.stringConstHashTable = new HashTable(size);
        this.size = this.identifierHashTable.getSize();
    }

    public HashTable getIdentifierHashTable() { return this.identifierHashTable; }
    public HashTable getIntConstHashTable() { return this.intConstHashTable; }
    public HashTable getStringConstHashTable() { return this.stringConstHashTable; }

    /**
     * Adds an identifier into the SymbolTable.
     * @param elem is the identifier that has to be added.
     * @return the position where the element was added into the identifierHashTable.
     * @throws Exception if the SymbolTable already contains the elem.
     */
    public Pair<Integer, Integer> addIdentifier(String elem) throws Exception {
        return this.identifierHashTable.addElem(elem);
    }

    /**
     * Adds a constant integer into the SymbolTable.
     * @param elem is the constant integer that has to be added.
     * @return the position where the element was added into the intConstHashTable.
     * @throws Exception if the SymbolTable already contains the elem.
     */
    public Pair<Integer, Integer> addIntConst(String elem) throws Exception {
        return this.intConstHashTable.addElem(elem);
    }

    /**
     * Adds a constant string into the SymbolTable.
     * @param elem is the constant string that has to be added.
     * @return the position where the element was added into the stringConstHashTable.
     * @throws Exception if the SymbolTable already contains the elem.
     */
    public Pair<Integer, Integer> addStringConst(String elem) throws Exception {
        return this.stringConstHashTable.addElem(elem);
    }

    /**
     * Checks whether an identifier is in the SymbolTable.
     * @param elem is the identifier that is searched.
     * @return true, if the identifier is found. Otherwise, false.
     */
    public boolean containsIdentifier(String elem) {
        return this.identifierHashTable.contains(elem);
    }

    /**
     * Checks whether a constant integer is in the SymbolTable.
     * @param elem is the constant integer that is searched.
     * @return true, if the constant integer is found. Otherwise, false.
     */
    public boolean containsIntConst(String elem) { return this.intConstHashTable.contains(elem); }

    /**
     * Checks whether a constant string is in the SymbolTable.
     * @param elem is the constant string that is searched.
     * @return true, if the constant string is found. Otherwise, false.
     */
    public boolean containsStringConst(String elem) {
        return this.stringConstHashTable.contains(elem);
    }

    /**
     * Finds the position of an identifier into the SymbolTable.
     * @param elem is the identifier for which the position is searched.
     * @return the position of the identifier from the identifierHashTable.
     */
    public Pair<Integer, Integer> getIdentifierPosition(String elem) {
        return this.identifierHashTable.findElemPosition(elem);
    }

    /**
     * Finds the position of a constant integer into the SymbolTable.
     * @param elem is the constant integer for which the position is searched.
     * @return the position of the constant integer from the intConstHashTable.
     */
    public Pair<Integer, Integer> getIntConstPosition(String elem) {
        return this.intConstHashTable.findElemPosition(elem);
    }

    /**
     * Finds the position of a constant string into the SymbolTable.
     * @param elem is the constant string for which the position is searched.
     * @return the position of the constant string from the stringConstHashTable.
     */
    public Pair<Integer, Integer> getStringConstPosition(String elem) {
        return this.stringConstHashTable.findElemPosition(elem);
    }

    @Override
    public String toString() {
        return "SymbolTable { " +
                "identifierTable: " + identifierHashTable +
                "\nintConstTable: " + intConstHashTable +
                "\nstringConstTable: " + stringConstHashTable + "}\n";
    }
}
