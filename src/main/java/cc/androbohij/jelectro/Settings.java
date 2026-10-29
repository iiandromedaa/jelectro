package cc.androbohij.jelectro;

public interface Settings {
    
    /**
     * coulombs constant, or, since its k, how about koulombs konstant
     */
    public static final double K = 8.987e9;

    /**
     * how many field vectors should we display in 1 cubic meter
     */
    public static final double vectorDensity = 4;

    public static final boolean displayGrid = true;

    public static final boolean showField = true;

    public static final boolean Field2DOnly = true;

    public static final double cullFieldVecs = 0.015;

}
