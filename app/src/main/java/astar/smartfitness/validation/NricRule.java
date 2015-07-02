package astar.smartfitness.validation;

import com.mobsandgeeks.saripaar.AnnotationRule;

import java.lang.annotation.Annotation;

import astar.smartfitness.Utils;

public class NricRule extends AnnotationRule<Nric, String> {
    /**
     * Constructor. It is mandatory that all subclasses MUST have a constructor with the same
     * signature.
     *
     * @param nric The rule {@link Annotation} instance to which
     *             this rule is paired.
     */
    protected NricRule(Nric nric) {
        super(nric);
    }

    @Override
    public boolean isValid(String s) {
        return Utils.isValidNric(s);
    }
}
