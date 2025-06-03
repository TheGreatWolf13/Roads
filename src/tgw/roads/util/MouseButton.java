package tgw.roads.util;

import org.intellij.lang.annotations.MagicConstant;
import org.lwjgl.glfw.GLFW;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.SOURCE)
@MagicConstant(intValues = {
        GLFW.GLFW_MOUSE_BUTTON_1,
        GLFW.GLFW_MOUSE_BUTTON_2,
        GLFW.GLFW_MOUSE_BUTTON_3,
        GLFW.GLFW_MOUSE_BUTTON_4,
        GLFW.GLFW_MOUSE_BUTTON_5,
        GLFW.GLFW_MOUSE_BUTTON_6,
        GLFW.GLFW_MOUSE_BUTTON_7,
        GLFW.GLFW_MOUSE_BUTTON_8,
        GLFW.GLFW_MOUSE_BUTTON_LEFT,
        GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
        GLFW.GLFW_MOUSE_BUTTON_RIGHT,
        GLFW.GLFW_MOUSE_BUTTON_LAST
})
public @interface MouseButton {
}
