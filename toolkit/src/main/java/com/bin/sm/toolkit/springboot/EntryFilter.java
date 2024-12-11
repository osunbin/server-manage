//package com.bin.sm.toolkit.springboot;
//
//import jakarta.servlet.AsyncContext;
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ReadListener;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletInputStream;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * TODO  感觉 /path  使用线程池
// */
//public class EntryFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//
//        long startTime = TimeRunnable.getStartTimeFromThreadLocal();
//
//        long start = System.currentTimeMillis();
//        if (startTime > 0) {
//           long waitTime = start - startTime;
//           // 等待时间
//            // 方法执行时间
//            // 等待+执行
//        }
//        // 成功  异常  超时  抛弃
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//
//
//        AsyncContext asyncContext = request.getAsyncContext();
//
//        ServletInputStream inputStream = request.getInputStream();
//        inputStream.setReadListener(new ReadListener() {
//            @Override
//            public void onDataAvailable() throws IOException {
//
//            }
//
//            @Override
//            public void onAllDataRead() throws IOException {
//                try {
//                    filterChain.doFilter(servletRequest, servletResponse);
//                } catch (ServletException e) {
//                    throw new RuntimeException(e);
//                } finally {
//                    long endTime = System.currentTimeMillis();
//                    asyncContext.complete();
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                asyncContext.complete();
//            }
//        });
//
//        // 异步 TODO
//        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
//        httpServletResponse.addHeader("cpuUsage","0.2");
//    }
//}
