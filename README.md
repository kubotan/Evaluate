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
/bad (player name) (reason) - 指定されたプレイヤーに悪い評価をします。理由(reason)は必須です。
/good (player name) - 指定されたプレイヤーに良い評価をします。理由(reason)は必須です。
/status - 自分のステータスを表示します(プレイヤーが指定ユーザーの場合のみ他のプレイヤー名を指定してステータスを表示できます。)
/update (yyyy-MM-dd HH:mm:ss) - badやgoodは短時間で連投できないようになっています。この指定された日時を最終実行日時をセットします（デバッグ用）
/upvalue (player name) (9.999|-9.999) §7- 指定された値で評価をセットします()
```

# Permission
Only the op can perform setting work.

# Disclaimer
Do not assume any responsibility by use. Please use it at your own risk.
