# DS-Desktop-Notify
A lightweight library that you can use in your Java-powered desktop apps to show floating notifications on the desktop.

Under development, this library allows to show small and stylish notifications on the Desktop. You, as developer, just need to add this tiny .jar to your classpath and invoke a single static method to create and display a new notification. As simple as the following:

'''java
DesktopNotify.showDesktopMessage(
  "This is a notification",
  "With DS Desktop Notify, displaying notifications on the screen is quick and easy!",
  DesktopNotify.SUCCESS);
'''

You can customize your notification to make it show what you want. You can specify a title, a message, an icon (or use a default one) and even the fonts and colors, picking one of the provided themes or creating one of your own. You don't need to worry about the bounds nor where you should break lines (as in JOptionPane), because all of this is automatically measured before showing your notification.

You can specify screentime and actions too, or you could make your notification stay on the screen as long as necessary for the user to see and click it (it means, no limit until the user clicks it).

So, no matter what your application is about, if you need to show pop-up notifications on the Desktop in any moment, you can use this library to deal with that.
