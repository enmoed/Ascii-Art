package image;

import java.awt.*;

/**
 * Decorator class to retrieve sub-images of original image
 */
class SubImageDecorator implements Image{
    private final Image im; // image to be decorated
    private final int size; // size of sub-images

    /**
     * Constructor for decorated Image
     * @param im image to be decorated
     * @param size int size of sub-images. Must be a power of 2 and no greater than the minimum between the
     *             original image height or width.
     */
    SubImageDecorator(Image im, int size){
        this.im = im;
        this.size = size;
    }

    /**
     * Retrieves the top left pixel of sub-image in coordinates representing sub-images
     * @param x the x'th sub-image on x-axis
     * @param y the y'th sub-image on y-axis
     * @return Color the top left pixel of sub-image in coordinates representing sub-images
     */
    @Override
    public Color getPixel(int x, int y) {
        return im.getPixel(x * size, y * size);
    }

    /**
     * @return int number of possible sub-images in a row
     */
    @Override
    public int getWidth() {
        return im.getWidth()/size;
    }

    /**
     * @return int number of possible sub-images in a column
     */
    @Override
    public int getHeight() {
        return im.getHeight()/size;
    }

    /**
     * Creates an Image from a section of another Image according to size.
     * @param a top left x-axis coordinate of image
     * @param b top left y-axis coordinate of image
     * @return a shallow copy Image of a subsection of the original image
     */
    public Image getImage(int a, int b){
        return new Image() {
            /**
             * Retrieves the pixel from the original image
             * @param x int x-axis coordinate of sub-image
             * @param y int y-axis coordinate of sub-image
             * @return pixel of referred coordinate from initial image
             */
            @Override
            public Color getPixel(int x, int y) {
                return im.getPixel(a * size + x, b * size + y);
            }

            /**
             * @return int width of sub-image
             */
            @Override
            public int getWidth() {
                return size;
            }

            /**
             * @return int height of sub-image
             */
            @Override
            public int getHeight() {
                return size;
            }
        };
    }
}
