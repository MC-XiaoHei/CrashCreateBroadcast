package cn.xor7.xiaohei.ccb

import cn.xor7.xiaohei.ccb.listeners.UpdateSuppressionListener
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
object CrashCreateBroadcastPlugin : JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(UpdateSuppressionListener, this)
    }
}
