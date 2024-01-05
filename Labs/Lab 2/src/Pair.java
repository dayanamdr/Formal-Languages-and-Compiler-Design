public class Pair<First, Second> {
    private final First firstItem;
    private final Second secondItem;

    public Pair(First firstItem, Second secondItem) {
        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }
    public First getFirstItem() {
        return this.firstItem;
    }

    public Second getSecondItem() {
        return this.secondItem;
    }

    @Override
    public String toString() {
        return "{first: " + this.firstItem + ", second: " + this.secondItem + "}";
    }
}
