# Evaluate
[![Spigot 1.12.2](https://img.shields.io/badge/Spigot-1.12.2-brightgreen.svg)](https://www.spigotmc.org/wiki/spigot/)
[![GitHub release](https://img.shields.io/github/release/kubotan/Evaluate.svg)](https://github.com/kubotan/Evaluate/releases)
[![Build Status]( https://travis-ci.org/kubotan/Evaluate.svg?branch=master)](https://travis-ci.org/kubotan/Evaluate)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/kubotan/Evaluate/issues)
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://github.com/kubotan/Evaluate/blob/master/LICENSE)

Evaluate plugin.

# Installation method
Please put jar in the plugins folder.   
Please put it only in the plugin folder of spigot or bukkit.   

# Useage
```
/bungeen reload - Reload the configuration.
/bungeen setname [server name] [value] - Set the display value of the first line of the signboard.
/bungeen setcomment [server name] [value] - Set the display value of the last line of the signboard.
/bungeen setprotocoltype [server name] [default|legacy] - default:Client version 1.7 or later legecy:Other than default
/bungeen remove [server name] - Remove the setting of the specified server name.
/bungeen addmember [server name] [playername] - Everyone can pass before this command is executed.If more than one player is added, only those who are added can pass.
/bungeen delmember [server name] [playername] - Delete players permitted to pass through.Everyone can pass if everyone is deleted.
/bungeen delallmember [server name] - Delete all players permitted to pass through.Everyone can pass if everyone is deleted.
```

# Permission
Only the op can perform setting work.

# Disclaimer
Do not assume any responsibility by use. Please use it at your own risk.
