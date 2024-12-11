package com.bin.sm.jvm;

import com.sun.management.HotSpotDiagnosticMXBean;
import javax.management.MBeanServer;
import java.io.File;
import java.io.OutputStream;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static java.lang.Thread.State.BLOCKED;
import static java.lang.Thread.State.TIMED_WAITING;
import static java.lang.Thread.State.WAITING;

public class JvmManager {


    public static void jstack(OutputStream stream) throws Exception {
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        for (ThreadInfo threadInfo : threadMxBean.dumpAllThreads(true, true)) {
            stream.write(getThreadDumpString(threadInfo).getBytes());
        }
    }

    private static String getThreadDumpString(ThreadInfo threadInfo) {
        StringBuilder sb = new StringBuilder("\"" + threadInfo.getThreadName() + "\"" + " Id="
                + threadInfo.getThreadId() + " " + threadInfo.getThreadState());
        if (threadInfo.getLockName() != null) {
            sb.append(" on " + threadInfo.getLockName());
        }
        if (threadInfo.getLockOwnerName() != null) {
            sb.append(" owned by \"" + threadInfo.getLockOwnerName() + "\" Id=" + threadInfo.getLockOwnerId());
        }
        if (threadInfo.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (threadInfo.isInNative()) {
            sb.append(" (in native)");
        }
        sb.append('\n');
        int i = 0;
        // default is 32, means only print up to 32 lines
        int jstackMaxLine = 32;
        StackTraceElement[] stackTrace = threadInfo.getStackTrace();
        MonitorInfo[] lockedMonitors = threadInfo.getLockedMonitors();
        for (; i < stackTrace.length && i < jstackMaxLine; i++) {
            StackTraceElement ste = stackTrace[i];
            sb.append("\tat ").append(ste.toString());
            sb.append('\n');
            if (i == 0 && threadInfo.getLockInfo() != null) {
                Thread.State ts = threadInfo.getThreadState();
                if (BLOCKED.equals(ts)) {
                    sb.append("\t-  blocked on ").append(threadInfo.getLockInfo());
                    sb.append('\n');
                } else if (WAITING.equals(ts) || TIMED_WAITING.equals(ts)) {
                    sb.append("\t-  waiting on ").append(threadInfo.getLockInfo());
                    sb.append('\n');
                }
            }

            for (MonitorInfo mi : lockedMonitors) {
                if (mi.getLockedStackDepth() == i) {
                    sb.append("\t-  locked ").append(mi);
                    sb.append('\n');
                }
            }
        }
        if (i < stackTrace.length) {
            sb.append("\t...");
            sb.append('\n');
        }

        LockInfo[] locks = threadInfo.getLockedSynchronizers();
        if (locks.length > 0) {
            sb.append("\n\tNumber of locked synchronizers = " + locks.length);
            sb.append('\n');
            for (LockInfo li : locks) {
                sb.append("\t- " + li);
                sb.append('\n');
            }
        }
        sb.append('\n');
        return sb.toString();
    }


    public static Map<String, Object> doDeadlockCheck() {
        String appName = "";
        try {
            ThreadMXBean tBean = ManagementFactory.getThreadMXBean();
            Map<String, Object> json = new HashMap<>();
            long[] dTh = tBean.findDeadlockedThreads();
            if (dTh != null) {
                ThreadInfo[] threadInfo = tBean.getThreadInfo(dTh, Integer.MAX_VALUE);
                StringBuffer sb = new StringBuffer();
                for (ThreadInfo info : threadInfo) {
                    sb.append("\n").append(info);
                }
                json.put("hasdeadlock", true);
                json.put("info", sb);
                return json;
            }
            json.put("hasdeadlock", false);
            return json;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String doDumpThread(long threadId) {
        try {
            ThreadMXBean tBean = ManagementFactory.getThreadMXBean();
            String dumpThread = null;

            if (threadId > 0) {
                ThreadInfo threadInfo = tBean.getThreadInfo(threadId, Integer.MAX_VALUE);
                dumpThread = threadInfo.toString();
            } else {
                ThreadInfo[] dumpAllThreads = tBean.dumpAllThreads(false, false);
                StringBuffer info = new StringBuffer();
                for (ThreadInfo threadInfo : dumpAllThreads) {
                    info.append("\n").append(threadInfo);
                }
                dumpThread = info.toString();
            }
            return dumpThread;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Integer getPid() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Integer.parseInt(runtimeMXBean.getName().split("@")[0]);
    }

    public static long getPidNew() {
        String name = ManagementFactory.getRuntimeMXBean().getName();

        if (name == null) {
            return -1;
        }
        int separatorIndex = name.indexOf("@");
        if (separatorIndex < 0) {
            return -1;
        }
        String potentialPid = name.substring(0, separatorIndex);
        try {
            return Long.parseLong(potentialPid);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String doGC() {
        try {
            ManagementFactory.getMemoryMXBean().gc();
            return "GC Success";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void doHeapDump(String appname) {
        try {
            // 20240405 09:40:26 -> 2024_4_5____9_39_26
            DateFormat fmt = DateFormat.getDateTimeInstance();
            String date = fmt.format(new Date()).replaceAll("\\D", "_");

            String path = System.getProperty("user.dir") + File.separator + "dump";
            File root = new File(path);
            if (!root.exists()) {
                root.mkdirs();
            }
            String dumpPath = path + File.separator +
                    String.format("%s_%s_heap.hprof", appname, date);
            File file = new File(dumpPath);
            String dumpFile = file.getAbsolutePath();

            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
                    server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
            mxBean.dumpHeap(dumpFile, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
