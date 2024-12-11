package com.bin.sm.logging;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(LogRecords.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LogRecord {

    /**
     * @return 方法执行成功后的日志模版
     */
    String success();

    /**
     * @return 方法执行失败后的日志模版
     */
    String fail() default "";

    /**
     * @return 操作人
     */
    String operator() default "";

    /**
     * @return 操作日志的类型，比如：订单类型、商品类型
     */
    String type();

    /**
     * @return 日志的子类型，比如订单的C端日志，和订单的B端日志，type都是订单类型，但是子类型不一样
     */
    String subType() default "";

    /**
     * @return 日志绑定的业务标识
     */
    String bizNo();

    /**
     * @return 日志的额外信息
     */
    String extra() default "";

    /**
     *  日志内容
     */
    String msg() default "";

    /**
     *  日志标签
     * @return
     */
    String tag() default "";
    /**
     * @return 是否记录日志
     */
    String condition() default "";

    /**
     * 记录成功日志的条件
     *
     * @return 表示成功的表达式，默认为空，代表不抛异常为成功
     */
    String successCondition() default "";

}
