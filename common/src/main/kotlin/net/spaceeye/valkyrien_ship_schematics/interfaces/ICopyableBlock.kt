package net.spaceeye.valkyrien_ship_schematics.interfaces

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.core.api.ships.ServerShip

interface ICopyableBlock {
    /**
     * Should be called on copy
     * @return If returns tag, then copy fn should save that tag. If returns null, then copy fn should get save tag from block entity itself.
     */
    fun onCopy(level: ServerLevel, pos: BlockPos, state: BlockState, be: BlockEntity?, shipsBeingCopied: List<ServerShip>): CompoundTag?
    /**
     * Should be called on block paste
     * @param delayLoading Allows guaranteed delayed or immediate execution of lambda that you pass to it
     * - - If called with false, and loading of block entities is delayed until all blocks on all ships are placed, should immediately load block entity.
     * - - If called with true, and loading of block entities is immediate, should load after all blocks on all ships are placed.
     * - - If isn't called, should leave loading order to schematic implementation
     * @param finalCallbackAdder Allows adding a callback that will be called after all ships were created, all blocks were placed, and all block entities were loaded.
     */
    fun onPaste(level: ServerLevel, pos: BlockPos, state: BlockState, oldShipIdToNewId: Map<Long, Long>, tag: CompoundTag?, delayLoading: (delay: Boolean, ((CompoundTag?) -> CompoundTag?)?) -> Unit, finalCallbackAdder: (callback: (BlockEntity?) -> Unit) -> Unit)

    /**
     * Should be called for simple blocks
     */
    fun onPasteNoTag(level: ServerLevel, pos: BlockPos, state: BlockState, oldShipIdToNewId: Map<Long, Long>) {}
}