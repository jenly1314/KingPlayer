package com.king.player.app.util;

public class HexUtils {

    /**
     * Table for HEX to DEC byte translation.
     */
    private static final int[] DEC = {
            00, 01, 02, 03, 04, 05, 06, 07, 8, 9, -1, -1, -1, -1, -1, -1,
            -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, 10, 11, 12, 13, 14, 15,
    };


    /**
     * Table for DEC to HEX byte translation.
     */
    private static final byte[] HEX =
            {(byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
                    (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
                    (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F'};


    /**
     * Table for byte to hex string translation.
     */
    private static final char[] hex = "0123456789ABCDEF".toCharArray();


    // --------------------------------------------------------- Static Methods

    public static int getDec(int index) {
        // Fast for correct values, slower for incorrect ones
        try {
            return DEC[index - '0'];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return -1;
        }
    }


    public static byte getHex(int index) {
        return HEX[index];
    }

    public static String toHexString(byte b) {
        return toHexString(new byte[]{b});
    }
    public static String toHexString(byte[] bytes) {
        return toHexString(null, bytes);
    }

    public static String toHexString(final String spacer, final byte[] bytes) {
        if (null == bytes) {
            return null;
        }

        int spacerLen = spacer == null ? 0 : spacer.length();
        StringBuilder sb = new StringBuilder(bytes.length << 2 + (bytes.length - 1));

        for (int i = 0; i < bytes.length; ++i) {
            sb.append(hex[(bytes[i] & 0xf0) >> 4]).append(hex[(bytes[i] & 0x0f)]);
            if (spacerLen > 0) {
                sb.append(spacer);
            }
        }

        return sb.toString();
    }


    public static byte[] fromHexString(String input) {
        if (input == null) {
            return null;
        }

        if (input.contains(" ")) {
            input = input.replace(" ", "");
        }

        if ((input.length() & 1) == 1) {
            // Odd number of characters
            throw new IllegalArgumentException("hexUtils.fromHex.oddDigits");
        }

        char[] inputChars = input.toCharArray();
        byte[] result = new byte[input.length() >> 1];
        for (int i = 0; i < result.length; i++) {
            int upperNibble = getDec(inputChars[2 * i]);
            int lowerNibble = getDec(inputChars[2 * i + 1]);
            if (upperNibble < 0 || lowerNibble < 0) {
                // Non hex character
                throw new IllegalArgumentException("hexUtils.fromHex.nonHex");
            }
            result[i] = (byte) ((upperNibble << 4) + lowerNibble);
        }
        return result;
    }
}
