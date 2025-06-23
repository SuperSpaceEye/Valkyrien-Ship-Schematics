package net.spaceeye.valkyrien_ship_schematics.containers.v1

import net.minecraft.core.BlockPos

open class ChunkyBlockData<T>(): Iterable<Pair<BlockPos, T>> {
    val blocks = mutableMapOf<BlockPos, MutableMap<BlockPos, T>>()
    val sortedChunkKeys = mutableListOf<BlockPos>()

    fun add(x: Int, y: Int, z: Int, item: T) {
        blocks.getOrPut(BlockPos(x shr 4, 0, z shr 4))
        { mutableMapOf() }[BlockPos(x and 15, y, z and 15)] = item
    }

    fun get(x: Int, y: Int, z: Int): T? = blocks.get(BlockPos(x shr 4, 0, z shr 4))?.get(BlockPos(x and 15, y, z and 15))
    fun get(pos: BlockPos): T? = get(pos.x, pos.y, pos.z)

    fun updateKeys() {
        sortedChunkKeys.clear()
        sortedChunkKeys.addAll(blocks.keys)
        sortedChunkKeys.sort()
    }

    inline fun chunkForEach(chunkNum: Int, fn: (x: Int, y: Int, z: Int, item: T) -> Unit) {
        val cpos = sortedChunkKeys[chunkNum]

        blocks[cpos]!!.forEach { (pos, item) -> fn(pos.x + (cpos.x shl 4), pos.y, pos.z + (cpos.z shl 4), item) }
    }

    inline fun forEach(fn: (x: Int, y: Int, z: Int, item: T) -> Unit) {
        blocks.forEach { (cpos, chunk) ->
            chunk.forEach { (pos, item) ->
                fn(pos.x + (cpos.x shl 4), pos.y, pos.z + (cpos.z shl 4), item)
            }
        }
    }

    override fun iterator(): Iterator<Pair<BlockPos, T>> =
        object : Iterator<Pair<BlockPos, T>> {
            var cpos = 0
            var bpos = 0

            var chunk = emptyList<Pair<BlockPos, T>>()

            fun updateChunk() {
                val cpos = sortedChunkKeys[cpos]
                chunk = blocks[cpos]!!.map { (pos, it) -> Pair(BlockPos(pos.x + (cpos.x shl 4), pos.y, pos.z + (cpos.z shl 4)), it) }
            }

            init {
                updateKeys()
                updateChunk()
            }

            override fun next(): Pair<BlockPos, T> = chunk[bpos]

            var yieldNextTime = false

            override fun hasNext(): Boolean {
                if (sortedChunkKeys.size >= cpos) return false

                while (true) {
                    if (sortedChunkKeys.size >= cpos) return false

                    if (bpos >= chunk.size) {
                        cpos++
                        bpos = 0
                        yieldNextTime = true
                        updateChunk()
                        continue
                    }
                    if (yieldNextTime) {
                        yieldNextTime = false
                        return true
                    }

                    bpos++
                    return true
                }
            }
        }
}