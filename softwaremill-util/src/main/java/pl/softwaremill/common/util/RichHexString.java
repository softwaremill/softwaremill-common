package pl.softwaremill.common.util;

/**
 * Wrapps a string that represents byte array in hex codes
 * <p/>
 * User: szimano
 */
public class RichHexString {

    private final String wrapped;

    /**
     * Builds new wrapper with given string
     *
     * @param wrapped Hex representation
     */
    public RichHexString(String wrapped) {
        this.wrapped = wrapped;

        // check that the string contains only allowed values

        if (wrapped != null && wrapped.length() > 0 && !wrapped.toUpperCase().matches(hexStringPattern)) {
            throw new IllegalArgumentException("Hex representation string '" + wrapped +
                    "' contains illegal characters. Allowed ones are 0-9 and a-f (A-F).");
        }
    }

    /**
     * Build hex representation from given array
     *
     * @param array Array of bytes
     */
    public RichHexString(byte[] array) {
        this.wrapped = hexStringFromBytes(array);
    }

    public String unwrap() {
        return wrapped;
    }

    public byte[] getBytes() {
        if (wrapped == null || wrapped.length() == 0) {
            return new byte[]{};
        }
        
        return hexStringToByteArray(wrapped);
    }

    // conversion methods
    
    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static final String hexStringPattern;

    static {
        // build a pattern from allowed chars
        StringBuffer sb = new StringBuffer("(");

        for (char hexChar : hexChars) {
            sb.append(hexChar).append("|");
        }

        // delete last one
        sb.deleteCharAt(sb.length() - 1);

        sb.append(")+");

        hexStringPattern = sb.toString();

        System.out.println(hexStringPattern);
    }

    // See: http://www.osix.net/modules/article/?id=42

    private String hexStringFromBytes(byte[] b) {
        StringBuffer hex = new StringBuffer("");

        int msb;
        int lsb;
        int i;

        // MSB maps to idx 0
        for (i = 0; i < b.length; i++) {
            msb = ((int) b[i] & 0x000000FF) / 16;
            lsb = ((int) b[i] & 0x000000FF) % 16;
            hex.append(hexChars[msb]).append(hexChars[lsb]);
        }

        return hex.toString();
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
