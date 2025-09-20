package cn.xor7.xiaohei.ccb.utils

import cn.xor7.xiaohei.ccb.CrashCreateBroadcastPlugin
import org.bukkit.Bukkit
import java.time.Duration
import java.time.Duration.ofMillis

const val oneSec = 20L

fun Int.tickToDuration(): Duration = ofMillis(this * 50L)

fun scheduleRepeating(
    task: () -> Unit,
    periodTicks: Long,
    delayTicks: Long = 0L,
    until: Long = Long.MAX_VALUE,
    retired: () -> Unit = {},
) = Bukkit.getScheduler().runTaskTimer(
    CrashCreateBroadcastPlugin,
    object : Runnable {
        var ticks = 0L
        override fun run() {
            if (ticks >= until) {
                retired()
                return
            }
            task()
            ticks += periodTicks
        }
    },
    delayTicks,
    periodTicks,
)