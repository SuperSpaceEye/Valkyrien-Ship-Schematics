package net.spaceeye.valkyrien_ship_schematics

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerLevel
import net.spaceeye.valkyrien_ship_schematics.interfaces.IShipSchematic
import org.valkyrienskies.core.api.ships.ServerShip
import net.spaceeye.valkyrien_ship_schematics.interfaces.ISerializable

typealias CopyEventSignature = (
    level: ServerLevel,
    shipsToBeSaved: List<ServerShip>,
    globalMap: MutableMap<String, Any>,
    unregister: () -> Unit
) -> ISerializable?
typealias PasteEventSignature = (
    level: ServerLevel,
    loadedShips: List<Pair<ServerShip, Long>>,
    file: ISerializable?,
    globalMap: MutableMap<String, Any>,
    unregister: () -> Unit
) -> Unit

private data class Events(
    val copyEvent: CopyEventSignature,
    val pasteBeforeEvent: PasteEventSignature,
    val pasteAfterEvent: PasteEventSignature,
    val next: MutableList<String> = mutableListOf(),
    var globalMap: MutableMap<String, Any> = mutableMapOf()
)

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

    private val rootEvents = mutableMapOf<String, Events>()
    private val allEvents = mutableMapOf<String, Events>()
    private val toAddEvent = mutableMapOf<String, MutableList<String>>()

    /**
     * WARNING onPasteBefore IS DUMB AND WILL PROBABLY GET CHANGED IN THE FUTURE
     * @param onPasteAfter - will be called after all ships are created, all blocks were placed and loaded
     * @param onPasteBefore - will be called after all ships are created but before any blocks are placed (THIS WILL PROBABLY GET CHANGED IN THE FUTURE)
     */
    fun registerCopyPasteEvents(name: String, onCopy: CopyEventSignature, onPasteAfter: PasteEventSignature, onPasteBefore: PasteEventSignature = { _, _, _, _, _ ->}) {
        val events = Events(onCopy, onPasteBefore, onPasteAfter)
        rootEvents[name] = events
        allEvents[name] = events

        val toAdd = toAddEvent[name]
        if (toAdd != null) {
            events.next.addAll(toAdd)
            toAddEvent.remove(name)
        }
    }

    /**
     * Will register a node event, that will be called after the target event. If the target event doesn't exist, it will not be called
     */
    fun registerOrderedCopyPasteEvents(name: String, after: String, onCopy: CopyEventSignature, onPasteAfter: PasteEventSignature, onPasteBefore: PasteEventSignature = { _, _, _, _, _ ->}) {
        val events = Events(onCopy, onPasteBefore, onPasteAfter)
        allEvents[name] = events

        val toAdd = toAddEvent[name]
        if (toAdd != null) {
            events.next.addAll(toAdd)
            toAddEvent.remove(name)
        }

        val node = allEvents[after]
        if (node == null) {
            toAddEvent.getOrPut(after) { mutableListOf() }.add(name)
            return
        }
        node.next.add(name)
    }

    fun getGlobalMap(name: String): Map<String, Any>? = allEvents[name]?.globalMap

    /**
     * Should be called on copy, before blocks were copied
     */
    fun onCopy(level: ServerLevel, shipsToBeSaved: List<ServerShip>): List<Pair<String, ISerializable>> {
        val toRemove = mutableListOf<String>()
        val toReturn = mutableListOf<Pair<String, ISerializable>>()

        val toExecute = mutableListOf<String>()
        val executed = mutableSetOf<String>()

        toExecute.addAll(rootEvents.keys)

        while (toExecute.isNotEmpty()) {
            val name = toExecute.removeLast()
            if (executed.contains(name)) {continue}
            executed.add(name)

            val event = allEvents[name] ?: continue

            val file = try { event.copyEvent(level, shipsToBeSaved, event.globalMap) {toRemove.add(name)}
            } catch (e: Exception) { ELOG("Event $name failed onCopy with exception:\n${e.stackTraceToString()}"); continue
            } catch (e: Error)     { ELOG("Event $name failed onCopy with exception:\n${e.stackTraceToString()}"); continue}
            if (file != null) toReturn.add(Pair(name, file))

            toExecute.addAll(event.next.filter { !executed.contains(it) })
        }
        toRemove.forEach { allEvents.remove(it); rootEvents.remove(it) }

        return toReturn
    }

    // Is called after all ServerShips are created, but blocks haven't been placed yet, so VS didn't "create them"
    fun onPasteBeforeBlocksAreLoaded(level: ServerLevel, emptyShips: List<Pair<ServerShip, Long>>, files: List<Pair<String, ISerializable>>) {
        val toRemove = mutableListOf<String>()
        val filesMap = files.toMap()

        val toExecute = mutableListOf<String>()
        val executed = mutableSetOf<String>()

        toExecute.addAll(rootEvents.keys)

        while (toExecute.isNotEmpty()) {
            val name = toExecute.removeLast()
            if (executed.contains(name)) {continue}
            executed.add(name)

            val event = allEvents[name] ?: continue

            try { event.pasteBeforeEvent(level, emptyShips, filesMap[name], event.globalMap) {toRemove.add(name)}
            } catch (e: Exception) { ELOG("Event $name failed onPasteBeforeBlocksAreLoaded with exception:\n${e.stackTraceToString()}"); continue
            } catch (e: Error)     { ELOG("Event $name failed onPasteBeforeBlocksAreLoaded with exception:\n${e.stackTraceToString()}"); continue}

            toExecute.addAll(event.next.filter { !executed.contains(it) })
        }
        toRemove.forEach { allEvents.remove(it); rootEvents.remove(it) }
    }

    // Is called after all ServerShips are created with blocks placed in shipyard
    fun onPasteAfterBlocksAreLoaded(level: ServerLevel, loadedShips: List<Pair<ServerShip, Long>>, files: List<Pair<String, ISerializable>>) {
        val toRemove = mutableListOf<String>()
        val filesMap = files.toMap()

        val toExecute = mutableListOf<String>()
        val executed = mutableSetOf<String>()

        toExecute.addAll(rootEvents.keys)

        while (toExecute.isNotEmpty()) {
            val name = toExecute.removeLast()
            if (executed.contains(name)) {continue}
            executed.add(name)

            val event = allEvents[name] ?: continue

            try { event.pasteAfterEvent(level, loadedShips, filesMap[name], event.globalMap) {toRemove.add(name)}
            } catch (e: Exception) { ELOG("Event $name failed onPasteAfterBlocksAreLoaded with exception:\n${e.stackTraceToString()}"); continue
            } catch (e: Error)     { ELOG("Event $name failed onPasteAfterBlocksAreLoaded with exception:\n${e.stackTraceToString()}"); continue}

            toExecute.addAll(event.next.filter { !executed.contains(it) })
        }
        toRemove.forEach { allEvents.remove(it); rootEvents.remove(it) }
    }
}