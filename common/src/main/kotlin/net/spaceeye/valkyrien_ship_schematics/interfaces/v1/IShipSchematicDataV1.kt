package net.spaceeye.valkyrien_ship_schematics.interfaces.v1

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.spaceeye.valkyrien_ship_schematics.containers.v1.BlockItem
import net.spaceeye.valkyrien_ship_schematics.interfaces.IBlockStatePalette
import net.spaceeye.valkyrien_ship_schematics.containers.v1.ChunkyBlockData
import net.spaceeye.valkyrien_ship_schematics.containers.v1.EntityItem
import org.valkyrienskies.core.api.ships.properties.ShipId

interface IShipSchematicDataV1 {
    var blockPalette: IBlockStatePalette
    var blockData: MutableMap<ShipId, ChunkyBlockData<BlockItem>>
    var entityData: MutableMap<ShipId, List<EntityItem>>

    /**
     * Index of the item is the extraDataId in BlockItem
     */
    var flatTagData: MutableList<CompoundTag>

    /**
     * Stores returns of [net.spaceeye.valkyrien_ship_schematics.interfaces.ISchematicEvent]
     */
    var extraData: MutableList<Pair<String, FriendlyByteBuf>>
}