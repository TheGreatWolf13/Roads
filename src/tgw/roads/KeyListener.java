package tgw.roads;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.lwjgl.glfw.GLFW;
import tgw.roads.util.Action;
import tgw.roads.util.Key;
import tgw.roads.util.Mod;

import java.util.function.Consumer;

public final class KeyListener {

    private static final ObjectList<KeyBinding>[] KEY_BINDINGS = new ObjectList[GLFW.GLFW_KEY_LAST + 1];
    private static final Int2ObjectMap<ObjectList<KeyBinding>> INVALID_KEY_BINDINGS = new Int2ObjectOpenHashMap<>();
    public static final KeyBinding FORWARD = registerByKey(GLFW.GLFW_KEY_W);
    public static final KeyBinding BACKWARD = registerByKey(GLFW.GLFW_KEY_S);
    public static final KeyBinding LEFT = registerByKey(GLFW.GLFW_KEY_A);
    public static final KeyBinding RIGHT = registerByKey(GLFW.GLFW_KEY_D);
    public static final KeyBinding ROTATE_LEFT = registerByKey(GLFW.GLFW_KEY_Q);
    public static final KeyBinding ROTATE_RIGHT = registerByKey(GLFW.GLFW_KEY_E);

    private KeyListener() {
    }

    private static void handleAction(ObjectList<KeyBinding> list, Consumer<KeyBinding> consumer) {
        for (int i = 0, len = list.size(); i < len; i++) {
            consumer.accept(list.get(i));
        }
    }

    public static void keyCallback(long ignoredWindowPointer, @Key int key, int scancode, @Action int action, @Mod int ignoredMods) {
        ObjectList<KeyBinding> bindings = key == GLFW.GLFW_KEY_UNKNOWN ? INVALID_KEY_BINDINGS.get(scancode) : KEY_BINDINGS[key];
        if (bindings != null && !bindings.isEmpty()) {
            switch (action) {
                case GLFW.GLFW_PRESS -> handleAction(bindings, b -> {
                    b.isDown = true;
                    ++b.clickCount;
                });
                case GLFW.GLFW_RELEASE -> handleAction(bindings, b -> b.isDown = false);
                case GLFW.GLFW_REPEAT -> handleAction(bindings, b -> ++b.clickCount);
            }
        }
    }

    public static KeyBinding registerByKey(@Key int key) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) {
            throw new IllegalStateException("Use the other register method to register the scancode!");
        }
        KeyBinding binding = new KeyBinding();
        ObjectList<KeyBinding> bindings = KEY_BINDINGS[key];
        if (bindings == null) {
            bindings = new ObjectArrayList<>();
            KEY_BINDINGS[key] = bindings;
        }
        bindings.add(binding);
        return binding;
    }

    public static KeyBinding registerByScancode(int scancode) {
        KeyBinding binding = new KeyBinding();
        ObjectList<KeyBinding> bindings = INVALID_KEY_BINDINGS.get(scancode);
        if (bindings == null) {
            bindings = new ObjectArrayList<>();
            INVALID_KEY_BINDINGS.put(scancode, bindings);
        }
        bindings.add(binding);
        return binding;
    }

    public static final class KeyBinding {
        private int clickCount;
        private boolean isDown;

        private KeyBinding() {
        }

        public boolean consumeClick() {
            if (this.clickCount > 0) {
                --this.clickCount;
                return true;
            }
            return false;
        }

        public boolean isDown() {
            return this.isDown;
        }
    }
}
