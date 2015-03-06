MarketBot ![Build Status](https://travis-ci.org/acj/MarketBot.svg?branch=master)
=========

A library for tracking app reviews and notifying interested parties.

### Getting Started

1. Edit [`MarketBot.java`](src/main/java/org/linuxguy/MarketBot/MarketBot.java) to suit your apps.
2. `$ gradle run`

If you are using MarketBot to track an app in Google Play, the username and password must match a valid Google account. These credentials are required to interact with the Google Play API. They're not stored anywhere, nor are they used for any other purpose. The account does *not* need to be the one used to publish the app(s) that you're tracking; it can be any valid Google account. I strongly recommend that you use a dummy account to protect your apps and personal data.

### Inputs

 * App Annie: iOS, Android, Windows, Mac, Amazon, iBooks, ...
 * Google Play: Android (RSS)
 * App Store: iOS, Mac (RSS)
 
### Outputs

 * [Flowdock](https://flowdock.com)
 * [GroupMe](https://groupme.com)
 * Console/Terminal
 
 
### Contributing

The inputs and outputs for MarketBot are extensible and easy to use. If you develop a new one, please send me a pull request.

To add a new input, implement a `Watcher` subclass.

To add a new output, implement a `Notifier` subclass.

### Acknowledgements

 * [android-market-api](https://code.google.com/p/android-market-api/) library

### License

Apache 2.0. Please see [LICENSE](LICENSE) for the nitty gritty.
