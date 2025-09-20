package cn.xor7.xiaohei.ccb.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent.showText
import net.kyori.adventure.text.format.NamedTextColor.AQUA
import net.kyori.adventure.text.format.NamedTextColor.YELLOW
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.leavesmc.leaves.event.player.UpdateSuppressionEvent

val unknown = text("<unknown>", YELLOW)

fun UpdateSuppressionEvent.toComponent(template: String) = MiniMessage
    .miniMessage()
    .deserialize(
        template,
        toMiniMessageTagResolver(),
    )


private fun UpdateSuppressionEvent.toMiniMessageTagResolver(): TagResolver = buildResolver(
    "player" to (player?.displayName() ?: unknown),
    "world" to (position?.world?.name?.let { text(it, AQUA) } ?: unknown),
    "position" to (position?.let {
        text(
            "x=${it.blockX}, y=${it.blockY}, z=${it.blockZ}",
            AQUA,
        ).hoverEvent(showText(text(position!!.world.name)))
    } ?: unknown),
    "cause" to text(throwable.javaClass.simpleName, AQUA)
        .hoverEvent(showText(text(throwable.stackTraceToString())))
        .clickEvent(ClickEvent.copyToClipboard(throwable.stackTraceToString())),
    "message" to text(throwable.localizedMessage, AQUA),
)

private fun buildResolver(
    vararg placeholders: Pair<String, Component>,
): TagResolver = TagResolver.resolver(
    *placeholders.map { (name, value) ->
        Placeholder.unparsed(name, MiniMessage.miniMessage().serialize(value))
    }.toTypedArray(),
)