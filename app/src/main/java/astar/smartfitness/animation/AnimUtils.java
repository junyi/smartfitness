package astar.smartfitness.animation;

import android.view.animation.Interpolator;

public class AnimUtils {
    public static Interpolator getFastInSlowOutInterpolator() {
        //Ease In Out Interpolator - For elements that change position while staying in the screen
        return new CubicBezierInterpolator(0.4, 0, 0.2, 1);
    }

    public static Interpolator getLinearOutSlowInInterpolator() {
        //Decelerate Interpolator - For elements that enter the screen
        return new CubicBezierInterpolator(0, 0, 0.2, 1);
    }

    public static Interpolator getFastOutLinearInInterpolator() {
        //Accelerate Interpolator - For elements that exit the screen
        return new CubicBezierInterpolator(0.4, 0, 1, 1);
    }

    public static Interpolator getEaseOutExpoInterpolator() {
        return new CubicBezierInterpolator(0.19, 1, 0.22, 1);
    }
}
