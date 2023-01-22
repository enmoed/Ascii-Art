package image;

import java.awt.*;
import java.io.IOException;

/**
 * Facade for the image module and an interface representing an image.
 * @author Dan Nirel
 */
public interface Image {
    /**
     * Gets the pixel at the given coordinates
     * @param x int x-axis pixel
     * @param y int y-axis pixel
     * @return Color of the pixel at the given coordinates
     */
    Color getPixel(int x, int y);

    /**
     * @return int width of Image
     */
    int getWidth();

    /**
     * @return int height of Image
     */
    int getHeight();

    /**
     * Open an image from file. Each dimensions of the returned image is guaranteed
     * to be a power of 2, but the dimensions may be different.
     * @param filename a path to an image file on disk
     * @return an object implementing Image if the operation was successful,
     * null otherwise
     */
    static Image fromFile(String filename) {
        try {
            return new FileImage(filename);
        } catch(IOException ioe) {
            return null;
        }
    }

    /**
     * Allows iterating the pixels' colors by order (first row, second row and so on).
     * @return an Iterable<Color> that can be traversed with a foreach loop
     */
    default Iterable<Color> pixels() {
        return new ImageIterableProperty<>(
                this, this::getPixel);
    }
    /**
     * Allows iterating the pixels' colors by order (first row, second row and so on).
     * @return an Iterable<Color> that can be traversed with a foreach loop
     */
    default Iterable<Image> subImages(int size) {
        SubImageDecorator subImageDecorator = new SubImageDecorator(this, size);
        return new ImageIterableProperty<>(subImageDecorator, subImageDecorator::getImage);
    }


}
