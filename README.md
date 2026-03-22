# Another WorldEdit Clone

A generic WorldEdit clone for BTA. Works as a client or server mod.

## Overview

This mod aims to achieve a decent degree of feature parity with the official
WorldEdit implementation. That being said, this is not a port and there are
minor differences compared to modern WorldEdit. This project does not contain
any code from the official implementation.

## Current Feature List
- `//set`, `//replace`, `//move`
- `//walls`, `//sphere`, `//hsphere`, `//cyl`, `//hcyl`
- `//copy`, `//paste`, `//cut`, `//stack`, `//flip`
- `//undo`, `//redo`
- `/up`, `/thru`, `/ascend`, `/descend`
- `//shift`, `//expand`, `//trim`
- `//count`, `//distr`
- `//drawsel` to display selections
- Schematics
- Random block patterns (like `50%stone,50%dirt`)
- Block masks
- Configurable permissions via `/wewhitelist`
- Toggleable edit wand via `/togglewand`


## Planned Features
- `//rotate`
- `//drawsel` on servers
- Don't try to modify unloaded blocks
