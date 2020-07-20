# Trading Commands
A shop and auction system for [NukkitX][nukkitx] server software for Minecraft Bedrock Edition, based on commands.

**Note!** This plugin is no longer supported. I advise you to migrate to [Trading Interface][interface].

## Setting up
-   Make sure that [EconomyAPI][economy] is installed;
-   Place the plugin's `.jar` file into the `plugins` folder on your server;
-   Set up the configuration file (see "Configuration file");
-   Set up the lists of buyable and sellable items using admin commands. The lists update automatically after editing, so you don't need to reboot your server.
 
## User commands
-   `/buy <id> <amount>` — buying a specified amount of items with a specified ID (string or number).
-   `/buy <id>` — buying 1 item with a specified ID.
-   `/sell <id> <amount>` — selling a specified amount of items with a specified ID.
-   `/sell <id>` — selling 1 item with a specified ID.
-   `/sell` — selling items from your hands.
-   `/buylist (/blist)` — list of buyable items.
-   `/selllist (/slist)` — list of sellable items.
-   `/auc <id> <amount> <start price>` — put a specified amount of items with a specified ID with a specified start price on an auction.
-   `/auc <id> <start price>` — put 1 item with a specified ID with a specified start price on an auction.
-   `/auc <start price>` — place items from your hands on an auction.
-   `/bet <your bid>` — place a bet on an ongoing auction.
-   `/id` — shows the ID of the item in your hand.
-   `/shophelp` — list of shop commands.
  
## Admin commands
Admin commands are available to players with `trading.editshoplist` permission. Operators have this permission by default.
-   `/addbuyitem <id> <price> (/abi <id> <price>)` — add an item to the list of buyable items.
-   `/addsellitem <id> <price> (/asi <id> <price>)` — add an item to the list of sellable items.
-   `/delbuyitem <id> (/dbi <id>)` — delete an item from the list of buyable items.
-   `/delsellitem <id> (/dsi <id>)` — delete an item from the list of sellable items.
-   `/adddiscount <id> <percent>` — add a discount for an item.
-   `/deldiscount <id>` — delete a discount.

## Configuration file
All parameters in the [configuration file](src/main/resources/config.yml) are commented.
  
## Other functions
The plugin supports custom translations. In order to use your own language, set `general.languauge` option in the config file to `<your_lang>`. The plugin will create `<your_lang>.lng` file with English strings. You have to translate the text in this file to your language.

## Building from sources
`mvn clean package`

## Links
-   [Trading Commands on nukkitx.com](https://nukkitx.com/resources/trading-commands.13/)
-   [Trading Commands on nukkit.ru](http://forums.voxelwind.com/resources/trading-commands.120/)

[nukkitx]: https://github.com/NukkitX/Nukkit
[economy]: https://github.com/EconomyS/EconomyAPI
[interface]: https://github.com/Leonidius20/TradingInterface