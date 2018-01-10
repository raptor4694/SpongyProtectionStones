# SpongyProtectionStones

![](https://img.shields.io/github/downloads/rodel77/SpongyProtectionStones/total.svg)

SpongyProtectionStones its a basic port of any PreciousStones plugin, just with the basic protection stone type

Disclaimer: This plugin its still in beta! Its very hard to me add prevention of any way to grief, please consider support this project reporting issues and making pull requests! 

## Permissions
+ Flags: `ps.flag.{flag-name}`
+ Commands: `ps.command.{command-name}`
+ Build flag bypass: `ps.bypass.build`

## Developers

### How to add new flags
+ Register the flag `SPSApi.registerFlag(String name, Object defaultValue)` (Important: You can only use the type String, Boolean and Integer for now, more types will be used in the future)
+ In the event you want to listen...
	+ First check if there is any protection `SPSApi.getProtection(Location<World> location)` this method will return a optional
	+ Now get the value of your flag in the protection, `Protection.getFlag(String name, Class<T> type)` this also will return a optional
	+ And also you can check if the player its the owner or a member `Protection.hasPermission(UUID uuid)`


### Notes about testing
+ In case you need to modify stuff in DB, you should do `/ps sreload` this will reload all protections saved by chunk location in memory 

### TODO LIST

There are some @NeedsCleanup in the code!

Ordered by Priority

##### TODO:
+ /ps hide/unhide
+ API
+ "Probably" List of all protections in the server, make a custom loading pagination that takes information while scrolling

#### PROGRESS:
+ protection break
+ set owner command
+ flags (Need to implement more complex flags)

#### NEED TESTING:

#### BACKLOG:
+ gretting message
+ better ps info
+ fix place protection message
+ visualize on place
+ owner (uuid=name) [owner & owner_name]
+ update name on player join
+ members
