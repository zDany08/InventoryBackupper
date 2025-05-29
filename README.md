## InventoryBackupper

InventoryBackupper is a Minecraft plugin that allows players to trigger event-specified backups of their inventory. These are the available triggers:
- **Join** _(When a player joins the server)_
- **Quit** _(When a player leaves the server)_
- **Death** _(When a player dies)_
- **Damage** _(When a player takes damage)_
- **Kill** _(When a player kills another player)_

You can toggle or specify a maximum amount per player of these triggers in the ```settings.yml``` configuration file.

### How to Use
The entire plugin is based on a single command:
- ```/inventorybackupper info``` _(Returns how many backups you have)_
- ```/inventorybackupper loadinv <player>``` _(Opens the backups GUI of the specified player)_

### Backups GUI
Once you performed the _loadinv_ command, you will encounter the following GUI:

![TriggersGUI](https://imgur.com/VEpf4xL.png)

Here you can filters backups based on the selected trigger. Then another GUI opens:

![BackupsGUI](https://imgur.com/kZdeM4K.png)

That is player's backup list of the trigger you selected, select on of them and see its contents (you can also take them if you need and nothing will change).
