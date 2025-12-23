package pl.endixon.sectors.paper.util;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public final class CpuUtil {

    private static final OperatingSystemMXBean OS_BEAN = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private CpuUtil() {}

    public static double getCpuLoad() {
        double systemLoad = OS_BEAN.getCpuLoad();
        double processLoad = OS_BEAN.getProcessCpuLoad();
        double load = Math.max(systemLoad, processLoad);
        if (load <= 0) {
            return 0.0;
        }
        return load * 100.0;
    }
}