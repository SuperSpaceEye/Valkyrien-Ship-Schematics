package net.spaceeye.valkyrien_ship_schematics.interfaces

import net.minecraft.world.level.block.state.BlockState

interface IBlockStatePalette {
    fun toId(state: BlockState): Int
    fun fromId(id: Int): BlockState?

    fun getPaletteSize(): Int

    fun setPalette(newPalette: List<Pair<Int, BlockState>>)
}