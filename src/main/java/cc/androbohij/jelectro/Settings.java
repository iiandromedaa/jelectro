package cc.androbohij.jelectro;

public abstract class Settings {
    
    /**
     * <p>coulombs constant, or, since its k, maybe koulombs konstant</p>
     */
    public static final double K = 8.987e9;

    /**
     * <p>how far out in meters to consider vectors to display</p>
     * <p>bounds covers a cube centered on the origin spanning 
     * -vectorBounds to vectorBounds on all sides</p>
     * <p><b>recommended: 5</b></p>
     */
    public static double vectorBounds = 2;

    /**
     * <p>how many field vectors should we display in 1 cubic meter</p>
     * <p><b>recommended: 23/sqrt(vectorBounds^3)</p><p>
     * (i just did a regression from some values i liked to get this)</b></p>
     */
    public static double vectorDensity = Math.round((23/Math.pow(vectorBounds, 1.5)));

    public static boolean showGrid = true;

    public static boolean showField = false;

    public static boolean showStreamlines = true;

    public static boolean field2DOnly = true;

    public static double cullFieldVecs = 0.05;

    public static double streamlineStepSize = 0.1;

    public static int streamlineMaxSteps = 100;

    /**
     * <p>how many streamlines to trace from each object</p>
     * <p><b>recommended: 100/number of objects</b></p>
     * <p><b>but, if you have fields with smaller features 
     * like octopoles, turn this up more</b></p>
     */
    public static int streamlinesPerObject = 20;

    public static boolean drawArrowHeads = false;

    /**
     * <p>at what distance from objects to begin walking streamlines forward and backwards</p>
     * <p><b>recommended: 0.25 for point charges and dipoles,</b></p>
     * <p><b>[0.5, 1] to see the lobes of multipoles beyond 2</b></p>
     */
    public static double streamlineStartDistance = 0.33;

    public static int streamlinePasses = 1;

    public static boolean showVelocities = true;
    
    public static boolean drawB = false;

    public static boolean drawE = true;

}
