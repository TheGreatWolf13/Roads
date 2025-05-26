package tgw.roads.util;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.*;

@Documented
@Nonnull
@TypeQualifierDefault(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PACKAGE)
public @interface FieldsAreNotNullByDefault {
}
