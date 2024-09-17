package net.spaceeye.valkyrien_ship_schematics.interfaces.v1

import net.minecraft.nbt.CompoundTag
import net.spaceeye.valkyrien_ship_schematics.containers.v1.BlockItem
import net.spaceeye.valkyrien_ship_schematics.interfaces.IBlockStatePalette
import net.spaceeye.valkyrien_ship_schematics.interfaces.ISerializable
import net.spaceeye.valkyrien_ship_schematics.containers.v1.ChunkyBlockData
import org.valkyrienskies.core.api.ships.properties.ShipId

interface IShipSchematicDataV1 {
    var blockPalette: IBlockStatePalette
    var blockData: MutableMap<ShipId, ChunkyBlockData<BlockItem>>

    /**
     * Index of the item is the extraDataId in BlockItem
     */
    var flatTagData: MutableList<CompoundTag>

    /**
     * TODO explanation
     */
    var extraData: MutableList<Pair<String, ISerializable>>
}