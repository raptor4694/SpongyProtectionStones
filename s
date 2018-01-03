[1mdiff --git a/src/main/java/mx/com/rodel/sps/command/CommandAdd.java b/src/main/java/mx/com/rodel/sps/command/CommandAdd.java[m
[1mindex 6765708..2d9115e 100644[m
[1m--- a/src/main/java/mx/com/rodel/sps/command/CommandAdd.java[m
[1m+++ b/src/main/java/mx/com/rodel/sps/command/CommandAdd.java[m
[36m@@ -61,6 +61,6 @@[m [mpublic class CommandAdd implements ICommand{[m
 [m
 	@Override[m
 	public String getDescription() {[m
[31m-		return "Add a member into the protection";[m
[32m+[m		[32mreturn SpongyPS.getInstance().getLangManager().localize("commands-add-description");[m
 	}[m
 }[m
[1mdiff --git a/src/main/java/mx/com/rodel/sps/command/CommandInfo.java b/src/main/java/mx/com/rodel/sps/command/CommandInfo.java[m
[1mindex 10438ab..7726f1b 100644[m
[1m--- a/src/main/java/mx/com/rodel/sps/command/CommandInfo.java[m
[1m+++ b/src/main/java/mx/com/rodel/sps/command/CommandInfo.java[m
[36m@@ -14,6 +14,7 @@[m [mimport com.google.common.base.Joiner;[m
 import mx.com.rodel.sps.SpongyPS;[m
 import mx.com.rodel.sps.config.LocaleFormat;[m
 import mx.com.rodel.sps.protection.Protection;[m
[32m+[m[32mimport mx.com.rodel.sps.utils.Helper;[m
 [m
 public class CommandInfo implements ICommand{[m
 [m
[36m@@ -43,6 +44,8 @@[m [mpublic class CommandInfo implements ICommand{[m
 			}else{[m
 				source.sendMessage(SpongyPS.getInstance().getLangManager().translate("info-nostone", false));[m
 			}[m
[32m+[m		[32m}else{[m
[32m+[m			[32msource.sendMessage(Helper.chatColor("&cThis comand is only for players"));[m
 		}[m
 		return true;[m
 	}[m
[1mdiff --git a/src/main/java/mx/com/rodel/sps/command/CommandRemove.java b/src/main/java/mx/com/rodel/sps/command/CommandRemove.java[m
[1mindex 5b17dcd..4d2f233 100644[m
[1m--- a/src/main/java/mx/com/rodel/sps/command/CommandRemove.java[m
[1m+++ b/src/main/java/mx/com/rodel/sps/command/CommandRemove.java[m
[36m@@ -65,6 +65,6 @@[m [mpublic class CommandRemove implements ICommand{[m
 [m
 	@Override[m
 	public String getDescription() {[m
[31m-		return "Removes a player from the protection";[m
[32m+[m		[32mreturn SpongyPS.getInstance().getLangManager().localize("commands-remove-description");[m
 	}[m
 }[m
[1mdiff --git a/src/main/resources/assets/spongyps/lang.conf b/src/main/resources/assets/spongyps/lang.conf[m
[1mindex 50dce74..56ecde1 100644[m
[1m--- a/src/main/resources/assets/spongyps/lang.conf[m
[1m+++ b/src/main/resources/assets/spongyps/lang.conf[m
[36m@@ -16,6 +16,9 @@[m [minfo-flags="&7Flags: &6{flags}"[m
 visualize="Visualizing borders..."[m
 commands-info-description="Get info about the protection in your current location"[m
 commands-visualize-description="Visualize the borders of your protection"[m
[32m+[m[32mcommands-add-description="Grant permission to players into the protection"[m
[32m+[m[32mcommands-remove-description="Revoke permission to players in the protection"[m
[32m+[m[32mcommands-limits-description="Check your available protections"[m
 member-already="&cThis player is already a member in this protection"[m
 member-missing="&cThis player isn't a member in this protection"[m
 member-add="&6{member}&a added to this protection!"[m
