![PwnFilter Chat Filtering Plugin through RegEx](http://dev.bukkit.org/media/images/57/739/pwnfilter.png)

[![Main Page](http://mc.pwn9.com/dbo_img/v2/main_on.png)](http://dev.bukkit.org/bukkit-plugins/pwnfilter/) [![Installation](http://mc.pwn9.com/dbo_img/v2/install_off.png)](http://dev.bukkit.org/bukkit-plugins/pwnfilter/pages/installation/) [![Configuration](http://mc.pwn9.com/dbo_img/v2/config_off.png)](http://dev.bukkit.org/bukkit-plugins/pwnfilter/pages/configuration/) [![Permissions](http://mc.pwn9.com/dbo_img/v2/perms_off.png)](http://dev.bukkit.org/bukkit-plugins/pwnfilter/pages/pwn-filter-permissions/) [![Commands](http://mc.pwn9.com/dbo_img/v2/cmds_off.png)](http://dev.bukkit.org/bukkit-plugins/pwnfilter/pages/commands/) [![Post-Match Operators](http://mc.pwn9.com/dbo_img/v2/actions_off.png)](http://dev.bukkit.org/bukkit-plugins/pwnfilter/pages/post-match-operators/) [![Regex Samples](http://mc.pwn9.com/dbo_img/v2/samples_off.png)](http://dev.bukkit.org/bukkit-plugins/pwnfilter/pages/sample-regex-rules/) [![GitHub](http://mc.pwn9.com/dbo_img/v2/github_off.png)](https://github.com/Pwn9/PwnFilter)

* * *

[![IntelliJ](http://www.jetbrains.com/idea/docs/logo_intellij_idea.png)](http://www.jetbrains.com/idea/)
 Thanks to JetBrains, for their generous OpenSource Project license for IntelliJ IDEA

* * *

<<color 00f>> <<size 16px>> Update April 23, 2016

PwnFilter 3.9.1 has been released, with a couple of important bugfixes. Please update as soon as possible.

If you need quick response, please join our IRC channel, #pwn9 on irc.esper.net.

<</size>> <</color>>

* * *

## About PwnFilter

PwnFilter is way more than just your average word-blocking plugin, PwnFilter uses the power of \_\_Reg\_\_ular-\_\_Ex\_\_pressions ("_RegEx_") to give you a highly effective and highly configurable plugin to filter anything you want from chat! Matching words can be rewritten, logged, blocked, and managed, depending on the rules you define. Use PwnFilter for:

- Extremely effective **chat/command filtering** : Filter anything you want with RegEx!
- Extremely effective **curse/swear word filtering** , warn users who use bad language, turn their profanities into harmless language, or just deny their message entirely!
- Assign appropriate **punishment levels** based on severity.
- Anti-Advertisement: **IP/URL filtering**
- Make **fun chat replacements** for your server's inside-jokes.
- Single line **spam filtering** : Remove repetitive characters like _hiiiiiiiiiiiii_ to _hi_.
- **CAPS Blocking** capability
- **Typo correction** : Replace common and annoying typos like _"teh" to "the"_ or _"u" or "you"_
- **Customizable warning messages**
- **Command aliases** : Automatically recognize a _!command_ and change to _/longer command_, Stop yourself from accidentally sending those embarrassing _.commands_ to chat for everyone to see.
- A few **built-in punishments** like burn, kill, fine (charge money!), warn, kick, and ban. Or simply **utilize the "then console" action** to make your server console execute almost any command from any other plugin as if you typed it yourself in the console window, especially if you use a plugin that adds a plethora of punishments, like [PunishMental](http://dev.bukkit.org/bukkit-plugins/punishmental/), or use it to make PwnFilter send the appropriate commands to your favorite ban-management system to let it handle what to do!

Think of this plugin as a platform for the power of RegEx, the matching rules are 100% entirely up to you, and there are a hundred ways to do something. The possibilities are endless, the power is as dynamic as you want it to be depending on how much effort you put into having the best RegEx rules.

_PwnFilter is a revival and complete rewrite of the RegexFilter plugin by FloydATC. Thanks to DreamPhreak for helping with this Bukkit page, to Sage905 for taking over the latest branch of development making PwnFilter even better than ever, and to EpicAtrain for developing comprehensive regex rules for server owners to use._

* * *

### Features:

- ++Filter Chat!++ (of course)
- ++Filter Item Names!++ (Anvil)
- ++Filter Signs!++
- ++Filter Commands!++
- ++Filter Console!++
- ++Filter Books!++ (Coming in next version!)
- ++Filter Nametags!++
- Built-in anti-spam feature can be enabled in config.
- Commands for OP or by permission node.
- Supports color message replacement.
- Globally clear all player's chat windows with " **/pfcls**".
- Global mute with " **/pfmute**" - stops all server chat and commands for making admin announcements.
- Command typos beginning with certain characters can be stopped (like . and 7, accidentally instead of / and &amp;).
- Optionally recover those typos and execute the command as intended.
- Define your own macros or command aliases.
- Simple but powerful configuration with built-in debugging.
- Each regular expression is compiled only once => very fast.
- Able to reload all your PwnFilter rules & config files without needing to restart the server with " **/pfreload**".
- Optionally kick or warn players on rule matches AND/OR:
- Execute commands from console or other plugins.
- Use _randrep_ action to replace swears with a random multiple option.
- Use _lower_ action to replace text to all lowercase (great for those CAPS chatters).
- True command aliases, link faux commands like /wave to /me waves at you.
- Customizable 'permission denied' messages

* * *

### Issues & Conflicts

_Plugin Conflicts:_ If you notice that PwnFilter conflicts/interferes with another chat-related plugin, edit your [config.yml](http://dev.bukkit.org/server-mods/pwnfilter/pages/configuration/), go to the "ADVANCED" section at the bottom, unhide the priority you want to change (by removing the # in front of the line), and change the priority from the default setting "Lowest" to another setting like "Highest". This can sometimes resolve conflicts, otherwise ask on our forum or create a ticket.

_Permission Bypass_: The permission node **pwnfilter.bypass** is automatically granted to OP and players with \* permission nodes. If you do not want these players bypassing the filter at all, you must negate this permission node for them in your Permissions Plugin's files.

* * *

### Plugin Metrics

This plugin utilizes Hidendra's plugin metrics system, which means that some information is collected and sent to mcstats.org. If you wish to disable this feature, you can do so by opting out, which you can do in the PluginMetrics' config file under _/YourServer_/plugins/PluginMetrics/

* * *

## Links & Info

[![McStats](http://api.mcstats.org/signature/PwnFilter.png?90%)](http://mcstats.org/plugin/PwnFilter)

- [PwnPlantGrowth](http://dev.bukkit.org/server-mods/pwnplantgrowth/) - Customize, modify, block all plant growth with varying configurations, including biome based and light level based farming.

- [PwnChickenLay](http://dev.bukkit.org/server-mods/pwnchickenlay/) - Configure or block how fast chickens can lay eggs, or replace eggs with other items like diamonds, bricks, anything you can think of.

- [ResPwn](http://dev.bukkit.org/bukkit-plugins/respwn/) - Configure useful and fun player respawn options like temporary forcefields, armor and weapons.

- [PwnPvpBalance](http://dev.bukkit.org/bukkit-plugins/pwnpvpbalance/) - Balance the tide of uneven game mechanics with options to help players with poor PvP skills be more competitive, and give good players a better challenge.

- [PwnFilter Regex Forum](http://dev.bukkit.org/server-mods/pwnfilter/forum/regex-discussion/) - Have questions about using RegEx or want to show your rules.txt off? Visit our RegEx forum here on DBO!

- [Pwn9.com](http://www.pwn9.com) - Visit the Pwn9 Gaming Community, the place we call home. Check out our Minecraft servers, other game servers and fun gaming community.

- IRC Channel: Join **#pwn9** on irc.esper.net - feel free to ask PwnFilter or RegEx questions on the channel or just come hang out with us.

- Development builds of this project can be acquired at the provided continuous integration server. These builds have not been approved by the BukkitDev staff. Use them at your own risk. [ci.sagely.ca](http://ci.sagely.ca)

* * *

### Donate

All of our "Pwn-" plugins are completely free of charge. We work hard to bring you the best, powerful, and up-to-date plugins. If we have helped you in any way, please consider a donation of any amount (Using the "Donate" button on the top-right of this page, or clicking [HERE](https://www.paypal.com/cgi-bin/webscr?return=http%3A%2F%2Fdev.bukkit.org%2Fbukkit-plugins%2Fpwnfilter%2F&amp;cn=Add+special+instructions+to+the+addon+author%28s%29&amp;business=admin%40pwn9.com&amp;bn=PP-DonationsBF%3Abtn_donateCC_LG.gif%3ANonHosted&amp;cancel_return=http%3A%2F%2Fdev.bukkit.org%2Fbukkit-plugins%2Fpwnfilter%2F&amp;lc=US&amp;item_name=PwnFilter+%28from+Bukkit.org%29&amp;cmd=_donations&amp;rm=1&amp;no_shipping=1&amp;currency_code=USD)). All donations are GREATLY appreciated for all of the time that goes into this plugin and support.

* * *

**EpicATrain's version 10.0 filter has been temporarily removed, due to a few issues.** The classic language filter is still available here: [http://pastebin.com/u/EpicATrain](http://pastebin.com/u/EpicATrain)

| ![Counter](http://hitwebcounter.com/counter/counter.php?page=5124746&amp;style=0007&amp;nbdigits=5&amp;type=page&amp;initCount=24703) |