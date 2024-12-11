package com.bin.sm.internal.http;

import com.bin.sm.internal.http.protocol.HttpRequest;
import com.bin.sm.internal.http.protocol.HttpResponse;
import com.bin.sm.internal.http.protocol.SimpleHttpRequest;
import com.bin.sm.internal.http.protocol.SimpleHttpResponse;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleHttpServer {

    private ThreadPoolExecutor httpThreadPool;

    private HttpServer httpServer;

    public void start() throws IOException {
        start(0);


    }
    public void start(int port) throws IOException {
        if (port <= 0) {
            port = 9081;
        }
        if (httpThreadPool == null) {
            httpThreadPool = httpThreadPool(4,4);
        }
        //创建一个HttpServer实例，并绑定到指定的IP地址和端口号
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);


        httpServer.setExecutor(httpThreadPool);

        //创建一个HttpContext，将路径为/myserver请求映射到MyHttpHandler处理器
        httpServer.createContext("/", exchange -> {
            HttpRequest request = new SimpleHttpRequest(exchange);
            HttpResponse response = new SimpleHttpResponse(exchange);
            try {
                Optional<HttpRouteHandler> handlerOptional = HttpRouter.getHandler(request);
                if (handlerOptional.isEmpty()) {
                    throw new HttpServerException(404, "Not Found");
                }
                handlerOptional.get().handle(request, response);
            } catch (HttpServerException e) {
                response.setStatus(e.getStatus());
                if (e.getStatus() < 500) {
                    response.writeBody(e.getMessage());
                } else {
                    response.writeBody(e);
                }
            } catch (Exception e) {
                response.setStatus(500);
                response.writeBody(e);
            }
        });

        //启动服务器
        httpServer.start();
    }

    public void stop() throws Exception {
        if (httpServer == null) {
            return;
        }
        httpServer.stop(1);
    }

    public ThreadPoolExecutor getHttpThreadPool() {
        return httpThreadPool;
    }

    private  ThreadPoolExecutor httpThreadPool(int coreThread, int maxThread) {
        return new ThreadPoolExecutor(coreThread, maxThread, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadFactory() {
                    private final AtomicInteger threadCount = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable runnable) {
                        return new Thread(runnable, "simpleHttpserver-" + threadCount.incrementAndGet());
                    }
                });
    }
}
