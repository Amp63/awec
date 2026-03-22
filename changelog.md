# Changelog

## v0.7.1+7.3_04

- Added `//help`
- Added block types to block masks
  - You can now use `t.[block type]` in a mask to match blocks of a specified type
  - Ex:
    - `t.leaves`: Matches any leaf block
    - `t.stone`: Matches any "stone-like" block like stone, granite, basalt
    - `t.stair`: Matches any stair block

## v0.7.0+7.3_04

- Added `//paint`
  - Paints all paintable blocks in a selection to a specific color
  - Ex: `//paint blue`, `//paint red yellow`
- Added `//distr`
- Added `//count`
- Added `//trim`
- Added dye colors to block masks
  - You can now use `c.[color]` in a mask to match any blocks that are painted with that color
  - Ex: `//replace c.blue air`
- `hand` in block masks now matches any metadata if not specified
- Added `HAND` pattern which matches the exact metadata of the held item
- Added `target` and `TARGET` custom block states
  - These refer to the currently targeted block
- Added `h1` - `h9` and `H1` - `H9` custom block states
  - These refer to hotbar slots
- Added all custom block states to command suggestions

## v0.6.2+7.3_04

- Wand can now select corners in air
- `//drawsel` now renders a wand's target position if the wand item is held
- Set blocks no longer update neighbors
- `//walls` now respects global mask
- Fix `//undo` not undoing corners from `//walls`
- Fix weird `//walls` behavior with thin selections
- Refactored selection mixins

## v0.6.1+7.3_04

- Fix exception on //move and //walls commands

## v0.6.0+7.3_04

- Added block masks
- Block pattern and mask arguments now support suggestions
- Added `-m` flag to `//set` and `//paste` commands for specifying masks
- Added `//gmask` to create a global mask
- Added more block aliases
- Added `//flip`
- Added `//cut`
- Removed `//paste` and `//flip` from command options when clipboard is empty
- Removed "Replace" mode from BlockPattern class
  - This functionality has been replaced by masks
- Cancel right click block event while holding wand
  - Prevents opening chests, editing signs, etc. while trying to make selections

## v0.5.0+7.3_04

- Added `//schem delete` and `//schem list`
- `//expand` now expands in a single direction
- Reduce `//schem` permission requirement from admin
- Implemented configurable settings via `worldedit.properties`
  - Added `//reloadconfig` to reload configuration file
- Added formatted status messages
  - Added sfx to error messages
- Added metadata to schematics
  - Schematics now keep track of required mods

## v0.4.0+7.3_04

- Added `//schem`
- Removed Halplibe dependency

## v0.3.0+7.3_04

- Added `//sphere`, `//hsphere`, `//cyl`, `//hcyl`
- Added `//shift`, `//expand`
- Added `//drawsel` to display selections in singleplayer
- Implemented dimension-specific selections and undo history
- Changed method hook for PlayerLeaveServerMixin

## v0.2.0+7.3_04

- Added `//walls`
- Added `//stack`
- Added `//move`
- Added `//undo`, `//redo`
- Added `/thru`, `/ascend`, `/descend`
- Refactoring

## v0.1.1+7.3_04

- Added `//replace`
- Block patterns now retain block metadata by default
- Block patterns now have a proper command argument type

## v0.1.0+7.3_04

- Added `//wand`, `//set`, `//copy`, `//paste`, `//up`, `/togglewand`
- Added block patterns
- Added configurable permissions
