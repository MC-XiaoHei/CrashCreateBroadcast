package cn.xor7.xiaohei.ccb.config

import cn.xor7.xiaohei.ccb.utils.oneSec
import cn.xor7.xiaohei.ccb.utils.scheduleRepeating
import cn.xor7.xiaohei.ccb.utils.tickToDuration
import cn.xor7.xiaohei.ccb.utils.toComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.title.Title.Times.times
import net.kyori.adventure.title.TitlePart
import org.bukkit.entity.Player
import org.leavesmc.leaves.event.player.UpdateSuppressionEvent

typealias ThrowableClassName = String

data class UserConfig(
    val broadcastMethods: Map<ThrowableClassName, Set<BroadcastMethod>>
)

data class BroadcastMethod(
    val displayType: DisplayType,
    val minimessageTemplate: String,
) {
    fun broadcast(player: Player, data: UpdateSuppressionEvent) {
        displayType.sender(player, data.toComponent("example_template"))
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