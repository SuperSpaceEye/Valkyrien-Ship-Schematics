## How to add copy & paste compat for a block/block entity: 
Implement ICopyableBlock

## *How* to implement ICopyableBlock
You only need to implement ICopyableBlock if you have logic that affects more than one ship

Let's say that your block creates a constraint between 2 ships, to correctly update it you will need to update shipId's and positions of constraint in ICopyableBlock.onPaste

Implementation can be something like this:

```kotlin
override fun onPaste(level: ServerLevel, pos: BlockPos, state: BlockState, oldShipIdToNewId: Map<Long, Long>, centerPositions: Map<Long, Pair<Vector3d, Vector3d>>, tag: CompoundTag?): CompoundTag? {
    ...
    val newId1 = oldShipIdToNewId[oldId1]
    val newId2 = oldShipIdToNewId[oldId2]
    val newPos1 = centerPositions[oldId1]?.let {(old, new) -> oldPos1.sub(old).add(new)}
    val newPos2 = centerPositions[oldId2]?.let {(old, new) -> oldPos2.sub(old).add(new)}
    ...
}
```

## How to add copy & paste compat for custom data:
Use ShipSchematic.registerCopyPasteEvents

## How to add copy & paste compat for Attachments:
For now ShipSchematic.registerCopyPasteEvents as idk how i want it to work rn

## Schematic paste logic
I've found this logic to work best for schematic pasting.
1) For each saved ship do
   1. Create ship and freeze it
   2. Place all blocks of that ship on shipyard, but do not load them
   3. Save all block entities and ICopyableBlock's to load later
2) After all ships were created and all blocks were placed, load saved blocks
3) Update all blocks on all ships
4) Teleport all ships to their actual positions after waiting for several mc ticks (VS doesn't seem to update COM pos of ships until next tick, so idk how to calculate teleport positions)
5) Create all entities
6) Call ShipSchematic.onPasteAfterBlocksAreLoaded
7) Unfreeze created ships

## How to add to project (for arch):
1) Add this repo as a submodule with "git submodule add https://github.com/SuperSpaceEye/Valkyrien-Ship-Schematics"
2) Run "git submodule init"
3) In common build.gradle add ``` modImplementation ("net.spaceeye:valkyrien-ship-schematics-$project.name:1.18.2-1.1") {transitive false}" ```
4) In forge build.gradle add ``` include modImplementation("net.spaceeye:valkyrien-ship-schematics-$project.name:1.18.2-1.1") {transitive false} ```
5) In fabric build.gradle add ``` include modImplementation("net.spaceeye:valkyrien-ship-schematics-$project.name:1.18.2-1.1") {transitive false} ```
6) Add mavenLocal to all dependencies if not already added
7) Build Valkyrien-Ship-Schematics from terminal, it will build all necessary jars and publish it to local maven
8) Sync your main project

If version updates, delete ".gradle/loom-cache/remapped_mods/loom_mappings.../net/spaceeye" and build Valkyrien-Ship-Schematics again.