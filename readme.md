## How to add copy & paste compat for a block/block entity: 
Implement ICopyableBlock

## How to add copy & paste compat for custom data:
Use ShipSchematic.registerCopyPasteEvents

## How to add copy & paste compat for Attachments:
For now ShipSchematic.registerCopyPasteEvents as idk how i want it to work rn

## How to add to project (for arch):
1) Add this repo as a submodule with "git submodule add https://github.com/SuperSpaceEye/Valkyrien-Ship-Schematics"
2) Run "git submodule init"
3) In common build.gradle add ``` modImplementation ("net.spaceeye:valkyrien-ship-schematics-$project.name:1.18.2-1.1") {transitive false}" ```
4) In forge build.gradle add ``` include modImplementation("net.spaceeye:valkyrien-ship-schematics-$project.name:1.18.2-1.1") {transitive false} ```
5) In fabric build.gradle add ``` include modImplementation("net.spaceeye:valkyrien-ship-schematics-$project.name:1.18.2-1.1") {transitive false} ```
6) build Valkyrien-Ship-Schematics

If version updates, delete ".gradle/loom-cache/remapped_mods/loom_mappings.../net/spaceeye" and build Valkyrien-Ship-Schematics again.