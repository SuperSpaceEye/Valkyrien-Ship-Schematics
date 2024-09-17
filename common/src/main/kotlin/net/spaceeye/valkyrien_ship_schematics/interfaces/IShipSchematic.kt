package net.spaceeye.valkyrien_ship_schematics.interfaces

import io.netty.buffer.ByteBuf

interface IShipSchematic {
    fun getInfo(): IShipSchematicInfo

    fun serialize(): ISerializable
    fun deserialize(buf: ByteBuf): Boolean
}