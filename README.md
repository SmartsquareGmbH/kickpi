# KickPi   
[![forthebadge](https://forthebadge.com/images/badges/built-for-android.svg)](https://forthebadge.com)

The KickPi is a Android Things application which acts as a server for the mobile clients and handles the matchmaking, the authorization with the kickway and the game storage.

# Architecture

The foosball table is connected to a raspberry which communicates with two photocells to react in case of a new goal. The mobile devices are used to create a new game, join an existing one, display the top ten players or the statistics of a specific player. The pairing between the players mobile devices and the raspberry is implemented using the [Google Nearby API](https://developers.google.com/nearby/). The Raspberry authorizes all players who try to join a lobby and persist the games.

![](https://image.ibb.co/gd64F9/Architekturdiagramm.jpg)

# Prerequisites
You need a deployment device with android things as operating system since no Android Things emulator does yet exist. :pensive: [Discover](https://developer.android.com/things/hardware/) the hardware platforms supported by Android Things.

The application also needs a secret.properties file in the root directory with the following key-values pairs. 
``` 
KICKWAY_URL=https://192.168.7.127:8080/
LEFT_GOAL_GPIO=BCM23
RIGHT_GOAL_GPIO=BCM24
SCORE_TO_FINISH_GAME=10
```

_Note: The kickchain currently supports only games which ended up with one team has scored ten goals. The I/O ports for the left and right goal can be seen in the next section._

# GPIO
![](http://mindmapengineers.com/sites/default/files/styles/large/public/field/image/piio.png?itok=aoPcK62h)

# Links
:link: [Kickchain](https://github.com/smartsquare/kickchain)
:left_right_arrow: [Kickway](https://github.com/SmartsquareGmbH/kickway)
:iphone: [Android App](https://github.com/SmartsquareGmbH/kickdroid)
:books: [Android Things Documentation](https://developer.android.com/docs/)
