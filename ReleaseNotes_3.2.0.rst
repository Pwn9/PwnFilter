Proposed New Features for PwnFilter 3.2.0
=========================================

Please read these notes in their entirety.  A lot have changes have been made since 3.1.x.

Your existing config may not work.  At very least, please read the section about backward incompatible
changes, and the new file structure.  If you have questions, please join the #pwn9 channel on espernet
and ask your question there.  Please be patient.  We're not always around.


!!!!!BACKWARDS INCOMPATIBLE CHANGES!!!!!
++++++++++++++++++++++++++++++++++++++++

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
+++++++++++++++++++++++++++++

All of these changes (except the ones noted above) should be backwards compatible with the 3.1.x
and lower versions.

Rules.txt format
----------------

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
-----------
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
---------

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
-------------

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
----------------

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
+++++++++++++++

Regex Timeout
-------------
An enhancement to the Regex which will automatically time-out if a Regex
takes more than 500ms to execute.  Upon triggering the timeout, PwnFilter
will log an error showing the failed rule as well as the text that triggered
the timeout.  This should be a big help in troubleshooting runaway regexes.