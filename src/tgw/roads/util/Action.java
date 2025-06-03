package tgw.roads.util;

import org.intellij.lang.annotations.MagicConstant;
import org.lwjgl.glfw.GLFW;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.SOURCE)
@MagicConstant(intValues = {
        GLFW.GLFW_RELEASE,
        GLFW.GLFW_PRESS,
        GLFW.GLFW_REPEAT
})
public @interface Action {
}
