# RedditBrowser

A light weigh ad-free reddit client built in top of the new architecture components.

It uses the new ```LiveData``` for reactive programming and the new ```ViewModel``` to achieve separation of concerns through MVVM architecture.

It uses ```Room``` to easily cache the posts in an SQLite Database.

It uses Retrofit and Reddit's Restful API's to acces the posts. You have to setup your own reddit app and use its token.

Commenting isn't functioning yet, it's on the way.

It also supports instant app, you can run the app without installing through the website(to be set-up later) or by pressing "Try Now" in the play store.
