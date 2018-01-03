# SpongyProtectionStones

SpongyProtectionStones its a basic port of any PreciousStones plugin, just with the basic protection stone type

## Developers

### How to add new flags
+ Register the flag `SPSApi.registerFlag(String name, Object defaultValue)`
+ In the event you want to listen...
	+ First check if there is any protection `SPSApi.getProtection(Location<World> location)` this method will return a optional
	+ Now get the value of your flag in the protection, `Protection.getFlag(String name, Class<T> type)` this also will return a optional
	+ And also you can check if the player its the owner or a member `Protection.hasPermission(UUID uuid)`


### Notes about testing
+ In case you need to modify stuff in DB, you should do `/ps sreload` this will reload all protections saved by chunk location in memory 

### TODO LIST

Ordered by Priority

##### TODO:
+ gretting message
+ set owner command
+ protection break
+ API
+ "Probably" List of all protections in the server, make a custom loading pagination that takes information while scrolling

#### PROGRESS:
+ flags

#### NEED TESTING:

#### BACKLOG:
+ better ps info
+ fix place protection message
+ visualize on place
+ owner (uuid=name) [owner & owner_name]
+ update name on player join
+ members