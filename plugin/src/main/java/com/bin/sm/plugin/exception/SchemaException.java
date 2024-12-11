package com.bin.sm.plugin.exception;

import java.util.Locale;

public class SchemaException extends RuntimeException {
    /**
     * Version not found
     */
    public static final MsgParser MISSING_VERSION = new MsgParser() {
        @Override
        public String parse(Object... args) {
            return String.format(Locale.ROOT, "Unable to find version of %s. ", args);
        }
    };

    /**
     * The name found does not match what was expected
     */
    public static final MsgParser UNEXPECTED_NAME = new MsgParser() {
        @Override
        public String parse(Object... args) {
            return String.format(Locale.ROOT, "Name of plugin is wrong, giving %s but expecting %s. ", args);
        }
    };

    /**
     * The version found does not match what was expected
     */
    public static final MsgParser UNEXPECTED_VERSION = new MsgParser() {
        @Override
        public String parse(Object... args) {
            return String.format(Locale.ROOT, "Version of %s is wrong, giving %s but expecting %s. ", args);
        }
    };

    /**
     * External dependency packages are not accepted
     */
    public static final MsgParser UNEXPECTED_EXT_JAR = new MsgParser() {
        @Override
        public String parse(Object... args) {
            return String.format(Locale.ROOT, "External jar %s is not allowed. ", args);
        }
    };

    private static final long serialVersionUID = 3875379572570581867L;

    /**
     * constructor
     *
     * @param parser parser
     * @param args args
     */
    public SchemaException(MsgParser parser, Object... args) {
        super(parser.parse(args));
    }

    /**
     * message parser
     *
     * @since 2021-11-04
     */
    public interface MsgParser {
        /**
         * parse
         *
         * @param args args
         * @return string
         */
        String parse(Object... args);
    }
}
