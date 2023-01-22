package ascii_art.img_to_char;

import image.Image;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Class to match image sections with chars
 */
public class BrightnessImgCharMatcher {
    private final Image img; // image to match with chars
    private final String font; // font of chars
    // map of chars to their brightness level
    private static final Map<Character, Double> charBrightnessMap = new HashMap<Character, Double>();
    // map of image resolution to a map of sub-image index to brightness
    private static final Map<Integer, Map<Integer, Double>> resolutionImagesMap = new HashMap<Integer, Map<Integer, Double>>();

    /**
     * Constructor to match image sections with chars according to brightness
     * @param img Image to turn into chars
     * @param font String representing font of chars
     */
    public BrightnessImgCharMatcher(Image img, String font){
        this.img = img;
        this.font = font;
    }

    /**
     * Selects chars to be matched with sections of image
     * @param numCharsInRow int number of chars in row
     * @param charSet Set of chars to choose from
     * @return Array of arrays of chars representing the image
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet){
        List<Double> charBrightness = getCharBrightness(charSet);
        int charSize = img.getWidth()/numCharsInRow;
        int numCharsInCol = img.getHeight()/charSize;
        char[][] ascii = new char[numCharsInCol][numCharsInRow];
        // checks if we already of map between sub-images and their brightness
        Map<Integer, Double> subImageBrightnessMap = resolutionImagesMap.containsKey(numCharsInCol) ?
                resolutionImagesMap.get(numCharsInCol) :
                new HashMap<>();
        Iterator<Image> it = img.subImages(charSize).iterator();
        for (int i = 0; i < numCharsInCol; i++){
            for (int j = 0; j < numCharsInRow; j++){
                Image image = it.next();
                double imageBrightness = getImageBrightness(image, i*numCharsInRow + j,
                        subImageBrightnessMap);
                // adds most similar character to ascii image
                ascii[i][j] = getCharByBrightness(charSet, charBrightness, imageBrightness);
                }
            }
        // if new sub-map then we add it to the main map
        if (!resolutionImagesMap.containsKey(numCharsInRow)){
            resolutionImagesMap.put(numCharsInRow, subImageBrightnessMap);
        }
        return ascii;
    }

    /**
     * Selects the chars according to the brightness of the image
     * @param charSet Array of Characters to choose from
     * @param charBrightness List of the brightness of the chars
     * @param imageBrightness double of the image brightness
     * @return char most similar in brightness to the brightness given
     */
    private static char getCharByBrightness(Character[] charSet,
                                            List<Double> charBrightness,
                                            double imageBrightness) {
        List<Double> temp = new ArrayList<>(charBrightness);
        // changes char brightness to absolute value of difference between image and char brightness
        temp.replaceAll(x-> Math.abs(x-imageBrightness));
        return charSet[temp.indexOf(Collections.min(temp))];
    }

    /**
     * Calculates the brightness of the image given
     * @param image Image to calculate on
     * @param index int representing index of image
     * @param subImageBrightnessMap map between image index and brightness
     * @return double brightness of image
     */
    private static double getImageBrightness(Image image,
                                             int index,
                                             Map<Integer, Double> subImageBrightnessMap) {
        // return brightness if available in map
        if (subImageBrightnessMap.containsKey(index)){
            return subImageBrightnessMap.get(index);
        }
        double imageBrightness = 0;
        // calculate brightness
        for (Color pixel: image.pixels()) {
            imageBrightness += getGreyPixel(pixel);
        }
        imageBrightness /= (255*image.getWidth()*image.getHeight());
        subImageBrightnessMap.put(index, imageBrightness);
        return imageBrightness;
    }

    /**
     * Calculates Color to grey pixel brightness
     * @param pixel Color to calculate
     * @return grey pixel brightness
     */
    private static double getGreyPixel(Color pixel) {
        return pixel.getRed() * 0.2126 + pixel.getGreen() * 0.7152 +
                pixel.getBlue() * 0.0722;
    }

    /**
     * Fills list with char brightness according to index of char in charSet
     * @param charSet Array of Characters to calculate brightness for
     * @return List of Character brightness
     */
    private List<Double> getCharBrightness(Character[] charSet) {
        List<Double> charValue = new ArrayList<>(charSet.length);
        for (Character character : charSet) {
            // if character brightness is saved in static map
            if (charBrightnessMap.containsKey(character)){
                charValue.add(charBrightnessMap.get(character));
            }
            // else calculate char brightness
            else {
                Double charBrightness = Arrays.stream(CharRenderer.getImg(character, 16, font)).
                        mapToDouble(x ->
                        {
                            double sum = 0;
                            for (boolean bool : x) {
                                sum = bool ? sum + 1 : sum;
                            }
                            return sum;
                        }).sum();
                charValue.add(charBrightness);
                charBrightnessMap.put(character, charBrightness);
            }
        }
        Double min = Collections.min(charValue);
        Double max = Collections.max(charValue);
        charValue.replaceAll(x -> (x - min) / (max - min)); // normalize char brightnesses
        return charValue;
    }
}
