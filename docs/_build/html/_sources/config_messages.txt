Config & Messages
=================

======
Config
======

The different config options are described in the config.

If the comments have been removed from the config automatically when Bukkit created it at startup, take a look at the original `config file at GitHub <https://github.com/Phiwa/DragonTravel/blob/master/src/main/resources/config.yml>`_

|

========
Messages
========

The messages file gives you the possibility to customize the messages DragonTravel prints to players.
On startup, DragonTravel automatically loads the file with the name

    'messages-<lang>.yml'

where

    ''<lang>'

is the tag of the desired language specified in the config.

DragonTravel ships with predefined language files for

    * English ('en')
    * German ('de')
    * French ('fr')

If you set one of those tags in the config file, DragonTravel creates those message files on startup if they do not exist.

If you do not want to use one of those files, you are free to create an own file with a different tag in the name (e.g. *'messages-stuff.yml'*)
and set this tag (*'stuff'*) in the config, so DragonTravel loads your file on startup.
