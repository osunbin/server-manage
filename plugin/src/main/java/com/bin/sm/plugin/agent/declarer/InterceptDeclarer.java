package com.bin.sm.plugin.agent.declarer;


import com.bin.sm.plugin.agent.interceptor.Interceptor;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class InterceptDeclarer {

    /**
     * constructor
     *
     * @param methodMatcher method matcher
     * @param interceptors interceptors
     * @return InterceptDeclarer
     * @throws IllegalArgumentException IllegalArgumentException
     */
    public static InterceptDeclarer build(MethodMatcher methodMatcher, Interceptor... interceptors) {
        if (methodMatcher == null || interceptors == null || interceptors.length == 0) {
            throw new IllegalArgumentException("Matcher cannot be null and interceptor array cannot be empty. ");
        }
        return new InterceptDeclarer() {
            @Override
            public MethodMatcher getMethodMatcher() {
                return methodMatcher;
            }

            @Override
            public Interceptor[] getInterceptors(ClassLoader classLoader) {
                return interceptors;
            }
        };
    }

    public static InterceptDeclarer build(MethodMatcher methodMatcher, String... interceptors) {
        if (methodMatcher == null || interceptors == null || interceptors.length == 0) {
            throw new IllegalArgumentException("Matcher cannot be null and interceptor array cannot be empty. ");
        }
        return new InterceptDeclarer() {
            @Override
            public MethodMatcher getMethodMatcher() {
                return methodMatcher;
            }

            @Override
            public Interceptor[] getInterceptors(ClassLoader classLoader) {
                try {
                    return createInterceptors(interceptors);
                } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
//                    LOGGER.log(Level.SEVERE,
//                            "Unable to create instance of interceptors: " + Arrays.toString(interceptors), e);

                }
                return new Interceptor[0];
            }
        };
    }

    private static Interceptor[] createInterceptors(String[] interceptors)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final ArrayList<Interceptor> interceptorList = new ArrayList<>();
        for (String interceptor : interceptors) {
//            final Object instance = ClassLoaderManager.getPluginClassFinder().loadSermantClass(interceptor)
//                    .newInstance();
            final Object instance = null;
            if (instance instanceof Interceptor) {
                interceptorList.add((Interceptor) instance);
            }
        }
        return interceptorList.toArray(new Interceptor[0]);
    }

    /**
     * Get method matcher
     *
     * @return method matcher
     */
    public abstract MethodMatcher getMethodMatcher();

    /**
     * Gets the interceptor set
     *
     * @param classLoader The classLoader of the enhanced class
     * @return Interceptor set
     */
    public abstract Interceptor[] getInterceptors(ClassLoader classLoader);
}
