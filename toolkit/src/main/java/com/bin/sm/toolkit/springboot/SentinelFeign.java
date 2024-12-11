//package com.bin.sm.springboot;
//
//import feign.Contract;
//import feign.Feign;
//import feign.InvocationHandlerFactory;
//import feign.Target;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.util.Map;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.cloud.openfeign.FallbackFactory;
//import org.springframework.cloud.openfeign.FeignClientFactory;
//import org.springframework.cloud.openfeign.FeignClientFactoryBean;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.support.GenericApplicationContext;
//import org.springframework.util.ReflectionUtils;
//import org.springframework.util.StringUtils;
//
//public final class SentinelFeign {
//    private static final String FEIGN_LAZY_ATTR_RESOLUTION = "spring.cloud.openfeign.lazy-attributes-resolution";
//
//    private SentinelFeign() {
//    }
//
//    public static Builder builder() {
//        return new Builder();
//    }
//
//    public static final class Builder extends Feign.Builder implements ApplicationContextAware {
//        private Contract contract = new Contract.Default();
//        private ApplicationContext applicationContext;
//        private FeignClientFactory feignClientFactory;
//
//        public Builder() {
//        }
//
//        public Feign.Builder invocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
//            throw new UnsupportedOperationException();
//        }
//
//        public Builder contract(Contract contract) {
//            this.contract = contract;
//            return this;
//        }
//
//        public Feign internalBuild() {
//
//            super.invocationHandlerFactory(new InvocationHandlerFactory() {
//                public InvocationHandler create(Target target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
//                    GenericApplicationContext gctx = (GenericApplicationContext)Builder.this.applicationContext;
//                    BeanDefinition def = gctx.getBeanDefinition(target.type().getName());
//                    Boolean isLazyInit = (Boolean)Builder.this.applicationContext.getEnvironment().getProperty("spring.cloud.openfeign.lazy-attributes-resolution", Boolean.class, false);
//                    FeignClientFactoryBean feignClientFactoryBean;
//                    if (isLazyInit) {
//                        feignClientFactoryBean = (FeignClientFactoryBean)def.getAttribute("feignClientsRegistrarFactoryBean");
//                    } else {
//                        feignClientFactoryBean = (FeignClientFactoryBean)Builder.this.applicationContext.getBean("&" + target.type().getName());
//                    }
//
//                    Class fallback = feignClientFactoryBean.getFallback();
//                    Class fallbackFactory = feignClientFactoryBean.getFallbackFactory();
//                    String beanName = feignClientFactoryBean.getContextId();
//                    if (!StringUtils.hasText(beanName)) {
//                        beanName = (String)Builder.this.getFieldValue(feignClientFactoryBean, "name");
//                    }
//
//                    if (Void.TYPE != fallback) {
//                        Object fallbackInstance = this.getFromContext(beanName, "fallback", fallback, target.type());
//                        return new SentinelInvocationHandler(target, dispatch, new FallbackFactory.Default(fallbackInstance));
//                    } else if (Void.TYPE != fallbackFactory) {
//                        FallbackFactory fallbackFactoryInstance = (FallbackFactory)this.getFromContext(beanName, "fallbackFactory", fallbackFactory, FallbackFactory.class);
//                        return new SentinelInvocationHandler(target, dispatch, fallbackFactoryInstance);
//                    } else {
//                        return new SentinelInvocationHandler(target, dispatch);
//                    }
//                }
//
//                private Object getFromContext(String name, String type, Class fallbackType, Class targetType) {
//                    Object fallbackInstance = Builder.this.feignClientFactory.getInstance(name, fallbackType);
//                    if (fallbackInstance == null) {
//                        throw new IllegalStateException(String.format("No %s instance of type %s found for feign client %s", type, fallbackType, name));
//                    } else {
//                        if (fallbackInstance instanceof FactoryBean) {
//                            FactoryBean<?> factoryBean = (FactoryBean)fallbackInstance;
//
//                            try {
//                                fallbackInstance = factoryBean.getObject();
//                            } catch (Exception var8) {
//                                Exception e = var8;
//                                throw new IllegalStateException(type + " create fail", e);
//                            }
//
//                            fallbackType = fallbackInstance.getClass();
//                        }
//
//                        if (!targetType.isAssignableFrom(fallbackType)) {
//                            throw new IllegalStateException(String.format("Incompatible %s instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s", type, fallbackType, targetType, name));
//                        } else {
//                            return fallbackInstance;
//                        }
//                    }
//                }
//            });// end
//
//
//            super.contract(new SentinelContractHolder(this.contract));
//            return super.internalBuild();
//
//        }
//
//        private Object getFieldValue(Object instance, String fieldName) {
//            Field field = ReflectionUtils.findField(instance.getClass(), fieldName);
//            field.setAccessible(true);
//
//            try {
//                return field.get(instance);
//            } catch (IllegalAccessException var5) {
//                return null;
//            }
//        }
//
//        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//            this.applicationContext = applicationContext;
//            this.feignClientFactory = (FeignClientFactory)this.applicationContext.getBean(FeignClientFactory.class);
//        }
//    }
//}
