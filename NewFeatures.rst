Proposed New Features for PwnFilter 3.2.0
=========================================

BACKWARDS INCOMPATIBLE CHANGES!!!!!
+++++++++++++++++++++++++++++++++++

***NOTE****

Any occurances of:
&world ,&player, &string, &rawstring, &event, &ruleid, &ruledescr

will need to be replaced with:
%world% ,%player%, %string%, %rawstring%, %event%, %ruleid%, %ruledescr%

ALSO...

A subtle, but important change has been made to the rules file format.  If a blank line is detected,
this will cause the parser to finish a rule.  This used to be valid::

  match blah
  then warn Hey!

  then deny

This is no longer valid, though, and the "then deny" will not ba attached to the rule.

Further, at least one blank line must separate all statement groups.  eg::

  VALID:
    match blah
    then action

    match foo
    then action

  NOT VALID:
    match blah
    then action
    match foo
    then action

Comments do not count as blank line.  eg::

  VALID:
    match blah
    #Now do an action.
    then action

  NOT VALID:
    match blah
    then action
    #Now another rule
    match foo
    then blah

Got it? :)


Rules file format / features
+++++++++++++++++++++++++++++


Rules.txt format * COMPLETE *
------------------------------

New folder structure::

    plugins/PwnFilter
             \->rules
                |-> tamewords.txt
                |-> badwords.txt
                |-> reallybadwords.txt
                |-> sign.txt
                |-> chat.txt
                |-> item.txt
                |-> command.txt
                \-> console.txt

Each of the sign, chat, etc. are rulesets for specific event
handlers.  They can import from any of the files in the rules directory
(or, in fact, any file that can be referred to relative to where it is, eg: ../rules.txt)
and/or they can just have rules directly entered.  Eg:

chat.txt::

    include tamewords.txt
    include badwords.txt

    match derp
    then ...

and so on...

Named Rules * COMPLETE *
------------------------
Adding a name / ID to a rule.  eg::

  match <matchstring>
  rule <id> [Optional description]
  ... etc...

Also, you can use &ruleid and &ruledescr in "then command" and "then console" messages.  Eg::

  match badword
  rule BW1 Badword Rule
  then console ban &player 1d (&ruleid) &ruledescr

would cause the following command to be run::

  /ban PlayerName 1d (BW1) Badword Rule


Shortcuts * COMPLETE *
----------------------

Writing regex's can be tedious.  Shortcuts allow the use of configurable
"variables" that can are replaced in the regex.  Eg::

    match ((http)*(\w|\W|\d|_)*(www)*(\w|\W|\d|_)*[a-zA-Z0-9\.\-\*_\^\+\~\`\=\,\&*]{3,}(\W|\d|_|dot|\(dot\))+(com\b|org\b|net\b|edu\b|co\b|uk\b|de\b|cc\b|biz\b|mobi\b|xxx\b|tv\b))

could be replaced with::

    shortcuts words.vars
    match ((http)*<chr>*(www)*<chr>*<xta>{3,}<dot>+<dom>)
    shortcuts
    # ^ This will disable the shortcuts for future rules.

Internally, this would be expanded out to the regex above.

In a file called words.vars, you would specify::

    chr (\w|\W|\d|_)
    dom (com\b|org\b|net\b|edu\b|co\b|uk\b|de\b|cc\b|biz\b|mobi\b|xxx\b|tv\b)
    dot (\W|\d|_|dot|\(dot\))
    xta [a-zA-Z0-9\.\-\*_\^\+\~\`\=\,\&*]

You can surround up to 3 characters with <> and they will
be replaced with whatever is defined in that varset.yml file.

Another example:

This file is called letters.vars::

    _ (\W|\d|_)
    E [eu]
    K [ck]

    matchusing letters.var j+<_>*<E>+<_>*r+<_>*<K>+<_>*s*

If you want to match an actual less-than (<) or greater-than (>), use a backslash (\\).

Allowed Characters in shortcut names: [_a-zA-z]

Action Groups * COMPLETE *
--------------------------

Sometimes, you want to have multiple rules that all do the same actions.
An Action Group allows you to predefine a set of actions which you can
then apply to a rule.  Eg::

  actiongroup swearactions
  then warn "Don't say that!"
  then fine 50 Pay $50 to the swear jar!

  .. later in the rules.txt ..

  match jerk
  then replace meanie
  then actions swearactions

Condition Groups * COMPLETE *
-----------------------------

Just as with action groups, condition groups let you specify common conditions
you wish to apply to multiple rules.   Eg::

  conditiongroup ignoreAdmins
  ignore user Sage905
  ignore user tremor77
  ignore user DreamPhreak
  ignore user EpicATrain

  ... later in the rules.txt ...

  rule L3 Match jerk
  matchusing varset j+<_>*<E>+<_>*r+<_>*<K>+<_>*s*
  conditions ignoreAdmins
  then replace meanie
  then actions swearactions



Match Group References *DEFERRED*
----------------------------------
When doing an action, there is currently no way to get the actual string that
matched.  This will allow a match group to be referenced in actions.  Eg::

  match (derp)ity(dah)
  then replace $1 $2

Would match 'derpitydah' and output 'derp dah'

Respond Multiline
-----------------
Add a "then respond" action, which allows \\n to separate lines.

Respond with File
-----------------
Add then respondfile <filename.txt> which will be send to player.

Notify Action * COMPLETED *
----------------------------
A "then notify" action will send the notify string to any logged in player
with a given permission.  Eg:

  then notify pwnfilter.notify &player just said &rawstring

Points System
-------------

New action: then points <##>

New config: warning thresholds. drain rate

Idea:

Think of a bucket with holes in the bottom, and multiple lines on it::


  \         / -- threshold3
   \       /  -- threshold2
    \     /   -- threshold1
     - - -    -- Leak rate: points / s, or points / min

Given rules like this::

    rule S1 Fuck
     match fuck
     then points 20

    rule S2 Asshole
     match asshole
     then points 5

The following will happen:

A user will have 0 points by default.  Every time they trip the filter, it
will add the # of points (20 for 'fuck', 5 for 'asshole').  When they hit
the threshold1 level, PwnFilter will execute the commands at the threshold1
level.  When they hit thresh2, same, thresh3, same.  Every second or minute,
depending on how configured, the configured leak rate number of points will
be subtracted from the bucket.

Thus, if a player swears once in a while, they will get no warning, no
consequence.  If they have a sailor's mouth, they might get a warning at
threshold1 and 2, and a tempban at threshold3.



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


Troubleshooting
+++++++++++++++

Regex Timeout * COMPLETE *
---------------------------
An enhancement to the Regex which will automatically time-out if a Regex
takes more than 500ms to execute.  Upon triggering the timeout, PwnFilter
will log an error showing the failed rule as well as the text that triggered
the timeout.  This should be a big help in troubleshooting runaway regexes.



Possible enhancements for 3.2 or 3.3
++++++++++++++++++++++++++++++++++++

Web-based configuration. (Drag and drop with modals for config)

/pftest command to test a string against a rule.

Name matcher.  Basically, a special "match" rule that would detect the name
of an online player. eg: matchplayer

Name filter: apply rules to player names in onPlayerJoin event.  If player
has offensive name, then take action.

Auto-updater
