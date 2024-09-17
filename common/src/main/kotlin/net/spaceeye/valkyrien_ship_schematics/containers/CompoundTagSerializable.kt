package net.spaceeye.valkyrien_ship_schematics.containers

import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.network.FriendlyByteBuf
import net.spaceeye.valkyrien_ship_schematics.interfaces.ISerializable
import java.io.IOException

class CompoundTagSerializable(var tag: CompoundTag? = null): ISerializable {
    override fun serialize(): FriendlyByteBuf {
        val buffer = ByteBufOutputStream(Unpooled.buffer())
        NbtIo.writeCompressed(tag!!, buffer)
        //Is it efficient? No. But do i care? Also no.
        return FriendlyByteBuf(Unpooled.wrappedBuffer(FriendlyByteBuf(buffer.buffer()).accessByteBufWithCorrectSize()))
    }

    override fun deserialize(buf: FriendlyByteBuf) {
        val buffer = ByteBufInputStream(buf)
        try {
            tag = NbtIo.readCompressed(buffer)
        } catch (e: IOException) {}
    }
}