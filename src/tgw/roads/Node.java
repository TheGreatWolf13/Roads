package tgw.roads;

public class Node {

    private static long ID_MAKER;
    private final long id;
    private double x;
    private double y;

    private Node(long id) {
        this.id = id;
    }

    public Node(double x, double y) {
        this(++ID_MAKER);
        this.x = x;
        this.y = y;
    }
}
