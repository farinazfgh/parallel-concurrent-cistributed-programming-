//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.rice.pcdp.config;

public enum SystemProperty {
    numWorkers("pcdp.numWorkers", "int", "Number of worker threads to create", SystemProperty.Constants.AVAILABLE_PROCESSORS_STR),
    showWarning("pcdp.showWarning", "bool", "Show warning/debug messages", "false"),
    showRuntimeStats("pcdp.showRuntimeStats", "bool", "Show executor service stats", "false");

    private final String propertyKey;
    private final String propertyType;
    private final String propertyDescription;
    private final String defaultValue;

    public static void setSystemProperty(SystemProperty hjSystemProperty, Object value) {
        System.setProperty(hjSystemProperty.propertyKey(), String.valueOf(value));
    }

    private SystemProperty(String pPropertyKey, String pPropertyType, String pPropertyDescription, String pDefaultValue) {
        this.propertyKey = pPropertyKey;
        this.propertyType = pPropertyType;
        this.propertyDescription = pPropertyDescription;
        this.defaultValue = pDefaultValue;
    }

    private String propertyKey() {
        return this.propertyKey;
    }

    public String getPropertyValue() {
        String systemProperty = System.getProperty(this.propertyKey);
        return systemProperty != null && !systemProperty.trim().isEmpty() ? systemProperty : this.defaultValue;
    }

    public void set(Object value) {
        this.setProperty(value);
    }

    public void setProperty(Object value) {
        setSystemProperty(this, value);
    }

    public String toString() {
        String configuredValue = System.getProperty(this.propertyKey);
        String displayValue;
        if (configuredValue != null) {
            displayValue = configuredValue;
        } else {
            displayValue = "";
        }

        return String.format("%20s : type=%-6s, default=%-6s, current=%-6s, description=%s", this.propertyKey, this.propertyType, this.defaultValue, displayValue, this.propertyDescription);
    }

    private static class Constants {
        private static int availableProcessors = Runtime.getRuntime().availableProcessors();
        private static final String AVAILABLE_PROCESSORS_STR;
        private static final String MAX_THREADS_DEFAULT;

        private Constants() {
        }

        static {
            AVAILABLE_PROCESSORS_STR = Integer.toString(availableProcessors);
            MAX_THREADS_DEFAULT = Integer.toString(Math.min(128, 10 * availableProcessors));
        }
    }
}
