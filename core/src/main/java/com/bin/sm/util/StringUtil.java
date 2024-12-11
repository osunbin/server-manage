package com.bin.sm.util;

import java.lang.reflect.Array;

public class StringUtil {
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isAnyEmpty(CharSequence... css) {
        if (isEmpty(css)) {
            return true;
        } else {
            CharSequence[] var1 = css;
            int var2 = css.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                CharSequence cs = var1[var3];
                if (isEmpty(cs)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isNoneEmpty(CharSequence... css) {
        return !isAnyEmpty(css);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isAnyBlank(CharSequence... css) {
        if (isEmpty(css)) {
            return true;
        } else {
            CharSequence[] var1 = css;
            int var2 = css.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                CharSequence cs = var1[var3];
                if (isBlank(cs)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isEmpty(Object[] array) {
        return getLength(array) == 0;
    }

    public static int getLength(Object array) {
        return array == null ? 0 : Array.getLength(array);
    }

    public static boolean isNoneBlank(CharSequence... css) {
        return !isAnyBlank(css);
    }

    public static boolean isNumeric(String str, boolean allowDot) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        boolean hasDot = false;
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (str.charAt(i) == '.') {
                if (hasDot || !allowDot) {
                    return false;
                }
                hasDot = true;
                continue;
            }
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
