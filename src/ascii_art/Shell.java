package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Class for building ascii art
 */
public class Shell {
    private static final int MIN_PIXELS_PER_CHAR = 2; // minimum pixels in a char
    private static final int INITIAL_CHARS_IN_ROW = 64; // initial chars in a row
    public static final String EXIT = "exit";
    public static final String CHARS = "chars";
    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    public static final String RES = "res";
    public static final String UP = "up";
    public static final String DOWN = "down";
    public static final String ALL = "all";
    public static final String SPACE = "space";
    public static final String CONSOLE = "console";
    public static final String RENDER = "render";
    private static final String OUTPUT_FILENAME = "out.html";
    private static final String FONT_NAME = "Courier New";
    public static final String INCORRECT_COMMAND = "Did not execute due to incorrect command";
    public static final String EXCEEDING_BOUNDARIES = "Did not change due to exceeding boundaries";
    public static final String INCORRECT_FORMAT = "Did not %s due to incorrect format%n";
    public static final String UPDATED_WIDTH = "Width set to %d%n";
    private final Image image; // image to be made into ascii art
    private final Set<Character> charSet = new HashSet<>(); // set of chars to use in ascii art
    private final BrightnessImgCharMatcher imageCharMatcher; // matches between image sections and chars
    private AsciiOutput output; // output for the ascii art
    private int charsInRow; // number of chars in a row

    /**
     * Constructor for creating the ascii art
     * @param image Image to be turned into ascii art
     */
    public Shell(Image image){
        this.image = image;
        getCharsInRow(INITIAL_CHARS_IN_ROW);
        this.imageCharMatcher = new BrightnessImgCharMatcher(image, FONT_NAME);
        this.output = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
        // initialize charSet to include numbers 0-9
        for (char i=48; i<58; i++){
            this.charSet.add(i);
        }
    }

    /**
     * Runs the shell
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(">>>");
            String initialScan = scanner.nextLine();
            // if no entry we repeat
            if (initialScan.length() == 0) {
                continue;
            }
            // if entry ends in ' ' it is incorrect
            if (initialScan.charAt(initialScan.length()-1) == ' '){
                incorrectCommand();
                continue;
            }
            // split entry into an array by spaces
            String [] scan = initialScan.split(" ");
            // switch based on first argument given
            switch (scan[0]) {
                case EXIT:
                    // make sure no extra commands were given
                    if(scan.length == 1){
                        return;
                    }
                    incorrectCommand();
                    break;
                case CHARS:
                    // make sure no extra commands were given
                    if(scan.length == 1){
                        getCharSet();
                        break;
                    }
                    incorrectCommand();
                    break;
                case ADD:
                    addChars(scan);
                    break;
                case REMOVE:
                    removeChars(scan);
                    break;
                case RES:
                    changeResolution(scan);
                    break;
                case CONSOLE:
                    // make sure no extra commands were given
                    if(scan.length == 1){
                        output = new ConsoleAsciiOutput();
                        break;
                    }
                    incorrectCommand();
                    break;
                case RENDER:
                    // make sure no extra commands were given
                    if(scan.length == 1){
                        renderImage();
                        break;
                    }
                    incorrectCommand();
                    break;
                default:
                    incorrectCommand();
            }
        }
    }

    /**
     * Sets charsInRow according to algorithm to make sure we are within boundaries
     * @param newCharsInRow int new charsInRow requested
     */
    private void getCharsInRow(int newCharsInRow) {
        int minCharsInRow = Math.max(1, image.getWidth() / image.getHeight());
        int maxCharsInRow = image.getWidth() / MIN_PIXELS_PER_CHAR;
        this.charsInRow = Math.max(Math.min(newCharsInRow, maxCharsInRow), minCharsInRow);
    }

    /**
     * Renders the image according to the output
     */
    private void renderImage() {
        char [][] charImage = imageCharMatcher.chooseChars(charsInRow, charSet.toArray(new Character[0]));
        if(charImage != null){
            output.output(charImage);
        }
    }

    /**
     * Prints incorrect command statement
     */
    private static void incorrectCommand() {
        System.out.println(INCORRECT_COMMAND);
    }

    /**
     * Changes resolution of image
     * @param scan String array containing the resolution to change to
     */
    private void changeResolution(String [] scan) {
        // checks that there are no extra commands in the array
        if(scan.length != 2) {
            incorrectCommand();
            return;
        }
        int oldCharsInRow = charsInRow;
        switch (scan[1]){
            case UP:
                getCharsInRow(charsInRow*2); // increases resolution
                break;
            case DOWN:
                getCharsInRow(charsInRow/2); // decreases resolution
                break;
            default:
                incorrectCommand(); // invalid argument
                return;
        }
        // compares between original resolution and new resolution
        if (oldCharsInRow != charsInRow){
            System.out.printf(UPDATED_WIDTH, charsInRow);
            return;
        }
        // could not update resolution due to it exceeding legal boundaries
        System.out.println(EXCEEDING_BOUNDARIES);
    }

    /**
     * Removes given chars from charSet
     * @param scan array including chars to be removed
     */
    private void removeChars(String [] scan) {
        editCharSet(scan, charSet::remove, REMOVE);
    }

    /**
     * Adds given chars from charSet
     * @param scan array including chars to be added
     */
    private void addChars(String [] scan) {
        editCharSet(scan, charSet::add, ADD);
    }

    /**
     * Edits the char set according to the consumer function
     * @param scan array of the chars to apply function to
     * @param func Consumer function for Characters
     * @param op String name of operation of consumer function
     */
    private static void editCharSet(String [] scan, Consumer<Character> func, String op) {
        // check that number of arguments in string array is valid
        if(scan.length != 2) {
            System.out.printf(INCORRECT_FORMAT, op);
            return;
        }
        // applies function if the argument is one char
        if (scan[1].length() == 1){
            func.accept(scan[1].charAt(0));
            return;
        }
        // applies function if argument is a range
        if (scan[1].length() == 3 && scan[1].charAt(1) == '-'){
            char max = (char) Math.max(scan[1].charAt(0), scan[1].charAt(2)); //maximum ascii range values
            char min = (char) Math.min(scan[1].charAt(0), scan[1].charAt(2)); //minimum ascii range values
            for (char i = min; i <= max; i++){
                func.accept(i);
            }
            return;
        }
        // applies function if argument is a special case
        switch(scan[1]) {
            case ALL:
                for (char i = 32; i < 127; i++) {
                    func.accept(i);
                }
                return;
            case SPACE:
                func.accept(' ');
                return;
        }
        System.out.printf(INCORRECT_FORMAT, op);

    }

    /**
     * Prints out all chars in the charSet
     */
    private void getCharSet() {
        for (Character character: charSet) {
            System.out.print(character + " ");
        }
        System.out.println();
    }
}
