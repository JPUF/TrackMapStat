# TrackMapStat 🏃‍♀️
Track your runs, see useful stats, and log your exercise! Created as the final assignment for the Mobile Device Programming module @ UoN. 

![Screenshots: overview of functionality ](https://i.imgur.com/CXdcpaf.png)

## Components 🧱
This app was designed to explore all the major 'components' of Android development:

### UI - Activities / Fragments 🔎
Each UI screen was implemented using a Fragment. This is more lightweight, as less it's less computationally expensive to instantiate a Fragment, over an Activity. Navigation between Fragments was managed using the 'Navigation' component.

![Navigation graph](https://i.imgur.com/RU50Y3x.png)

For further minimisation of run-time computation, programmatic references to View widgets were obtained using data binding, which provides compile-time references, eliminating expensive calls to findViewById().

### Services 🚌
The function of tracking a user's exercise is an inherently long-running task. This requires a Service, where execution is decoupled from the UI. The user's location is accessed via the Location system service. To ensure loose coupling, the Service has no reference to the UI View that started it, as such, communication between the Service and the View is acheived through a selection of callback methods. While this long-running code is active, a notification is displayed to the user.

### Content Provider 💾
An Android Content Provider was implemented as this app's core method of persistence. The Content Provider acts as a layer of abstraction over an underlying SQLite Database which was used to store data on the user's runs. The information stored in the database is displayed via a RecyclerView.

### Broadcast Receiver 📶
This component allows for the TrackStatMap app to respond to System broadcasts. This was used to respond to the event of the user disabling their phone’s GPS. The app’s core functionality relies on GPS readings, the LocationStateReceiver prompts the user to re-enable their GPS. To avoid leaking the receiver at runtime, this Broadcast Receiver is registered and unregistered in time with the underlying MainActivity lifecycle. 

## Architecture 🏗
In order to provide predictable behaviour, I needed a design that could gracefully handle Android's lifecycle events, and configuration changes. As such, I chose the Model-View-ViewModel (MVVM) design pattern. 

![MVVM](https://i.imgur.com/iuxg1Wt.png)

With MVVM, all dynamic data is handled by the ViewModel. This is a singleton that its respective View (Fragment) couples to (or decouples from) during lifecycle events. This allows dynamic data (such as the current distance ran) to survive outside the lifetime of the Fragment. 
