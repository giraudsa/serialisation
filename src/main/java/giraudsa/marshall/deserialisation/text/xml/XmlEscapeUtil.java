/*
 * =============================================================================
 * 
 *   Copyright (c) 2014, The UNBESCAPE team (http://www.unbescape.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package giraudsa.marshall.deserialisation.text.xml;

/**
 * <p>
 *   Internal class in charge of performing the real escape/unescape operations.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0.0
 *
 */
public final class XmlEscapeUtil {
	private static final XmlEscapeSymbols SYMBOLS = new XmlEscapeSymbols();
    private static final char REFERENCE_PREFIX = '&';
    private static final char REFERENCE_NUMERIC_PREFIX2 = '#';
    private static final char REFERENCE_HEXA_PREFIX3 = 'x';
    private static final char REFERENCE_SUFFIX = ';';
    private static final char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static final char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();




    private XmlEscapeUtil() {
        super();
    }

    private static int parseIntFromReference(final String text, final int start, final int end, final int radix) {
        int result = 0;
        for (int i = start; i < end; i++) {
            final char c = text.charAt(i);
            int n = -1;
            for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
                if (c == HEXA_CHARS_UPPER[j] || c == HEXA_CHARS_LOWER[j]) {
                    n = j;
                    break;
                }
            }
            result = (radix * result) + n;
        }
        return result;
    }

    /*
     * Perform an unescape operation based on String.
     */
    public static String unescape(final String text) {

        if (text == null) {
            return null;
        }

        StringBuilder strBuilder = null;

        final int offset = 0;
        final int max = text.length();

        int readOffset = offset;
        int referenceOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text.charAt(i);

            /*
             * Check the need for an unescape operation at this point
             */

            if (c != REFERENCE_PREFIX || (i + 1) >= max) {
                continue;
            }

            int codepoint = 0;

            if (c == REFERENCE_PREFIX) {

                final char c1 = text.charAt(i + 1);

                if (c1 == '\u0020' || // SPACE
                        c1 == '\n' ||     // LF
                        c1 == '\u0009' || // TAB
                        c1 == '\u000C' || // FF
                        c1 == '\u003C' || // LES-THAN SIGN
                        c1 == '\u0026') { // AMPERSAND
                    // Not a character references. No characters are consumed, and nothing is returned.
                    continue;

                } else if (c1 == REFERENCE_NUMERIC_PREFIX2) {

                    if (i + 2 >= max) {
                        // No reference possible
                        continue;
                    }

                    final char c2 = text.charAt(i + 2);

                    if (c2 == REFERENCE_HEXA_PREFIX3 && (i + 3) < max) {
                        // This is a hexadecimal reference

                        int f = i + 3;
                        while (f < max) {
                            final char cf = text.charAt(f);
                            if (!((cf >= '0' && cf <= '9') || (cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f'))) {
                                break;
                            }
                            f++;
                        }

                        if ((f - (i + 3)) <= 0) {
                            // We weren't able to consume any hexa chars
                            continue;
                        }

                        if ((f >= max) || text.charAt(f) != REFERENCE_SUFFIX) {
                            continue;
                        }

                        f++; // Count the REFERENCE_SUFFIX (semi-colon)

                        codepoint = parseIntFromReference(text, i + 3, f - 1, 16);
                        referenceOffset = f - 1;

                        // Don't continue here, just let the unescape code below do its job

                    } else if (c2 >= '0' && c2 <= '9') {
                        // This is a decimal reference

                        int f = i + 2;
                        while (f < max) {
                            final char cf = text.charAt(f);
                            if (!(cf >= '0' && cf <= '9')) {
                                break;
                            }
                            f++;
                        }

                        if ((f - (i + 2)) <= 0) {
                            // We weren't able to consume any decimal chars
                            continue;
                        }

                        if ((f >= max) || text.charAt(f) != REFERENCE_SUFFIX) {
                            continue;
                        }

                        f++; // Count the REFERENCE_SUFFIX (semi-colon)

                        codepoint = parseIntFromReference(text, i + 2, f - 1, 10);
                        referenceOffset = f - 1;

                        // Don't continue here, just let the unescape code below do its job

                    } else {
                        // This is not a valid reference, just discard
                        continue;
                    }


                } else {

                    // This is a named reference, must be comprised only of ALPHABETIC chars

                    int f = i + 1;
                    while (f < max) {
                        final char cf = text.charAt(f);
                        if (!((cf >= 'a' && cf <= 'z') || (cf >= 'A' && cf <= 'Z') || (cf >= '0' && cf <= '9'))) {
                            break;
                        }
                        f++;
                    }

                    if ((f - (i + 1)) <= 0) {
                        // We weren't able to consume any alphanumeric
                        continue;
                    }

                    if ((f < max) && text.charAt(f) == REFERENCE_SUFFIX) {
                        f++;
                    }

                    final int ncrPosition = XmlEscapeSymbols.binarySearch(SYMBOLS.SORTED_CERS, text, i, f);
                    if (ncrPosition >= 0) {
                        codepoint = SYMBOLS.SORTED_CODEPOINTS_BY_CER[ncrPosition];
                    } else {
                        // Not found! Just ignore our efforts to find a match.
                        continue;
                    }

                    referenceOffset = f - 1;

                }

            }


            /*
             * At this point we know for sure we will need some kind of unescape, so we
             * can increase the offset and initialize the string builder if needed, along with
             * copying to it all the contents pending up to this point.
             */

            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 5);
            }

            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }

            i = referenceOffset;
            readOffset = i + 1;

            /*
             * --------------------------
             *
             * Perform the real unescape
             *
             * --------------------------
             */

            if (codepoint > '\uFFFF') {
                strBuilder.append(Character.toChars(codepoint));
            } else {
                strBuilder.append((char)codepoint);
            }

        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: return the original String object if no unescape was actually needed. Otherwise
         *                 append the remaining escaped text to the string builder and return.
         * -----------------------------------------------------------------------------------------------
         */

        if (strBuilder == null) {
            return text;
        }

        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }

        return strBuilder.toString();

    }
}

