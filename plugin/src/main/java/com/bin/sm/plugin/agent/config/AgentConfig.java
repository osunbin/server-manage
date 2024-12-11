package com.bin.sm.plugin.agent.config;



import java.util.Collections;
import java.util.Set;

public class AgentConfig {
    public static final AgentConfig INSTANCE = new AgentConfig();
    /**
     * Whether to enable bytecode retransform
     */
    private boolean isReTransformEnable = false;

    /**
     * Enhancement ignored set, a set in which the fully qualified name prefix defined is used to exclude classes that
     * are ignored during enhancement, contains {@code io.sermant} by default, and is not mandatory
     */
    private Set<String> ignoredPrefixes = Collections.singleton("com.bin.sm");

    /**
     * Enhancement ignored interface set, defined in the collection interface is used to exclude enhancement is ignored
     * in the process of class, the default contains {@org.Springframework.Additional.Proxy.Factory}, mandatory,
     * otherwise it will cause spring dynamic proxy conflict, throws java.lang.VerifyError
     */
    private Set<String> ignoredInterfaces = Collections.singleton("org.springframework.cglib.proxy.Factory");

    /**
     * Whether to output a search log during the enhancement process
     */
    private boolean isShowEnhanceLog = false;

    /**
     * Whether to output the bytecode file of the enhanced class
     */
    private boolean isOutputEnhancedClasses = false;

    /**
     * The output path of the enhanced class, if empty, is will use agent/enhancedClasses
     */
    private String enhancedClassesOutputPath;

    /**
     * List of inject plugin services
     */
    private Set<String> serviceInjectList = Collections.emptySet();

   // @ConfigFieldKey("preFilter.enable")
    private boolean preFilterEnable = false;

   // @ConfigFieldKey("preFilter.path")
    private String preFilterPath;

    //@ConfigFieldKey("preFilter.file")
    private String preFilterFile;

    /**
     * Allows classes to be loaded from the thread context, mainly used by the PluginClassLoader to load the classes of
     * the host instance through the thread context, if not allowed can be specified during the interceptor call
     */
    private boolean useContextLoader = false;

    public boolean isReTransformEnable() {
        return isReTransformEnable;
    }

    public void setReTransformEnable(boolean reTransformEnable) {
        isReTransformEnable = reTransformEnable;
    }

    public Set<String> getIgnoredPrefixes() {
        return ignoredPrefixes;
    }

    public Set<String> getIgnoredInterfaces() {
        return ignoredInterfaces;
    }

    public void setIgnoredInterfaces(Set<String> ignoredInterfaces) {
        this.ignoredInterfaces = ignoredInterfaces;
    }

    public void setIgnoredPrefixes(Set<String> ignoredPrefixes) {
        this.ignoredPrefixes = ignoredPrefixes;
    }

    public boolean isShowEnhanceLog() {
        return isShowEnhanceLog;
    }

    public void setShowEnhanceLog(boolean showEnhanceLog) {
        isShowEnhanceLog = showEnhanceLog;
    }

    public boolean isOutputEnhancedClasses() {
        return isOutputEnhancedClasses;
    }

    public void setOutputEnhancedClasses(boolean outputEnhancedClasses) {
        isOutputEnhancedClasses = outputEnhancedClasses;
    }

    public String getEnhancedClassesOutputPath() {
        return enhancedClassesOutputPath;
    }

    public void setEnhancedClassesOutputPath(String enhancedClassesOutputPath) {
        this.enhancedClassesOutputPath = enhancedClassesOutputPath;
    }

    public Set<String> getServiceInjectList() {
        return serviceInjectList;
    }

    public void setServiceInjectList(Set<String> serviceInjectList) {
        this.serviceInjectList = serviceInjectList;
    }

    public boolean isUseContextLoader() {
        return useContextLoader;
    }

    public void setUseContextLoader(boolean useContextLoader) {
        this.useContextLoader = useContextLoader;
    }

    public boolean isPreFilterEnable() {
        return preFilterEnable;
    }

    public void setPreFilterEnable(boolean preFilterEnable) {
        this.preFilterEnable = preFilterEnable;
    }

    public String getPreFilterPath() {
        return preFilterPath;
    }

    public void setPreFilterPath(String preFilterPath) {
        this.preFilterPath = preFilterPath;
    }

    public String getPreFilterFile() {
        return preFilterFile;
    }

    public void setPreFilterFile(String preFilterFile) {
        this.preFilterFile = preFilterFile;
    }
}
