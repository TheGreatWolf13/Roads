package tgw.roads;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.lwjgl.glfw.GLFW;
import tgw.roads.util.Action;
import tgw.roads.util.Key;
import tgw.roads.util.Mod;
import tgw.roads.util.Nullable;

import java.util.function.Consumer;

public final class KeyListener {

    private static @Nullable KeyListener instance;
    private final Int2ObjectMap<ObjectList<KeyBinding>> keyBinds = new Int2ObjectOpenHashMap<>();

    private KeyListener() {
    }

    public static KeyListener get() {
        if (instance == null) {
            instance = new KeyListener();
        }
        return instance;
    }

    private static void handleAction(ObjectList<KeyBinding> list, Consumer<KeyBinding> consumer) {
        for (int i = 0, len = list.size(); i < len; i++) {
            consumer.accept(list.get(i));
        }
    }

    public static void keyCallback(long ignoredWindowPointer, @Key int key, int ignoredScancode, @Action int action, @Mod int ignoredMods) {
        KeyListener listener = get();
        ObjectList<KeyBinding> keyBindings = listener.keyBinds.get(key);
        if (keyBindings != null && !keyBindings.isEmpty()) {
            switch (action) {
                case GLFW.GLFW_PRESS -> handleAction(keyBindings, b -> {
                    b.isDown = true;
                    ++b.clickCount;
                });
                case GLFW.GLFW_RELEASE -> handleAction(keyBindings, b -> b.isDown = false);
                case GLFW.GLFW_REPEAT -> handleAction(keyBindings, b -> ++b.clickCount);
            }
        }
    }

    public static class KeyBinding {
        private int clickCount;
        private boolean isDown;
        private final int scancode;

        public KeyBinding(int scancode) {
            this.scancode = scancode;
        }

        public boolean isDown() {
            return this.isDown;
        }
    }
}
