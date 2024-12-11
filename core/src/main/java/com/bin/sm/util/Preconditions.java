package com.bin.sm.util;

import com.google.common.base.Strings;

import javax.annotation.CheckForNull;

public class Preconditions {

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, @CheckForNull Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, char p1) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, int p1) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, long p1) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, char p1, char p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, char p1, int p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, char p1, long p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, char p1, @CheckForNull Object p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, int p1, char p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, int p1, int p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, int p1, long p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, int p1, @CheckForNull Object p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, long p1, char p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, long p1, int p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, long p1, long p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, long p1, @CheckForNull Object p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, char p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, int p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, long p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2, @CheckForNull Object p3) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2, p3}));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2, @CheckForNull Object p3, @CheckForNull Object p4) {
        if (!expression) {
            throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[]{p1, p2, p3, p4}));
        }
    }

    public static void checkTrue(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static <T> T checkNotNull(T argument) {
        if (argument == null) {
            throw new NullPointerException();
        }
        return argument;
    }


    public static <T> T checkNotNull(T argument, String errorMessage) {
        if (argument == null) {
            throw new NullPointerException(errorMessage);
        }
        return argument;
    }

    public static int checkPositive(String paramName, int value) {
        if (value <= 0) {
            throw new IllegalArgumentException(paramName + " is " + value + " but must be > 0");
        }
        return value;
    }

}
