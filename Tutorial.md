# Getting started with PwnFilter 3.2

## Introduction

PwnFilter is an extremely powerful tool, but the user-friendliness has not
caught up with its power.  This tutorial will walk through some common
use-cases, to help people get started with the latest version of PwnFilter.

Some of these examples use EpicATrain's Language Filter, which is amazingly
comprehensive.  With some of the features of the PwnFilter 3.2 release,
Mr. ATrain has updated his filter to be easier to use and customize.  A lot
of work went into trying to catch as many variants of undesirable words,
while minimizing the false positives.

## How Does PwnFilter Work?

PwnFilter is a Bukkit (also works on Spigot) plugin that hooks into various
server events to check their text against a "rules" file.  The rules file
follows a basic format.  Let's go over it now.

The rules format is a plain text file with a series of blocks.  Each block
makes up a rule.  An example:

    match fuck
    then rewrite fudge
    then warn Watch your language please
    then log

Let's break this down.

    match fuck

The "match" keyword starts a new rule.  The text after "match" is the Regular
Expression. New to Regex? Check out this [introduction to Regular Expressions](http://www.regexbuddy.com/regex.html)
In the example above, we've used the literal text fuck, but this isn't really
very powerful, because players will often use "f u c k", or "fck", or
other such combinations to get around the filter.  If you want some
comprehensive filters to start from, have a look at EpicATrain's filters on the
[PwnFilter download page](http://dev.bukkit.org/bukkit-plugins/pwnfilter/)
The main point here is that if the filter detects "fuck" in the message, it
will execute the subsequent actions.

Next line:

    then replace fudge

Any line that starts with "then" is going to trigger an "action". See the
section titled [Actions](#actions) for a list of the available options. The
action in our example is going to rewrite "fuck" to "fudge".

    then warn Watch your language please

This line causes the server to send the message: "Watch your language please"
to the player.  "warn" doesn't actually execute /warn (if you had a plugin that
actually had a /warn command).  It just sends the player a message.

    then log

This line causes the match to be logged in the console log. By default,
PwnFilter does not log to the console, but it does log to the
plugins/PwnFilter/pwnfilter.log file. If you want matches to go to the
console log, you can change loglevel to "info" in the config.yml

So, to recap, if a player sends a chat message that says: "Hey, fuck you!",
the filter will:

  1. Change the message to read: "Hey, fudge you!"
  2. Send the player a message that says: "Watch your language please"
  3. Log this match to the console.

For every message that PwnFilter processes, it will go through all the rules
in the chain, check to see if the message matches the regex, and if so,
it will execute the actions for that match rule.

Tip: Since the rules are executed in a chain, in order, you can use rules
at the top of the chain to perform substitutions on the message, for later
rules to use.  Take this example:

    match \bu\b # matches a 'u' character by itself (See regex tutorial)
    then rewrite you

    match fuck you
    then kick Watch your language!
    then notify pwnfilter.admins %player% was kicked for saying 'fuck you'

If a player says: "fuck u", the filter will first rewrite "u" to "you",
so the message will read: "fuck you".  The next rule sees "fuck you",
and then kicks the player, and notifies logged-in admins.

We will introduce more complex concepts as we proceed through the tutorial.

## First Time Setup

 1. [Download plugin](http://dev.bukkit.org/bukkit-plugins/pwnfilter/)
 2. Install in server plugins folder (eg: /home/server/plugins/PwnFilter.jar
 3. Run server once to generate folders / configs
 4. Stop server.
 5. Edit configs and rules. (See next section)
 6. Start server.
 7. Profit

We presume that you can complete steps 1-4 without assistance. :)

## Step 5: Editing config.yml

The config.yml file contains all configuration, except for the rules.
A default configuration is created whenever PwnFilter is started for the
first time. Let's walk through the options...

### Default Messages

The first items in this file will be default messages for some of
the commands.  Eg: 'kickmsg' is a default for the "then kick" command.  If you
use "then kick You were kicked!", the player will get "You were kicked!".  If
the string "You were kicked!" were omitted from the action, the player would
instead get whatever is in the 'kickmsg' config setting. By default, this is
set to:

    kickmsg: '&4[&6PwnFilter&4] &4Kicked by PwnFilter!'

### Logging Options

By default, PwnFilter outputs basic startup information, eg: counts of how many
rules, any errors while parsing them, as well as entries for each time the filter
matches.  In the plugins/PwnFilter directory is a file called:
pwnfilter.log, which is created on startup.  This file will, by default contain
the history of matches, as well as other debugging info, if enabled.  Eg:

    [2013/09/30 23:54:49] [PwnFilter] |CHAT| MATCH <tremor77> fuck
    [2013/09/30 23:54:49] [PwnFilter] Warned tremor77: Swearing is not allowed.
    [2013/09/30 23:54:49] [PwnFilter] <tremor77> Original message cancelled.
    [2013/09/30 23:54:57] [PwnFilter] |CHAT| MATCH <tremor77> lol i swore and got blocked here
    [2013/09/30 23:54:57] [PwnFilter] |CHAT| SENT <tremor77> lol I swore and got blocked here

If you set *loglevel*: fine

## Editing rules text files



##<a name="actions"></a> Actions

As of 3.2.0, the valid actions are:

 * abort

     This action stops processing of more rules.  Use this if you want to ensure
     that rules that come after don't have a chance to run.

 * burn [message]

     A "fun" (for you, not the player!) rule that allows you to set a player
     on fire for 5 seconds.  If you specify a "message", it will be sent to
     the player.  Eg:

         burn You've been set on fire

     would cause the player to be set on fire for 5s, and send them a message
     that says: "You've been set on fire".

 * command [command]
 
     Executes [command] as the *player*.  For example: "then command me is phat"
     would cause the same result as if they player had done: "/me is phat".

 * cmdchain [command]|[command]|[command]...
 
     Same as above, but allows multiple commands (separated by | ) to be
     executed.  Note: using multiple "then command" lines is preferred.  The
     cmdchain action may be deprecated in the future.

 * console [command]
 
     Execute [command] as the *console*.  This is useful to execute commands
     like: "tempban %player% 15m Excessive foul language".  Note that this
     example presumes you have a plugin like Ban Management to actually handle
     the "tempban" command.

 * conchain [command]|[command]|[command]...
 
     Similar to cmdchain, except allows multiple commands to be executed as
     the *console*.  Also likely to be deprecated in the future.  Please use
     multiple "then console [command]" actions, instead.

 * deny
 
     Cancels the event that we're currently processing.  For example, if this
     is a chat event, cancel the message.

 * fine [amount] [message]

     If Vault is installed, this action will cause [amount] of money to be
     deducted from the players balance.  Also, it will send [message] to the
     player.

 * kick [message]

     This will do the same thing as /kick [player] [message]

 * kill [message]

     This will kill the player, and set the death message to [message]

 * log

     This will cause this rule match to be logged in the console.

 * lower

     This will cause the matched text to be converted to lowercase.

 * notify [permission] [message]

     This will send a message to all logged in players with [permission]
     Eg: notify pwnfilter.admins %player% just triggered %ruleid%.

 * rewrite [string]

     This replaces the matched text with [string]

 * randrep <string_a>|<string_b>|<string_c>

     Specify a number of strings separated by | symbols.  The filter will
     randomly replace the matched text with one of the strings.

 * replace [string]

     This is identical to rewrite, except it will also *decolor the whole
     string*.

 * respond [string]

     Send the player a message with [string]

 * warn [string]

     Same as respond.  This is a "legacy" command.  Its behaviour may change
     in the future.


PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
