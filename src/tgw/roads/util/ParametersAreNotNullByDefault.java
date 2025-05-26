package tgw.roads.util;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.*;

@Documented
@Nonnull
@TypeQualifierDefault(ElementType.PARAMETER)
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface ParametersAreNotNullByDefault {
}