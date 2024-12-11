package com.bin.sm.plugin.agent.declarer;


import com.bin.sm.plugin.agent.template.ImplDelegator;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public abstract class SuperTypeDeclarer {

    /**
     * Do not use constructors and external custom implementations
     */
    private SuperTypeDeclarer() {
    }

    /**
     * Gets the superclass type and determines whether to process enhancements
     *
     * @return superclass type
     */
    public abstract Class<?> getSuperType();

    /**
     * process enhancement
     *
     * @param superType superclass type
     * @param builder builder
     * @return builder
     */
    public abstract DynamicType.Builder<?> resolve(Class<?> superType, DynamicType.Builder<?> builder);

    /**
     * The {@link SuperTypeDeclarer} implementation uses the interface implementation to implement the superclass
     * interface
     *
     * @param <T> superclass type
     * @since 2022-01-24
     */
    public abstract static class ForImplInstance<T> extends SuperTypeDeclarer {
        /**
         * Build a {@link SuperTypeDeclarer} implementation that uses the interface implementation to implement the
         * superclass interface
         *
         * @param superType superclass type
         * @param implInstance implement instance
         * @param <T> superclass generic type
         * @return SuperTypeDeclarer
         * @throws IllegalArgumentException IllegalArgumentException
         */
        public static <T> SuperTypeDeclarer build(Class<T> superType, T implInstance) {
            if (superType == null || !superType.isInterface() || implInstance == null) {
                throw new IllegalArgumentException(
                        "Super type has to be an interface and its implement instance should not be null. ");
            }
            return new ForImplInstance<T>() {
                @Override
                public Class<T> getSuperType() {
                    return superType;
                }

                @Override
                public T getImplInstance() {
                    return implInstance;
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public abstract Class<T> getSuperType();

        @Override
        public DynamicType.Builder<?> resolve(Class<?> superType, DynamicType.Builder<?> builder) {
            return builder.implement(superType).method(ElementMatchers.isDeclaredBy(superType))
                    .intercept(MethodDelegation.withDefaultConfiguration().to(new ImplDelegator(getImplInstance())));
        }

        /**
         * Gets an instance of the superclass
         *
         * @return instance
         */
        protected abstract T getImplInstance();
    }

    /**
     * {@link SuperTypeDeclarer} implementation that generates fields and get and set methods based on
     *
     * @since 2022-01-24
     */
    public abstract static class ForBeanProperty extends SuperTypeDeclarer {
        /**
         * build {@link SuperTypeDeclarer} implementation
         *
         * @param superType superclass type
         * @return SuperTypeDeclarer
         * @throws IllegalArgumentException IllegalArgumentException
         */
        public static SuperTypeDeclarer build(Class<?> superType) {
//            if (superType == null || !superType.isInterface()
//                    || superType.getAnnotationsByType(BeanPropertyFlag.class).length <= 0) {
//                throw new IllegalArgumentException(String.format(
//                        "Super type has to be an interface annotated by [%s]. ", BeanPropertyFlag.class.getName()
//                ));
//            }
            return new ForBeanProperty() {
                @Override
                public Class<?> getSuperType() {
                    return superType;
                }
            };
        }

        @Override
        public DynamicType.Builder<?> resolve(Class<?> superType, DynamicType.Builder<?> builder) {
//            final BeanPropertyFlag[] flags = superType.getAnnotationsByType(BeanPropertyFlag.class);
//            if (flags.length <= 0) {
//                return builder;
//            }
//            DynamicType.Builder<?> newBuilder = builder;
//            for (BeanPropertyFlag beanPropertyFlag : flags) {
//                newBuilder = newBuilder.defineField(beanPropertyFlag.value(), beanPropertyFlag.type(),
//                        Visibility.PRIVATE);
//            }
//            return newBuilder.implement(superType).intercept(FieldAccessor.ofBeanProperty());
            return builder;
        }
    }
}
