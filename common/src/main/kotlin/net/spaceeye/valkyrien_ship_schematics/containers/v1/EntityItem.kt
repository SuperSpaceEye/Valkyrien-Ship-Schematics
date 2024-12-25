package net.spaceeye.valkyrien_ship_schematics.containers.v1

import net.minecraft.nbt.CompoundTag
import org.joml.Vector3d

data class EntityItem(var pos: Vector3d, var tag: CompoundTag)