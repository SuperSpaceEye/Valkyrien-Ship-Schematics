package net.spaceeye.valkyrien_ship_schematics.containers

import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.spaceeye.valkyrien_ship_schematics.interfaces.ISerializable

class RawBytesSerializable(val bytes: ByteArray): ISerializable {
    override fun serialize(): FriendlyByteBuf { return FriendlyByteBuf(Unpooled.wrappedBuffer(bytes)) }
    override fun deserialize(buffer: FriendlyByteBuf) { throw AssertionError("Not Implemented. Not going to be implemented.") }
}