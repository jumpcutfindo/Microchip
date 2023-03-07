# Testing Microchip
Due to the nature of Minecraft and the complexity of the different moving parts involved, the most straightforward and simple way of testing Microchip would be to carry out manual testing. This document intends to present a standardised set of tests that should be carried out in order to ensure that the mod works correctly in lieu of new updates.

## Basic Functionalities
- Opening and closing of the Microchips interface
- Tagging a mob and viewing their Microchip
- Creating and deleting a group (with different names and colours)
- Tagging and removing a mob
- Moving a mob from one group to another
- Tagging multiple mobs and checking that list scroll works OK
- Modifying the name and colour of a group
- Changes are being persisted to the server on leaving

## Intermediate Functionalities
- Scaling of mob rendering in their relevant boxes is correct (when villagers are sleeping, for big mobs as well)
- Tooltips are being presented where relevant
- Status effects and various stats of the mob are reflected and updating correctly
- Items in the mob's inventory and inventory size are reflected correctly
- Actions in the Microchip are being executed correctly

## Advanced Functionalities
- When updating from one version to the next, Microchips are being loaded correctly on the new version
