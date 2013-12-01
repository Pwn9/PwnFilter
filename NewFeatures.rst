Proposed New Features for PwnFilter
===================================

Match Group References *DEFERRED*
----------------------------------
When doing an action, there is currently no way to get the actual string that
matched.  This will allow a match group to be referenced in actions.  Eg::

  match (derp)ity(dah)
  then replace $1 $2

Would match 'derpitydah' and output 'derp dah'




Event Enhancements
++++++++++++++++++

Book Support
------------
Complete support for filtering of books.

Proper Anvil Support
--------------------
This is more of a bug-fix than enhancement, but we required Bukkit to update
support for Anvils to properly filter item names.

Player Configuration
++++++++++++++++++++

Disable Filter
--------------
A player with the pwnfilter.toggleraw permission will be able to *receive* raw
messages.  This will effectively bypass any "then replace", "then rewrite"
rules in chat messages they receive. (Will not apply to signs, anvil, books, etc.)

Must take into consideration that some rules may not be 'bypassable'.


Possible enhancements for 3.3
++++++++++++++++++++++++++++++++++++

Web-based configuration. (Drag and drop with modals for config)

/pftest command to test a string against a rule.

Name matcher.  Basically, a special "match" rule that would detect the name
of an online player. eg: matchplayer

Name filter: apply rules to player names in onPlayerJoin event.  If player
has offensive name, then take action.

Auto-updater

Revamp config file to be more organized / hierarchical.
