package org.incava.ijdk.lang;

import java.util.*;


/**
 * Extensions to the String class.
 */
public class StringExt
{
    /**
     * Set this to true for debugging output.
     */
    public static boolean DEBUG = false;

    /**
     * Returns an array of strings split at the character delimiter.
     */
    public static String[] split(String str, char delim, int max) {
        return split(str, "" + delim, max);
    }

    /**
     * Returns an array of strings split at the string delimiter.
     */
    public static String[] split(String str, String delim, int max) {
        if (max == 1) {
            return new String[] { str };
        }
        else {
            --max;              // adjust count between 0 and 1

            List<String> splitList = new ArrayList<String>();

            int  nFound = 0;
            int  strlen = str.length();
            int  end = 0;
            int  beg = 0;
            int  delimlen = delim.length();

            for (int idx = 0; idx < strlen; ++idx) {
                if (left(str.substring(idx), delimlen).equals(delim)) {
                    String substr = str.substring(beg, end);
                    splitList.add(substr);
                    beg = end + delimlen;
                    if (max > 0 && ++nFound >= max) {
                        break;
                    }
                }
                ++end;
            }

            if (strlen > beg) {
                String tmp = strlen == beg ? "" : str.substring(beg, strlen);
                splitList.add(tmp);
            }

            if (DEBUG) {
                System.out.println("split(" + str + ", " + delim + ", " + max + "):");
                for (int i = 0; i < splitList.size(); ++i) {
                    System.out.println("    [" + i + "] = '" + splitList.get(i) + "'");
                }
            }
            
            return splitList.toArray(new String[splitList.size()]);
        }
    }

    /**
     * Returns an array of strings split at the character delimiter.
     */
    public static String[] split(String str, char delim) {
        return split(str, String.valueOf(delim), -1);
    }

    /**
     * Returns an array of strings split at the string delimiter.
     */
    public static String[] split(String str, String delim) {
        return split(str, delim, -1);
    }

    /**
     * Converts the (possibly quoted) string into a list, delimited by
     * whitespace and commas..
     */
    public static List<String> listify(String str) {
        // strip leading/trailing single/double quotes
        if (str.charAt(0) == str.charAt(str.length() - 1) &&
            (str.charAt(0) == '"' || str.charAt(0) == '\'')) {
            str = str.substring(1, str.length() - 1);
        }
        
        List<String> list = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(str, " \t\n\r\f,");
        while (st.hasMoreTokens()) {
            String tk = st.nextToken();
            list.add(tk);
        }
        return list;
    }

    /**
     * Returns a string starting with the <code>str</code> parameter, with
     * <code>ch</code>'s following the string to a length of
     * <code>length</code>.
     *
     * Examples:
     *     pad("abcd", '*', 8) -> "abcd****"
     *     pad("abcd", '*', 3) -> "abcd"
     */
    public static String pad(String str, char ch, int length) {
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() < length) {
            buf.append(ch);
        }
        return buf.toString();
    }

    /**
     * Same as the <code>pad</code> method, but applies the padding to the
     * left - hand (leading) side of the string.
     *
     * Examples:
     * <pre>
     *     pad("420", '*', 8) -> "*****420"
     *     pad("1144", '*', 3) -> "1144"
     * </pre>
     */
    public static String padLeft(String str, char ch, int length) {
        return repeat(ch, length - str.length()) + str;
    }

    public static String pad(String str, int length) {
        return pad(str, ' ', length);
    }

    public static String padLeft(String str, int length) {
        return padLeft(str, ' ', length);
    }

    public static String repeat(String str, int length) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            buf.append(str);
        }
        return buf.toString();
    }

    public static String repeat(char ch, int length) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            buf.append(ch);
        }
        return buf.toString();
    }

    /**
     * Returns the leftmost n characters of the string, not exceeding the length
     * of the string. Does not throw the annoying IndexOutOfBoundsException.
     */
    public static String left(String str, int n) {
        int x = Math.min(n, str.length());
        x = Math.max(0, x);     // guard against 0
        return str.substring(0, x);
    }

    /**
     * Returns the rightmost n characters of the string, not exceeding the
     * length of the string. Does not throw the annoying
     * IndexOutOfBoundsException.
     */
    public static String right(String str, int n) {
        int x = Math.min(n, str.length());
        x = Math.max(0, x);     // guard against 0
        int s = str.length() - x;
        return str.substring(s);
    }

    public static void test(String str, char del) {
        System.out.println("-----  test: \"" + str + "\"  -----");
        String[] splits = StringExt.split(str, del);
        System.out.println("#splits: " + splits.length);
        for (int i = 0; i < splits.length; ++i) {
            System.out.println("    # " + i + ": " + splits[i]);
        }
    }

    public static void test(String str, String del) {
        System.out.println("-----  test: \"" + str + "\"  -----");
        String[] splits = StringExt.split(str, del);
        System.out.println("#splits: " + splits.length);
        for (int i = 0; i < splits.length; ++i) {
            System.out.println("    # " + i + ": " + splits[i]);
        }
    }

    public static String join(Collection c, String str) {
        StringBuffer buf = new StringBuffer();
        boolean isFirst = true;
        for (Object obj : c) {
            if (!isFirst) {
                buf.append(str);
            }
            else {
                isFirst = false;
            }
            buf.append(obj.toString());
        }
        return buf.toString();
    }

    public static String join(Object[] ary, String str) {
        return join(Arrays.asList(ary), str);
    }

    public static void main(String[] args) {
        test("this;is;a;test", ';');
        test(";is;a;test", ';');
        test("this;is;a;", ';');
        test("this;;a;test", ';');
        test(";this;;a;test;;", ';');
        test("this is yet another test, it is.", "is");
    }
}


