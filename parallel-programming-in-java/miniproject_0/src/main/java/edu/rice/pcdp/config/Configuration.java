//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.rice.pcdp.config;

import java.io.InputStream;
import java.util.Properties;

public final class Configuration {
    public static boolean showWarning;
    public static boolean SHOW_RUNTIME_STATS;
    public static String BUILD_INFO;

    private Configuration() {
        throw new IllegalStateException("Emptyton, no instance creation expected!");
    }

    private static void initializeFlags() {
        showWarning = readBooleanProperty(SystemProperty.showWarning);
        if (showWarning) {
            printConfiguredOptions();
        }

        SHOW_RUNTIME_STATS = readBooleanProperty(SystemProperty.showRuntimeStats);

        String buildInfo;
        try {
            Properties buildProperties = new Properties();
            InputStream buildPropsStream = Configuration.class.getResourceAsStream("/build.properties");
            buildProperties.load(buildPropsStream);
            buildInfo = buildProperties.getProperty("version") + ' ' + buildProperties.getProperty("buildTimestamp");
        } catch (Exception var3) {
            buildInfo = "";
        }

        BUILD_INFO = buildInfo;
    }

    public static boolean readBooleanProperty(SystemProperty systemProperty) {
        Configuration.Lambda<String, Boolean> converter = new Configuration.Lambda<String, Boolean>() {
            public Boolean apply(String s) {
                return Boolean.parseBoolean(s);
            }
        };
        return (Boolean)extractProperty(systemProperty, converter);
    }

    public static void printConfiguredOptions() {
        System.err.println("Interpreter flags: ");
        SystemProperty[] var0 = SystemProperty.values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            SystemProperty systemProperty = var0[var2];
            System.err.println(" " + systemProperty);
        }

    }

    public static int readIntProperty(SystemProperty systemProperty) {
        Configuration.Lambda<String, Integer> converter = new Configuration.Lambda<String, Integer>() {
            public Integer apply(String s) {
                return Integer.parseInt(s);
            }
        };
        return (Integer)extractProperty(systemProperty, converter);
    }

    private static <T> T extractProperty(SystemProperty propertyName, Configuration.Lambda<String, T> converter) {
        try {
            String valueStr = propertyName.getPropertyValue();
            return converter.apply(valueStr);
        } catch (Exception var3) {
            throw new IllegalStateException("Error while converting property: " + propertyName);
        }
    }

    public static String readStringProperty(SystemProperty systemProperty) {
        Configuration.Lambda<String, String> converter = new Configuration.Lambda<String, String>() {
            public String apply(String s) {
                return s;
            }
        };
        return (String)extractProperty(systemProperty, converter);
    }

    static {
        initializeFlags();
    }

    private interface Lambda<P, R> {
        R apply(P var1);
    }
}
