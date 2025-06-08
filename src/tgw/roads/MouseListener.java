package tgw.roads;

import org.lwjgl.glfw.GLFW;
import tgw.roads.util.Action;
import tgw.roads.util.Mod;
import tgw.roads.util.MouseButton;

public final class MouseListener {

    private static boolean isDragging;
    private static double lastX;
    private static double lastY;
    private static byte mouseButtonPressed;
    private static double scrollX;
    private static double scrollY;
    private static double x;
    private static double y;

    private MouseListener() {
    }

    public static void endFrame() {
        scrollX = 0;
        scrollY = 0;
        lastX = x;
        lastY = y;
    }

    public static float getDx() {
        return (float) (x - lastX);
    }

    public static float getDy() {
        return (float) (y - lastY);
    }

    public static float getScrollX() {
        return (float) scrollX;
    }

    public static float getScrollY() {
        return (float) scrollY;
    }

    public static float getX() {
        return (float) x;
    }

    public static float getY() {
        return (float) y;
    }

    public static boolean isButtonDown(@MouseButton int button) {
        return (mouseButtonPressed & 1 << button) != 0;
    }

    public static boolean isDragging() {
        return isDragging;
    }

    public static void mouseButtonCallback(long ignoredWindowPointer, @MouseButton int button, @Action int action, @Mod int ignoredMods) {
        if (action == GLFW.GLFW_PRESS) {
            mouseButtonPressed |= (byte) (1 << button);
        }
        else if (action == GLFW.GLFW_RELEASE) {
            mouseButtonPressed &= (byte) ~(1 << button);
            isDragging = false;
        }
    }

    public static void mousePosCallback(long ignoredWindowPointer, double x, double y) {
        lastX = MouseListener.x;
        lastY = MouseListener.y;
        MouseListener.x = x;
        MouseListener.y = y;
        isDragging = mouseButtonPressed != 0;
    }

    public static void mouseScrollCallback(long ignoredWindow, double dx, double dy) {
        scrollX = dx;
        scrollY = dy;
    }
}
