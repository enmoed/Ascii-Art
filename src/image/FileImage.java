package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class FileImage implements Image {
    private static final Color DEFAULT_COLOR = Color.WHITE; // default border color of image
    private final Color[][] pixelArray; // array of arrays of colors
    private final int pixelArrayWidth; // width of pixel matrix
    private final int pixelArrayHeight; // height of pixel matrix

    /**
     * Constructor for interpreting image from file source
     * @param filename pathname of image file
     * @throws IOException
     */
    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();
        //im.getRGB(x, y)); getter for access to a specific RGB rates

        // 2^ceil(log2(orig))
        pixelArrayWidth = (int) Math.pow(2, Math.ceil(Math.log(origWidth)/Math.log(2)));
        pixelArrayHeight = (int) Math.pow(2, Math.ceil(Math.log(origHeight)/Math.log(2)));
        // matrix initialization
        pixelArray = new Color[pixelArrayHeight][pixelArrayWidth];
        initPixelArray(im, origWidth, origHeight);
    }

    /**
     * Initialize array of arrays of pixels
     * @param im image to transform into color matrix
     * @param origWidth original width of image
     * @param origHeight original height of image
     */
    private void initPixelArray(BufferedImage im, int origWidth, int origHeight) {
        // white border length
        int borderWidth = (pixelArrayWidth - origWidth) / 2;
        int borderHeight = (pixelArrayHeight - origHeight) / 2;

        // iterate over the height of the board
        for (int i = 0; i < pixelArrayHeight; i++){
            // initialize row
            pixelArray[i] = new Color[pixelArrayWidth];

            // if we are within the height border then create a white row and continue
            if (i < borderHeight || i >= borderHeight + origHeight) {
                for (int j = 0; j < pixelArrayWidth; j++) {
                    pixelArray[i][j] = new Color(DEFAULT_COLOR.getRGB());
                }
                continue;
            }

            // iterate over the colored rows of the board
            for (int j = 0; j < pixelArrayWidth; j++){
                // if we are within the width border then create a white pixel and continue
                if (j < borderWidth || j >= borderWidth + origWidth) {
                    pixelArray[i][j] = new Color(DEFAULT_COLOR.getRGB());
                    continue;
                }
                // otherwise color the pixel
                pixelArray[i][j] = new Color(im.getRGB(j-borderWidth, i-borderHeight));
            }
        }
    }

    /**
     * @return width of pixel matrix
     */
    @Override
    public int getWidth() {
        return pixelArrayWidth;
    }

    /**
     * @return height of pixel matrix
     */
    @Override
    public int getHeight() {
        return pixelArrayHeight;
    }

    /**
     * @param x column coordinate of pixel
     * @param y row coordinate of pixel
     * @return Color of pixel
     */
    @Override
    public Color getPixel(int x, int y) {
        if (x >= pixelArrayWidth || y >= pixelArrayHeight){
            return null;
        }
        return new Color(pixelArray[y][x].getRGB());
    }
}
