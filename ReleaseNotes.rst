===========================
Release Notes for PwnFilter
===========================

Changes in 3.3.0
================

Deprecated Feature
------------------
The "events chat,command, etc.." option in the rules.txt has been deprecated.
If you have rules that you want to apply to multiple types of events, put them
in their own file, eg:

common.txt::

  match blah
  then replace

And then, in the event rules, use include...

chat.txt::

  include common.txt

Points System
-------------

New action: then points <##>

New config: warning thresholds. drain rate

Idea:

Think of a bucket with holes in the bottom, and multiple lines on it::


  \         / -- threshold3
   \       /  -- threshold2
    \     /   -- threshold1
     - - -    -- Leak rate: points / s

Given rules like this::

     match fuck
     rule S1 Fuck
     then points 20

     match asshole
     rule S2 Asshole
     then points 5

The following will happen:

A user will have 0 points by default.  Every time they trip the filter, it
will add the # of points (20 for 'fuck', 5 for 'asshole').  When they hit
the threshold1 level, PwnFilter will execute the "ascending" commands at the
threshold1 level.  When they hit thresh2, same, thresh3, same.  Every second
or minute, depending on how configured, the configured leak rate number of
points will be subtracted from the bucket.  As the points balance crosses the
threshold from above, it will execute the "descending" actions.

Thus, if a player swears once in a while, they will get no warning, no
consequence.  If they have a sailor's mouth, they might get a warning at
threshold1 and 2, and a ban at threshold3.  Once their points balance
drops back below the ban threshold, they will be unbanned, and allowed back on
the server.

A sample configuration for thresholds is below::

    points:
      enabled: true # 'false' disables the points-system
      leak:
        points: 1.0
        interval: 30 # seconds
      thresholds:
        threshold1:
          name: Warn
          points: 10.0
          actions:
            ascending:
             - respond You have hit our warning threshold for bad language!
            descending:
             - respond You are now back in our good books.  Remember to keep it clean!

        threshold2:
          name: Kick
          points: 20.0
          actions:
            ascending:
             - kick You need to tone down your language!
             - notify pwnfilter.admins %player% was kicked for hitting the kick threshold.

        threshold3:
          name: Tempban
          points: 30.0
          actions:
            ascending:
             - console ban %player% Your language is not acceptable.  Take 15m to cool off!
             - notify pwnfilter.admins %player% was given a temporary ban for bad language.
             - 'notify console &4%player% was given a temporary ban for bad language. Points %points%'
            descending:
             - console pardon %player%
             - notify pwnfilter.admins %player% points have fallen below Tempban threshold. %player% was unbanned
             - notify console &4%player% points have fallen below Tempban threshold. %player% was unbanned


Respond Multiline
-----------------
Enhance "then respond" action, by allowing a "here" tag function, to permit a
multi-line response. Use "then respond <<END" to start the multiline message,
and Terminate the response with a single line that has only 'END' (no quotes).
Eg::

  match ^/grue$
  then respond <<END
  The grue is a sinister, lurking presence in the dark places of the earth. Its
  favorite diet is adventurers, but its insatiable appetite is tempered by its
  fear of light. No grue has ever been seen by the light of day, and few have
  survived its fearsome jaws to tell the tale.
  END

Behavioural Changes in Command Filter
-------------------------------------
By default, commands will no longer be treated as "chat".  They will not be
decolored, nor will they be "spam filtered".  There is a new config entry called
cmdchat:.  If commands are listed in cmdchat, those commands will be treated like
chat events, and will be filtered by the chat.txt filter, instead of the
command.txt filter.  The reason for this change is that there are some commands
(eg: /tell, /msg, /me) which most people feel should be filtered with the chat
filter, and having to include chat filters in command.txt is cumbersome.

::

  cmdchat:
   - me
   - nick
   - tell
   -whisper


Respond with File
-----------------
Add then respondfile <filename.txt> which will be send to player.  By default,
text files for this command are stored in the PwnFilter/textfiles directory.
This can be overridden with the config.yml textdir: config.


Changes in 3.2.0
================

Please read these notes in their entirety.  A lot have changes have been made since 3.1.x.

Your existing config may not work.  At very least, please read the section about backward incompatible
changes, and the new file structure.  If you have questions, please join the #pwn9 channel on espernet
and ask your question there.  Please be patient.  We're not always around.


!!!!!BACKWARDS INCOMPATIBLE CHANGES!!!!!
----------------------------------------

***NOTE****

Any occurances of:
&world ,&player, &string, &rawstring, &event, &ruleid, &ruledescr

will need to be replaced with:
%world% ,%player%, %string%, %rawstring%, %event%, %ruleid%, %ruledescr%

You will get deprecation warnings if you use the old format, but it should still work for now.

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
----------------------------

All of these changes (except the ones noted above) should be backwards compatible with the 3.1.x
and lower versions.

Rules.txt format
^^^^^^^^^^^^^^^^

By default, PwnFilter 3.2 will create a PwnFilter/rules directory, move your current rules.txt
into it, and create one rules file for each handler, which links back to rules.txt.  You do not
need to keep all your rules in rules.txt.  In fact, it is recommended that you create several
rules files (in seperate subdirectories, if you prefer), and link them from each handler.

New folder structure::

    plugins/PwnFilter
             \->rules
                |-> common --> tamewords.txt
                |          |-> badwords.txt
                |          |-> reallybadwords.txt
                |-> sign.txt
                |-> chat.txt
                |-> item.txt
                |-> command.txt
                \-> console.txt

Each of the sign, chat, etc. are rulesets for specific event
handlers.  They can import from any of the files in the rules directory
(or, in fact, any file that can be referred to relative to where it is, eg: common/tamewords.txt)
and/or they can just have rules directly entered.  Eg:

chat.txt::

    include tamewords.txt
    include badwords.txt

    match derp
    then ...

and so on...


Named Rules
^^^^^^^^^^^
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


Shortcuts
^^^^^^^^^

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

Action Groups
^^^^^^^^^^^^^

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

Condition Groups
^^^^^^^^^^^^^^^^

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


Troubleshooting
---------------

Regex Timeout
^^^^^^^^^^^^^
An enhancement to the Regex which will automatically time-out if a Regex
takes more than 500ms to execute.  Upon triggering the timeout, PwnFilter
will log an error showing the failed rule as well as the text that triggered
the timeout.  This should be a big help in troubleshooting runaway regexes.