FAQ
===

This page gives answers to some frequently asked questions about DragonTravel.


Is it possible to remove the dragon's health-bar?
-------------------------------------------------

Short answer: No

Longer answer: At the moment this is not possible because the health-bar is rendered by the client and the server has no influence on that if we do not use third party APIs which we do not want to rely on.
Should it ever be possible to remove the health-bar, we will remove it!

|

Is it possible to change the speed the dragon's wings are flapping?
-------------------------------------------------------------------

Short answer: No

Longer answer: The problem about this is that the dragon's wings are rendered client-side, so the plugin (which is only run on the server) does not have any chance to modify the speed of the wings.
For some players, the wings a flapping too fast, for some players the wings a flapping in a perfect speed, but we cannot change this speed.

|

Is it possible to control the dragon manually?
----------------------------------------------

Short answer: No, but it might be implemented in the future

Longer answer: When this project started, there was the project RideThaDragon by V10lator which enabled you to do exactly that, so we decided that this does not fit into DragonTravel, because, like the name already says, Dragontravel is about "travels", not free-riding.
During the past months we thought about this topic again, because the project RideThaDragon has been abandoned by the author and we came to the point that we might implement this feature as soon as we have enough time.

|

Is it possible to move the rider closer to the dragon to avoid him flying above it?
-----------------------------------------------------------------------------------

Short answer: No

Longer answer: The dragon has a hitbox which is a bit bigger than the actual dragon.
The player cannot sit "inside" this hitbox because the server doesn't want a player to sit inside a monster (seems legit, right? ;) ), so we cannot move the player closer to the dragon. -.-
Other players do not see the rider floating as far above the dragon as the rider sees himself. This difference is caused by the client-side rendering of the player models.

|

DragonTravel does not start, what can be the problem?
-----------------------------------------------------

Short answer: How can I know? Best would be to check the server.log, this usually tells you the problem.

Longer answer: There are several possible reasons for this and I cannot give an exact answer to this question.
The most common reasons are listed below:

    #. One of the most common reasons for this is that users updated their server's version of DragonTravel and the new version contains new options in the config.yml or the messages.yml. If DragonTravel then tries to start and recognizes that the existing config-/messages-file is outdated, it states that in the server-log and requires the admin to delete the old config (backup it before!) to create a new one at start-up. Afterwards, the admin can change the new config the way he likes to.
    #. Of course there is also the possibility that there is an error which was caused by a corrupted database, configy.ml, messages.yml, a dumb admin or one of us developers. You normally see this directly when taking a look at the server.log. In this case, we can best help you if you simply paste your server.log at pastebin.com and create a ticket linking to it, telling us some important things about the moment the problem occured (What did you do? What did you expect to happen? What did actually happen? ...?). We will then try to help you as soon as possible.
