package net.spaceeye.valkyrien_ship_schematics.interfaces

import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf

interface Serializable {
    fun serialize(): FriendlyByteBuf
    fun deserialize(buf: FriendlyByteBuf)

    fun getBuffer() = FriendlyByteBuf(Unpooled.buffer(64))
}