package tgw.roads;

import org.lwjgl.glfw.GLFW;
import tgw.roads.util.Action;
import tgw.roads.util.Mod;
import tgw.roads.util.MouseButton;

public final class MouseListener {

    private static final MouseListener INSTANCE = new MouseListener();
    private boolean isDragging;
    private double lastX;
    private double lastY;
    private byte mouseButtonPressed;
    private double scrollX;
    private double scrollY;
    private double x;
    private double y;

    private MouseListener() {
    }

    public static void endFrame() {
        MouseListener listener = get();
        listener.scrollX = 0;
        listener.scrollY = 0;
        listener.lastX = listener.x;
        listener.lastY = listener.y;
    }

    public static MouseListener get() {
        return INSTANCE;
    }

    public static float getDx() {
        MouseListener listener = get();
        return (float) (listener.x - listener.lastX);
    }

    public static float getDy() {
        MouseListener listener = get();
        return (float) (listener.y - listener.lastY);
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static float getX() {
        return (float) get().x;
    }

    public static float getY() {
        return (float) get().y;
    }

    public static boolean isButtonDown(@MouseButton int button) {
        return (get().mouseButtonPressed & 1 << button) != 0;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static void mouseButtonCallback(long ignoredWindowPointer, @MouseButton int button, @Action int action, @Mod int ignoredMods) {
        MouseListener listener = get();
        if (action == GLFW.GLFW_PRESS) {
            listener.mouseButtonPressed |= (byte) (1 << button);
        }
        else if (action == GLFW.GLFW_RELEASE) {
            listener.mouseButtonPressed &= (byte) ~(1 << button);
            listener.isDragging = false;
        }
    }

    public static void mousePosCallback(long ignoredWindowPointer, double x, double y) {
        MouseListener listener = get();
        listener.lastX = listener.x;
        listener.lastY = listener.y;
        listener.x = x;
        listener.y = y;
        listener.isDragging = listener.mouseButtonPressed != 0;
    }

    public static void mouseScrollCallback(long ignoredWindow, double dx, double dy) {
        MouseListener listener = get();
        listener.scrollX = dx;
        listener.scrollY = dy;
    }
}
