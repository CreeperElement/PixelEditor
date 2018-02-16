package fenske;

/**
 * Excpetion thrown to indicate that a file doesn't have properly formatted files
 */
public class CorruptDataException extends Exception {
    /**
     * Used in ImageIO class whenever data requirements aren't met
     * @param message Tell user where error occured
     */
    public CorruptDataException(String message){
        super(message);
    }
}
