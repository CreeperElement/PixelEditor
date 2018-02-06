package fenske;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Transformation {
    public Color transform(int x, int y, Color color);
}
