# DS-Desktop-Notify ![build passing](http://b.repl.ca/v1/build-passing-green.png) ![java-version 1.7](http://b.repl.ca/v1/java--version-1.7-blue.png) ![platform desktop](http://b.repl.ca/v1/platform-desktop-orange.png)
###### A lightweight library that you can use in your Java-powered desktop apps to show floating notifications on the desktop.

<p align="center">
  <img src="http://dscore.webcindario.com/software/img/showcase/desktopnotify.png" alt="Preview"/>
</p>

Under development, this library allows to show small and stylish notifications on the Desktop. You, as developer, just need to add this tiny .jar to your classpath and invoke a single static method to create and display a new notification. As simple as the following:

```java
DesktopNotify.showDesktopMessage(
    "This is a notification",
    "With DS Desktop Notify, displaying notifications on the screen is quick and easy!",
    DesktopNotify.SUCCESS);
```

You can customize your notification to make it show what you want. You can specify a title, a message, an icon (or use a default one) and even the fonts and colors, picking one of the provided themes or creating one of your own. You don't need to worry about the bounds nor where you should break lines (as in JOptionPane), because all of this is automatically measured before showing your notification.

You can specify screentime and actions too, or you could make your notification stay on the screen as long as necessary for the user to see and click it (it means, no limit until the user clicks it).

So, no matter what your application is about, if you need to show pop-up notifications on the Desktop in any moment, you can use this library to deal with that.

## Features
- Lightweight and easy to use.
- Create and show your notification with a single code line!
- The notifications can be closed by mouse clicking, or have an specific time on screen.
- They can wait on queue if there's no room to show them all.
- Support for action events.
- They won't mess with the taskbar in Windows PCs!
- It leaves no traces: the service thread automatically stops when there are no more notifications to show. It is also started again when new notifications arrive.

## Requirements
- Windows XP/Vista/7/8, Linux, Mac-OS X
- Java Runtime Environment 6 or higher
- Translucent windows support in AWT/Swing is recommended for best results (Java 7 or higher is needed for this).

## Being developed
- The ability to spawn notifications from external processes via the command line (maybe to be added as a separate distributable).
- Transitions and more customizable options for the notification area.

## Do you want to use this in a production environment?
It has come to my attention that there's been some enterprise-class projects that have included this library as one of their dependencies. This is why I've deployed an [official javadoc](http://dscore.webcindario.com/software/desktopnotify/javadoc).

If you are interested in using DS Desktop Notify and need some help/guidance with it, want to request a feature you need or submit a bug, you can send an e-mail to [the.drag.shot@gmail.com](mailto:the.drag.shot@gmail.com) with a subject that starts with **[Desktop Notify]**, or start a discussion here on GitHub.
