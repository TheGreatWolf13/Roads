package tgw.roads.util;

import org.intellij.lang.annotations.MagicConstant;
import org.lwjgl.glfw.GLFW;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.SOURCE)
@MagicConstant(flags = {
        GLFW.GLFW_MOD_SHIFT,
        GLFW.GLFW_MOD_CONTROL,
        GLFW.GLFW_MOD_ALT,
        GLFW.GLFW_MOD_SUPER,
        GLFW.GLFW_MOD_CAPS_LOCK,
        GLFW.GLFW_MOD_NUM_LOCK
})
public @interface Mod {
}
