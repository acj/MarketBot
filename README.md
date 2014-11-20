MarketBot
=========

A library for tracking app reviews and notifying interested parties.

### Demo

```$ java -jar MarketBot.jar <username> <password> <package name for app>```

The username and password must match a valid Google account and are required to interact with the Google Play API. They're not stored anywhere, nor are they used for any other purpose. The account does not need to be the one used to publish the app(s) that you're tracking; it can be any valid account.

### Inputs

 * Google Play
 * App Store
 
### Outputs

 * [Flowdock](https://flowdock.com)
 * [GroupMe](https://groupme.com)
 * Console/Terminal
 
 
### Contributing

The inputs and outputs for MarketBot are extensible and easy to use. If you develop a new one, please send me a pull request.

### Acknowledgements

 * [android-market-api](https://code.google.com/p/android-market-api/) library

### License

Apache 2.0. Please see [LICENSE](LICENSE) for the nitty gritty.
