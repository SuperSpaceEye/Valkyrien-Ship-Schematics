package net.spaceeye.valkyrien_ship_schematics.interfaces.v1

import io.netty.buffer.ByteBuf
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.level.block.Blocks
import net.spaceeye.valkyrien_ship_schematics.ELOG
import net.spaceeye.valkyrien_ship_schematics.containers.CompoundTagSerializable
import net.spaceeye.valkyrien_ship_schematics.containers.RawBytesSerializable
import net.spaceeye.valkyrien_ship_schematics.containers.v1.*
import net.spaceeye.valkyrien_ship_schematics.interfaces.IShipSchematic
import net.spaceeye.valkyrien_ship_schematics.interfaces.ISerializable
import net.spaceeye.valkyrien_ship_schematics.util.getQuaterniond
import net.spaceeye.valkyrien_ship_schematics.util.getVector3d
import net.spaceeye.valkyrien_ship_schematics.util.putQuaterniond
import net.spaceeye.valkyrien_ship_schematics.util.putVector3d
import org.joml.primitives.AABBi

interface SchemSerializeDataV1Impl: IShipSchematic, IShipSchematicDataV1 {
    override fun serialize(): ISerializable {
        val saveTag = CompoundTag()

        serializeShipData(saveTag)
        serializeExtraData(saveTag)
        serializeBlockPalette(saveTag)
        serializeGridDataInfo(saveTag)
        serializeExtraBlockData(saveTag)
        serializeEntityData(saveTag)

        return CompoundTagSerializable(saveTag)
    }

    override fun deserialize(buf: ByteBuf): Boolean {
        val file = CompoundTagSerializable(CompoundTag())
        file.deserialize(FriendlyByteBuf(buf))

        val saveTag = file.tag!!

        deserializeShipData(saveTag)
        deserializeExtraData(saveTag)
        deserializeBlockPalette(saveTag)
        deserializeGridDataInfo(saveTag)
        deserializeExtraBlockData(saveTag)
        deserializeEntityData(saveTag)

        return true
    }

    private fun serializeShipData(tag: CompoundTag) {
        val shipDataTag = CompoundTag()

        shipDataTag.putVector3d("maxObjectPos", info!!.maxObjectPos)

        val shipsDataTag = ListTag()
        info!!.shipsInfo.forEach {
            val shipTag = CompoundTag()

            shipTag.putLong("id", it.id)
            shipTag.putVector3d("rptc", it.relPositionToCenter)

            shipTag.putInt("sb_mx", it.shipAABB.minX())
            shipTag.putInt("sb_my", it.shipAABB.minY())
            shipTag.putInt("sb_mz", it.shipAABB.minZ())
            shipTag.putInt("sb_Mx", it.shipAABB.maxX())
            shipTag.putInt("sb_My", it.shipAABB.maxY())
            shipTag.putInt("sb_Mz", it.shipAABB.maxZ())

            shipTag.putVector3d("pis", it.positionInShip)
            shipTag.putDouble("sc", it.shipScale)
            shipTag.putQuaterniond("rot", it.rotation)

            shipsDataTag.add(shipTag)
        }

        shipDataTag.put("data", shipsDataTag)
        tag.put("shipData", shipDataTag)
    }

    private fun serializeExtraData(tag: CompoundTag) {
        val extraDataTag = CompoundTag()

        extraData.forEach { (name, file) -> extraDataTag.putByteArray(name, file.serialize().accessByteBufWithCorrectSize()) }

        tag.put("extraData", extraDataTag)
    }

    private fun serializeBlockPalette(tag: CompoundTag) {
        val paletteTag = ListTag()

        for (i in 0 until blockPalette.getPaletteSize()) {
            val state = blockPalette.fromId(i)
            paletteTag.add(NbtUtils.writeBlockState(state ?: run { ELOG("Block palette somehow returned null for id $i") ; Blocks.AIR.defaultBlockState() }))
        }

        tag.put("blockPalette", paletteTag)
    }

    private fun serializeExtraBlockData(tag: CompoundTag) {
        val extraBlockData = ListTag()

        flatTagData.forEach { extraBlockData.add(it) }

        tag.put("extraBlockData", extraBlockData)
    }

    private fun serializeGridDataInfo(tag: CompoundTag) {
        val gridDataTag = CompoundTag()

        blockData.forEach {
                (shipId, data) ->
            val dataTag = ListTag()

            data.forEach {x, y, z, it ->
                val item = CompoundTag()

                item.putInt("x", x)
                item.putInt("y", y)
                item.putInt("z", z)
                item.putInt("pid", it.paletteId)
                item.putInt("edi", it.extraDataId)

                dataTag.add(item)
            }

            gridDataTag.put(shipId.toString(), dataTag)
        }

        tag.put("gridData", gridDataTag)
    }

    private fun serializeEntityData(tag: CompoundTag) {
        val entityDataTag = CompoundTag()

        entityData.forEach { (id, data) ->
            val dataTag = ListTag()

            dataTag.addAll(
                data.map { (pos, tag) ->
                    val item = CompoundTag()

                    item.putVector3d("pos", pos)
                    item.put("entity", tag)
                    item
            })

            entityDataTag.put(id.toString(), dataTag)
        }

        tag.put("entityData", entityDataTag)
    }




    private fun deserializeShipData(tag: CompoundTag) {
        val shipDataTag = tag.get("shipData") as CompoundTag

        val maxObjectPos = shipDataTag.getVector3d("maxObjectPos")!!

        val shipsDataTag = shipDataTag.get("data") as ListTag

        info = ShipSchematicInfo( maxObjectPos,
            shipsDataTag.map {shipTag ->
                shipTag as CompoundTag

                ShipInfo(
                    shipTag.getLong("id"),
                    shipTag.getVector3d("rptc")!!,
                    AABBi(
                        shipTag.getInt("sb_mx"),
                        shipTag.getInt("sb_my"),
                        shipTag.getInt("sb_mz"),
                        shipTag.getInt("sb_Mx"),
                        shipTag.getInt("sb_My"),
                        shipTag.getInt("sb_Mz"),
                    ),
                    shipTag.getVector3d("pis")!!,
                    shipTag.getDouble("sc"),
                    shipTag.getQuaterniond("rot")!!
                )
            }
        )
    }

    private fun deserializeExtraData(tag: CompoundTag) {
        val extraDataTag = tag.get("extraData") as CompoundTag

        extraData = extraDataTag.allKeys.map { name ->
            val byteArray = extraDataTag.getByteArray(name)

            Pair(name, RawBytesSerializable(byteArray))
        }.toMutableList()
    }

    private fun deserializeBlockPalette(tag: CompoundTag) {
        val paletteTag = tag.get("blockPalette") as ListTag

        val lookup = BuiltInRegistries.BLOCK.asLookup()
        val newPalette = paletteTag.mapIndexed { i, it ->
            val state = NbtUtils.readBlockState(lookup, it as CompoundTag)
            if (state.isAir) { ELOG("State under id $i is air. $it") }
            Pair(i, state)
        }

        blockPalette.setPalette(newPalette)
    }

    private fun deserializeExtraBlockData(tag: CompoundTag) {
        val extraBlockData = tag.get("extraBlockData") as ListTag

        flatTagData = extraBlockData.map { it as CompoundTag }.toMutableList()
    }

    private fun deserializeGridDataInfo(tag: CompoundTag) {
        val gridDataTag = tag.get("gridData") as CompoundTag

        for (k in gridDataTag.allKeys) {
            val dataTag = gridDataTag.get(k) as ListTag
            val data = blockData.getOrPut(k.toLong()) { ChunkyBlockData() }

            dataTag.forEach {blockTag ->
                blockTag as CompoundTag

                data.add(
                    blockTag.getInt("x"),
                    blockTag.getInt("y"),
                    blockTag.getInt("z"),
                    BlockItem(
                        blockTag.getInt("pid"),
                        blockTag.getInt("edi")
                    )
                )
            }
        }
    }

    private fun deserializeEntityData(tag: CompoundTag) {
        if (!tag.contains("entityData")) {return}
        val entityDataTag = tag.getCompound("entityData")

        for (k in entityDataTag.allKeys) {
            val dataTag = entityDataTag.get(k) as ListTag

            entityData[k.toLong()] = dataTag.map {
                it as CompoundTag;
                EntityItem(it.getVector3d("pos")!!, it.getCompound("entity"))
            }
        }
    }
}