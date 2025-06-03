package tgw.roads;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.lwjgl.glfw.GLFW;
import tgw.roads.util.Action;
import tgw.roads.util.Key;
import tgw.roads.util.Mod;
import tgw.roads.util.Nullable;

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

    public static void keyCallback(long ignoredWindowPointer, @Key int key, int ignoredScancode, @Action int action, @Mod int ignoredMods) {
        KeyListener listener = get();
        ObjectList<KeyBinding> keyBindings = listener.keyBinds.get(key);
        if (keyBindings != null && !keyBindings.isEmpty()) {
            switch (action) {
                case GLFW.GLFW_PRESS -> {
                    for (int i = 0, len = keyBindings.size(); i < len; i++) {
                        KeyBinding binding = keyBindings.get(i);
                        binding.isDown = true;
                        ++binding.clickCount;
                    }
                }
                case GLFW.GLFW_RELEASE -> {
                    for (int i = 0, len = keyBindings.size(); i < len; i++) {
                        keyBindings.get(i).isDown = false;
                    }
                }
                case GLFW.GLFW_REPEAT -> {
                    for (int i = 0, len = keyBindings.size(); i < len; i++) {
                        ++keyBindings.get(i).clickCount;
                    }
                }
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
    }
}
