## How to add copy & paste compat for a block/block entity: 
Implement ICopyableBlock

## How to add copy & paste compat for custom data:
Use ShipSchematic.registerCopyPasteEvents

## How to add copy & paste compat for Attachments:
For now ShipSchematic.registerCopyPasteEvents as idk how i want it to work rn

## How to add to project (for arch):
1) Add this repo as a submodule with "git submodule add https://github.com/SuperSpaceEye/Valkyrien-Ship-Schematics"
2) Run "git submodule init"
3) In common build.gradle add ``` implementation "net.spaceeye:valkyrien-ship-schematics-$project.name-$minecraft_version:1.0" ```
4) In forge build.gradle add ``` forgeRuntimeLibrary include(modImplementation("net.spaceeye:valkyrien-ship-schematics-$project.name-$minecraft_version:1.0")) ``` ~~i think~~
5) In fabric build.gradle add ``` include(modImplementation("net.spaceeye:valkyrien-ship-schematics-$project.name-$minecraft_version:1.0")) ```
6) build Valkyrien-Ship-Schematics

Sometimes you may need to delete ".gradle/loom-cache/remapped_mods/loom_mappings.../net/spaceeye" and reload or just build Valkyrien-Ship-Schematics directly idk.