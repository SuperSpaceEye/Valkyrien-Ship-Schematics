package net.spaceeye.valkyrien_ship_schematics

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerLevel
import net.spaceeye.valkyrien_ship_schematics.interfaces.IShipSchematic
import org.valkyrienskies.core.api.ships.ServerShip
import net.spaceeye.valkyrien_ship_schematics.interfaces.ISerializable

object ShipSchematic {
    const val schematicIdentifier = "vschem"

    /**
     * Should be used to serialize schematic
     */
    @JvmStatic
    fun writeSchematicToBuffer(schematic: IShipSchematic): ByteBuf? {
        val buf = FriendlyByteBuf(Unpooled.buffer())

        val serialized = try {
            schematic.serialize().serialize()
        } catch (e: Exception) { ELOG("Failed to load schematic with exception:\n${e.stackTraceToString()}"); return null
        } catch (e: Error) { ELOG("Failed to load schematic with error:\n${e.stackTraceToString()}"); return null }

        buf.writeUtf(schematicIdentifier)
        buf.writeUtf(SchematicRegistry.typeToString(schematic::class.java))
        buf.writeBytes(serialized)

        return Unpooled.wrappedBuffer(buf.accessByteBufWithCorrectSize())
    }

    /**
     * Should be used to deserialize schematic
     */
    @JvmStatic
    fun getSchematicFromBytes(bytes: ByteArray): IShipSchematic? {
        val buffer = FriendlyByteBuf(Unpooled.wrappedBuffer(bytes))

        val schematic = try {
            val identifier = buffer.readUtf()
            //TODO
            if (identifier != schematicIdentifier) {throw AssertionError()}

            SchematicRegistry.strTypeToSupplier(buffer.readUtf()).get()
        } catch (e: AssertionError) {return null
        } catch (e: Exception) { ELOG("Failed to load schematic with exception:\n${e.stackTraceToString()}"); return null
        } catch (e: Error) { ELOG("Failed to load schematic with error:\n${e.stackTraceToString()}"); return null }

        try {
            schematic.deserialize(buffer)
        } catch (e: AssertionError) {return null
        } catch (e: Exception) { ELOG("Failed to load schematic with exception:\n${e.stackTraceToString()}"); return null
        } catch (e: Error) { ELOG("Failed to load schematic with error:\n${e.stackTraceToString()}"); return null }

        return schematic
    }


    /**
     * Should be called on copy, before blocks were copied
     */
    fun onCopy(level: ServerLevel, shipsToBeSaved: List<ServerShip>): List<Pair<String, ISerializable>> {
        val toReturn = mutableListOf<Pair<String, ISerializable>>()

        val (roots, branches) = SchematicEventRegistry.makeOrderedInstances()
        val executed = mutableSetOf<String>()

        while (roots.isNotEmpty()) {
            val event = roots.removeLast()
            val name = event.getName()
            if (executed.contains(name)) {continue}
            executed.add(name)

            val file = try { event.onCopy(level, shipsToBeSaved)
            } catch (e: Exception) { ELOG("Event $name failed onCopy with exception:\n${e.stackTraceToString()}"); continue
            } catch (e: Error)     { ELOG("Event $name failed onCopy with exception:\n${e.stackTraceToString()}"); continue}
            if (file != null) toReturn.add(Pair(name, file))

            val newRoots = branches[name] ?: continue
            roots.addAll(newRoots.filter { !executed.contains(it.first) }.map { it.second.get() })
        }

        return toReturn
    }

    // Is called after all ServerShips are created, but blocks haven't been placed yet, so VS didn't "create them"
    fun onPasteBeforeBlocksAreLoaded(level: ServerLevel, maybeLoadedShips: List<Pair<ServerShip, Long>>, emptyShip: Pair<ServerShip, Long>, files: Map<String, ISerializable>) {
        val (roots, branches) = SchematicEventRegistry.makeOrderedInstances()
        val executed = mutableSetOf<String>()

        while (roots.isNotEmpty()) {
            val event = roots.removeLast()
            val name = event.getName()
            if (executed.contains(name)) {continue}
            executed.add(name)

            try { event.onPasteBeforeBlocksAreLoaded(level, maybeLoadedShips, emptyShip, files[name]?.let { {it.serialize()} })
            } catch (e: Exception) { ELOG("Event $name failed onPasteBeforeBlocksAreLoaded with exception:\n${e.stackTraceToString()}"); continue
            } catch (e: Error)     { ELOG("Event $name failed onPasteBeforeBlocksAreLoaded with exception:\n${e.stackTraceToString()}"); continue}

            val newRoots = branches[name] ?: continue
            roots.addAll(newRoots.filter { !executed.contains(it.first) }.map { it.second.get() })
        }
    }

    // Is called after all ServerShips are created with blocks placed in shipyard
    fun onPasteAfterBlocksAreLoaded(level: ServerLevel, loadedShips: List<Pair<ServerShip, Long>>, files: Map<String, ISerializable>) {
        val (roots, branches) = SchematicEventRegistry.makeOrderedInstances()
        val executed = mutableSetOf<String>()

        while (roots.isNotEmpty()) {
            val event = roots.removeLast()
            val name = event.getName()
            if (executed.contains(name)) {continue}
            executed.add(name)

            try { event.onPasteAfterBlocksAreLoaded(level, loadedShips, files[name]?.let { {it.serialize()} })
            } catch (e: Exception) { ELOG("Event $name failed onPasteAfterBlocksAreLoaded with exception:\n${e.stackTraceToString()}"); continue
            } catch (e: Error)     { ELOG("Event $name failed onPasteAfterBlocksAreLoaded with exception:\n${e.stackTraceToString()}"); continue}

            val newRoots = branches[name] ?: continue
            roots.addAll(newRoots.filter { !executed.contains(it.first) }.map { it.second.get() })
        }
    }
}