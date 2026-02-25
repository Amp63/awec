# Changelog

## v0.6.0+7.3_04

- Added block masks
- Block pattern and mask arguments now support suggestions
- Added more block aliases
- Added `//flip`
- Added `//cut`
- Removed `//paste` and `//flip` from command options when clipboard is empty
- Removed "Replace" mode from BlockPattern class
  - This functionality has been replaced by masks

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
