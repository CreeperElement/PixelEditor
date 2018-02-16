/*
 * SE 1021
 * Winter 2018
 * Lab Eight Image Viewer
 * Seth Fenske
 * Created 2/7/18
 */
package fenske;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Function used to mutate colors of images
 */
public interface Transformation {
    /**
     * Function, use to mutate colors for different coloring gradients
     * @param x X-position of coordinate
     * @param y Y-position of coordinate
     * @param color Color to be mutated
     * @return Mutated Color
     */
    public Color transform(int x, int y, Color color);
}
