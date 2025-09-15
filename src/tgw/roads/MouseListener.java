package tgw.roads;

import org.lwjgl.glfw.GLFW;
import tgw.roads.util.Action;
import tgw.roads.util.Mod;
import tgw.roads.util.MouseButton;

public final class MouseListener {

    private static short clickCount0;
    private static short clickCount1;
    private static short clickCount2;
    private static short clickCount3;
    private static short clickCount4;
    private static short clickCount5;
    private static short clickCount6;
    private static short clickCount7;
    private static byte isDown;
    private static boolean isDragging;
    private static double lastX;
    private static double lastY;
    private static double scrollX;
    private static double scrollY;
    private static double x;
    private static double y;

    private MouseListener() {
    }

    public static boolean consumeClick(@MouseButton int button) {
        return switch (button) {
            case GLFW.GLFW_MOUSE_BUTTON_1 -> {
                if (clickCount0 > 0) {
                    --clickCount0;
                    yield true;
                }
                yield false;
            }
            case GLFW.GLFW_MOUSE_BUTTON_2 -> {
                if (clickCount1 > 0) {
                    --clickCount1;
                    yield true;
                }
                yield false;
            }
            case GLFW.GLFW_MOUSE_BUTTON_3 -> {
                if (clickCount2 > 0) {
                    --clickCount2;
                    yield true;
                }
                yield false;
            }
            case GLFW.GLFW_MOUSE_BUTTON_4 -> {
                if (clickCount3 > 0) {
                    --clickCount3;
                    yield true;
                }
                yield false;
            }
            case GLFW.GLFW_MOUSE_BUTTON_5 -> {
                if (clickCount4 > 0) {
                    --clickCount4;
                    yield true;
                }
                yield false;
            }
            case GLFW.GLFW_MOUSE_BUTTON_6 -> {
                if (clickCount5 > 0) {
                    --clickCount5;
                    yield true;
                }
                yield false;
            }
            case GLFW.GLFW_MOUSE_BUTTON_7 -> {
                if (clickCount6 > 0) {
                    --clickCount6;
                    yield true;
                }
                yield false;
            }
            case GLFW.GLFW_MOUSE_BUTTON_8 -> {
                if (clickCount7 > 0) {
                    --clickCount7;
                    yield true;
                }
                yield false;
            }
            default -> throw new IllegalStateException("Unknown Mouse Button: " + button);
        };
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
        return (isDown & 1 << button) != 0;
    }

    public static boolean isDragging() {
        return isDragging;
    }

    public static void mouseButtonCallback(long ignoredWindowPointer, @MouseButton int button, @Action int action, @Mod int ignoredMods) {
        if (action == GLFW.GLFW_PRESS) {
            isDown |= (byte) (1 << button);
            switch (button) {
                case GLFW.GLFW_MOUSE_BUTTON_1 -> {
                    ++clickCount0;
                }
                case GLFW.GLFW_MOUSE_BUTTON_2 -> {
                    ++clickCount1;
                }
                case GLFW.GLFW_MOUSE_BUTTON_3 -> {
                    ++clickCount2;
                }
                case GLFW.GLFW_MOUSE_BUTTON_4 -> {
                    ++clickCount3;
                }
                case GLFW.GLFW_MOUSE_BUTTON_5 -> {
                    ++clickCount4;
                }
                case GLFW.GLFW_MOUSE_BUTTON_6 -> {
                    ++clickCount5;
                }
                case GLFW.GLFW_MOUSE_BUTTON_7 -> {
                    ++clickCount6;
                }
                case GLFW.GLFW_MOUSE_BUTTON_8 -> {
                    ++clickCount7;
                }
            }
        }
        else if (action == GLFW.GLFW_RELEASE) {
            isDown &= (byte) ~(1 << button);
            isDragging = false;
        }
    }

    public static void mousePosCallback(long ignoredWindowPointer, double x, double y) {
        lastX = MouseListener.x;
        lastY = MouseListener.y;
        MouseListener.x = x;
        MouseListener.y = y;
        isDragging = isDown != 0;
    }

    public static void mouseScrollCallback(long ignoredWindow, double dx, double dy) {
        scrollX = dx;
        scrollY = dy;
    }
}
