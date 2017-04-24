# boilerplate-mvp
This is a boilerplate project Android for start MVP architecture

What is an MVP design pattern?

MVP design pattern is a set of guidelines that if followed, decouples the code for reusability and testability.
It divides the application components based on its role, called separation of concern.

MVP divides the application into three basic components:

**Model**: It is responsible for handling the data part of the application.It is responsible for fetching the data from server,
database and file system.
DataManager only serves when asked

The Model is broken into few parts: ApiHelper, PreferenceHelper, DatabaseHelper, and FileHelper. 
These are all helpers to a DataManager, which in essence binds all Model parts

**View**: It is responsible for laying out the views with specific data on the screen.
It delegates all the user interactions to its Presenter.The View never communicates with Model directly. In Android,
Activity, Fragment, and a CustomView act as the View part of the application.

Each View has a Presenter in a one-to-one relationship. View communicates with its Presenter through an interface and vice versa.

**Presenter**: It is a bridge that connects a Model and a View. It also acts as an instructor to the View.
Presenter is responsible for delegating View’s requirements to Model and instructing View with actions for specific events.
Presenter communicates with the DataManager through an interface.

So,Let’s take a look at the project structuring:

   The entire app is packaged into **five** **parts**:
   
1. **data**: It contains all the data accessing and manipulating components.
2. **di**: Dependency providing classes using Dagger2.
3. **ui**: View classes along with their corresponding Presenters.
4. **service**: Services for the application.
5. **utils**: Utility classes.





