package astar.smartfitness.validation;

import com.mobsandgeeks.saripaar.annotation.ValidateUsing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ValidateUsing(NricRule.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Nric {
    public int messageResId() default -1;                     // Mandatory attribute
    public String message() default "Invalid NRIC";   // Mandatory attribute
    public int sequence() default -1;                     // Mandatory attribute

}