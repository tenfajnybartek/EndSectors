/*
 *
 *  EndSectors  Non-Commercial License
 *  (c) 2025 Endixon
 *
 *  Permission is granted to use, copy, and
 *  modify this software **only** for personal
 *  or educational purposes.
 *
 *   Commercial use, redistribution, claiming
 *  this work as your own, or copying code
 *  without explicit permission is strictly
 *  prohibited.
 *
 *  Visit https://github.com/Endixon/EndSectors
 *  for more info.
 *
 */

package pl.endixon.sectors.proxy.util;

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