package tgw.roads;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class Node {

    public static final Long2ObjectMap<Node> NODES = new Long2ObjectOpenHashMap<>();
    private static long idMaker;
    private final long id;
    private double x;
    private double y;

    private Node(long id) {
        this.id = id;
    }

    public Node(double x, double y) {
        this(++idMaker);
        this.x = x;
        this.y = y;
    }

    public static void createNew(float x, float y) {
        Node node = new Node(x, y);
        NODES.put(node.id, node);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
