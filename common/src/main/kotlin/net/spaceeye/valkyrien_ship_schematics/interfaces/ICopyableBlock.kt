package net.spaceeye.valkyrien_ship_schematics.interfaces

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip

/**
 * Should be inherited by block, not block entity
 */
interface ICopyableBlock {
    /**
     * Should be called on copy of the blocks.
     * @return If returns tag, then copy fn should save that tag. If returns null, then copy fn should get tag from block entity if it exists.
     */
    fun onCopy(level: ServerLevel, pos: BlockPos, state: BlockState, be: BlockEntity?, shipsBeingCopied: List<ServerShip>, centerPositions: Map<Long, Vector3d>): CompoundTag?

    /**
     * Should be called for all ICopyableBlock's after all ships were created and all blocks were placed
     */
    fun onPaste(level: ServerLevel, pos: BlockPos, state: BlockState, oldShipIdToNewId: Map<Long, Long>, centerPositions: Map<Long, Pair<Vector3d, Vector3d>>, tag: CompoundTag?): CompoundTag?
}