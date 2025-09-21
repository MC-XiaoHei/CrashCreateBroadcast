package cn.xor7.xiaohei.ccb.config

import cn.xor7.xiaohei.ccb.utils.ExceptionType
import cn.xor7.xiaohei.ccb.utils.oneSec
import cn.xor7.xiaohei.ccb.utils.scheduleRepeating
import cn.xor7.xiaohei.ccb.utils.tickToDuration
import cn.xor7.xiaohei.ccb.utils.toComponent
import cn.xor7.xiaohei.ccb.utils.type
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.title.Title.Times.times
import net.kyori.adventure.title.TitlePart
import org.bukkit.entity.Player
import org.leavesmc.leaves.event.player.UpdateSuppressionEvent

data class UserConfig(
    val broadcastMethods: Map<ExceptionType, Set<BroadcastMethod>>
) {
    fun broadcast(player: Player, data: UpdateSuppressionEvent) {
        val methods = broadcastMethods[data.type] ?: return
        methods.forEach { it.broadcast(player, data) }
    }
}

data class BroadcastMethod(
    val displayType: DisplayType,
    val messageTemplate: String,
) {
    fun broadcast(player: Player, data: UpdateSuppressionEvent) {
        displayType.sender(player, data.toComponent(messageTemplate))
    }
}

sealed class DisplayType(val name: String, val sender: Player.(Component) -> Unit) {
    object Chat : DisplayType("chat", Player::sendMessage)

    class ActionBar(displayTicks: Int) : DisplayType(
        "action_bar",
        {
            scheduleRepeating(
                task = { sendActionBar(it) },
                periodTicks = oneSec,
                retired = { sendActionBar(empty()) },
                until = displayTicks.toLong(),
            )
        },
    )

    class Title(fadeInTicks: Int, displayTicks: Int, fadeOutTicks: Int, part: TitlePart<Component>) : DisplayType(
        "title",
        {
            sendTitlePart(part, it)
            sendTitlePart(
                TitlePart.TIMES,
                times(
                    fadeInTicks.tickToDuration(),
                    displayTicks.tickToDuration(),
                    fadeOutTicks.tickToDuration(),
                ),
            )
        },
    )
}