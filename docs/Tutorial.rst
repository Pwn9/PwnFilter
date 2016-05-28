Getting started with PwnFilter 3.2
==================================

.. contents::

Introduction
------------

PwnFilter is an extremely powerful tool, but the user-friendliness has
not caught up with its power. This tutorial will walk through some
common use-cases, to help people get started with the latest version of
PwnFilter.

Some of these examples use EpicATrain's Language Filter, which is
amazingly comprehensive. With some of the features of the PwnFilter 3.2
release, Mr. ATrain has updated his filter to be easier to use and
customize. A lot of work went into trying to catch as many variants of
undesirable words, while minimizing the false positives.

How Does PwnFilter Work?
------------------------

PwnFilter is a Bukkit (also works on Spigot) plugin that hooks into
various server events to check their text against a "rules" file. The
rules file follows a basic format. Let's go over it now.

The rules format is a plain text file with a series of blocks. Each
block makes up a rule. An example::

    match fuck
    then rewrite fudge
    then warn Watch your language please
    then log

Let's break this down.

::

    match fuck

The "match" keyword starts a new rule. The text after "match" is the
Regular Expression. New to Regex? Check out this `introduction to
Regular Expressions <http://www.regexbuddy.com/regex.html>`__ In the
example above, we've used the literal text fuck, but this isn't really
very powerful, because players will often use "f u c k", or "fck", or
other such combinations to get around the filter. If you want some
comprehensive filters to start from, have a look at EpicATrain's filters
on the `PwnFilter download
page <http://dev.bukkit.org/bukkit-plugins/pwnfilter/>`__ The main point
here is that if the filter detects "fuck" in the message, it will
execute the subsequent actions.

Next line:

::

    then replace fudge

Any line that starts with "then" is going to trigger an "action". See
the section titled `Actions <#actions>`__ for a list of the available
options. The action in our example is going to rewrite "fuck" to
"fudge".

::

    then warn Watch your language please

This line causes the server to send the message: "Watch your language
please" to the player. "warn" doesn't actually execute /warn (if you had
a plugin that actually had a /warn command). It just sends the player a
message.

::

    then log

This line causes the match to be logged in the console log. By default,
PwnFilter does not log to the console, but it does log to the
plugins/PwnFilter/pwnfilter.log file. If you want matches to go to the
console log, you can change loglevel to "info" in the config.yml

So, to recap, if a player sends a chat message that says: "Hey, fuck
you!", the filter will:

1. Change the message to read: "Hey, fudge you!"
2. Send the player a message that says: "Watch your language please"
3. Log this match to the console.

For every message that PwnFilter processes, it will go through all the
rules in the chain, check to see if the message matches the regex, and
if so, it will execute the actions for that match rule.

Tip: Since the rules are executed in a chain, in order, you can use
rules at the top of the chain to perform substitutions on the message,
for later rules to use. Take this example::

    match \bu\b # matches a 'u' character by itself (See regex tutorial)
    then rewrite you

    match fuck you
    then kick Watch your language!
    then notify pwnfilter.admins %player% was kicked for saying 'fuck you'

If a player says: "fuck u", the filter will first rewrite "u" to "you",
so the message will read: "fuck you". The next rule sees "fuck you", and
then kicks the player, and notifies logged-in admins.

We will introduce more complex concepts as we proceed through the
tutorial.

First Time Setup
----------------

1. `Download plugin <http://dev.bukkit.org/bukkit-plugins/pwnfilter/>`__
2. Install in server plugins folder (eg:
   /home/server/plugins/PwnFilter.jar
3. Run server once to generate folders / configs
4. Stop server.
5. Edit configs and rules. (See next section)
6. Start server.
7. Profit

We presume that you can complete steps 1-4 without assistance. :)

Step 5: Editing config.yml
--------------------------

The config.yml file contains all configuration, except for the rules. A
default configuration is created whenever PwnFilter is started for the
first time. Let's walk through the options...

Default Messages
~~~~~~~~~~~~~~~~

The first items in this file will be default messages for some of the
commands. Eg: 'kickmsg' is a default for the "then kick" command. If you
use "then kick You were kicked!", the player will get "You were
kicked!". If the string "You were kicked!" were omitted from the action,
the player would instead get whatever is in the 'kickmsg' config
setting. By default, this is set to::

    kickmsg: '&4[&6PwnFilter&4] &4Kicked by PwnFilter!'

Logging Options
~~~~~~~~~~~~~~~

By default, PwnFilter outputs basic startup information, eg: counts of
how many rules, any errors while parsing them, as well as entries for
each time the filter matches. Also, in the plugins/PwnFilter directory
is a file called: pwnfilter.log, which is created if you have:
*logfile*: true. This file will, by default contain the history of
matches, as well as other debugging info, if enabled. Eg::

    [2013/09/30 23:54:49] [PwnFilter] |CHAT| MATCH <tremor77> fuck
    [2013/09/30 23:54:49] [PwnFilter] Warned tremor77: Swearing is not allowed.
    [2013/09/30 23:54:49] [PwnFilter] <tremor77> Original message cancelled.
    [2013/09/30 23:54:57] [PwnFilter] |CHAT| MATCH <tremor77> lol i swore and got blocked here
    [2013/09/30 23:54:57] [PwnFilter] |CHAT| SENT <tremor77> lol I swore and got blocked here

If you set *loglevel*: fine, the MATCH/SENT messages will only be logged
in the pwnfilter.log, if you set *loglevel*: info, they will be logged
in the console as well.

The "debug" option can be very useful when troubleshooting rules. By
default, debug is set to "off". The options are:

-  low : minor logging, not much here, really.

-  medium : Detailed information about regex matches, and filter
   internals.

-  high : Crazy amount of detail. At least one log entry for every rule
   in the config. NOT recommended for production use!

Filter configuration
~~~~~~~~~~~~~~~~~~~~

::

    ruledirectory: /path/to/rules

By default, all rules are stored in the server's plugins/PwnFilter/rules
directory. You may override that with the above option, to point to any
location on your filesystem. Also, all "include" directives will be
relative to this path.

Chat Filter
^^^^^^^^^^^

By default, the chat filter is always enabled. When PwnFilter starts up,
it looks for a file called "chat.txt" in the Rule Directory. It then
parses this file, and any included files.

If you set:

::

    spamfilter: true

PwnFilter will prevent a player from sending the exact same message
twice. NOTE: This spam filter can cause problems with plugins like
BoosCooldowns, since Boos cancels the first attempt when using warmups,
and then re-issues it after the warmup, thus looking like spam. We
recommend you do not use the built-in spam filter at this time.

Command Filter
^^^^^^^^^^^^^^

PwnFilter can do more than just filter chat messages! It can also filter
the /me command, as well as create command aliases! Set:

::

    commandfilter: true

to enable the command filter. Also, there is an option called:

::

    commandspamfilter: true/false

which works just like the spamfilter for chat. (Except it's for
commands!) :) PwnFilter will look in the rules/command.txt file for
rules that should be applied to commands. If you want to have the same
rules for commands as for chat, you can just point these both to the
same file, eg:

In command.txt::

    include rules.txt

In chat.txt::

    include rules.txt

In rules.txt::

    match foo
    then kick

    match bar
    then warn

    ... etc

In the next section, we'll show some examples of what you can do with
the command filter. For now though, there are two other options that you
need to know about::

    cmdlist:
    - me
    - nick

    cmdblist: []

These two options give a "whitelist" or a "blacklist" to the command
handler. Basically, if you have a whitelist, then only those commands
will be handled. Any other commands will be ignored by PwnFilter. The
cmdblist is the opposite. If you have any commands listed here, they
will be ignored by PwnFilter. So, for example, if you only wanted
PwnFilter to handle the /me and /nick commands, you would use the
example above. On the other hand, if you wanted PwnFilter to handle all
commands *except* op and deop, you would do::

    cmdlist: []
    cmdblist:
    - op
    - deop

In this case, the filter would be applied to all commands, except op and
deop.

Sign Filter
^^^^^^^^^^^

PwnFilter can also check signs that players create. To enable, use::

    signfilter: true

Filtering signs is tricky business, since the text can span all 4 lines
of the sign. In order to try to catch as much as possible, PwnFilter
treats each of the 4 lines as a single line.

Miscellaneous Options
~~~~~~~~~~~~~~~~~~~~~

::

    decolor: true / false

This option will cause all messages to be stripped of color codes (eg:
&5)

Editing rules text files
------------------------

This is where the real magic happens in PwnFilter. Creating Regex Rules can
be complex and challenging.  Fortunately, EpicATrain has created some really
comprehensive rules files that you can use in your server.


Reference
---------

Actions
=======

As of 3.2.0, the valid actions are:

-  abort

   This action stops processing of more rules. Use this if you want to
   ensure that rules that come after don't have a chance to run.

-  burn [message]

   A "fun" (for you, not the player!) rule that allows you to set a
   player on fire for 5 seconds. If you specify a "message", it will be
   sent to the player. Eg::

        burn You've been set on fire

   would cause the player to be set on fire for 5s, and send them a
   message that says: "You've been set on fire".

-  command [command]

   Executes [command] as the *player*. For example: "then command me is
   phat" would cause the same result as if they player had done: "/me is
   phat".

-  cmdchain [command] | [command] | [command]...

   Same as above, but allows multiple commands (separated by \| ) to be
   executed. Note: using multiple "then command" lines is preferred. The
   cmdchain action may be deprecated in the future.

-  console [command]

   Execute [command] as the *console*. This is useful to execute
   commands like: "tempban %player% 15m Excessive foul language". Note
   that this example presumes you have a plugin like Ban Management to
   actually handle the "tempban" command.

-  conchain [command] | [command] | [command]...

   Similar to cmdchain, except allows multiple commands to be executed
   as the *console*. Also likely to be deprecated in the future. Please
   use multiple "then console [command]" actions, instead.

-  deny

   Cancels the event that we're currently processing. For example, if
   this is a chat event, cancel the message.

-  fine [amount][message]

   If Vault is installed, this action will cause [amount] of money to be
   deducted from the players balance. Also, it will send [message] to
   the player.

-  kick [message]

   This will do the same thing as /kick [player][message]

-  kill [message]

   This will kill the player, and set the death message to [message]

-  log

   This will cause this rule match to be logged in the console.

-  lower

   This will cause the matched text to be converted to lowercase.

-  notify ["console" | permission] [message]

   This will send a message to all logged in players with [permission]
   Eg: notify pwnfilter.admins %player% just triggered %ruleid%.

   If [permission] is "console", the notification will be sent as a message
   to the console.

-  rewrite [string]

   This replaces the matched text with [string]

-  randrep \|\|

   Specify a number of strings separated by \| symbols. The filter will
   randomly replace the matched text with one of the strings.

-  replace [string]

   This is identical to rewrite, except it will also *decolor the whole
   string*.

-  respond [string]

   Send the player a message with [string]

-  warn [string]

   Same as respond. This is a "legacy" command. Its behaviour may change
   in the future.

Variables for Actions
=====================

The following are available to be used in "then command <string>":

- %world%
  The World the player is currently in
- %player%
  The name of the Player.
- %string%
  The current string (may have been modified by other rules).
- %rawstring%
  The original string, before any rule processing.
- %event%
  The name of the filter client that we're processing. (eg: CHAT, COMMAND, etc.)
- %points%
  The current points balance of the Player
- %ruleid%
  The <id> of the currently matched rule.
- %ruledescr%
  The <description> of the currently matched rule

PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft
servers. Copyright (c) 2013 Pwn9.com. Tremor77 admin@pwn9.com & Sage905
patrick@toal.ca
