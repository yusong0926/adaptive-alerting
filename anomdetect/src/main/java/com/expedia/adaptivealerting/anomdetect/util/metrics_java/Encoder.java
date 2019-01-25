package com.expedia.adaptivealerting.anomdetect.util.metrics_java;

/**
 * @author shsethi
 */
public class Encoder {
    private static final char[] HEX_DIGITS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    /**
     * Appends hex code of bytes to builder.
     * @param builder
     * @param bytes
     */
    public static void encodeHex(StringBuilder builder, byte[] bytes) {
        for (byte b : bytes) {
            builder.append(HEX_DIGITS[(0xF0 & b) >>> 4])
                    .append(HEX_DIGITS[0x0F & b]);
        }
    }
}