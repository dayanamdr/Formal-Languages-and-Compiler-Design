public class Main {
    public static void main(String[] args) {
        MyScanner myScanner = new MyScanner();
        myScanner.start("p1.txt");
        myScanner.start("p1err.txt");
        myScanner.start("p2.txt");
        myScanner.start("p3.txt");
    }
}