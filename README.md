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


*How to implement a new screen following MVP**

Imagine you have to implement a sign in screen.

1. Create new package under ui folder called signin
2. Create an new Activity called **ActivitySignIn**. You could also use a Fragment.
3. Define the view interface that your Activity is going to implement. Create a new interface called **SignInMvpView** that extends **MvpView**. Add the methods that you think will be necessary, e.g. showSignInSuccessful()
4. Create a **SignInPresenter** class that extends **BasePresenter**<SignInMvpView>
5. Implement the methods in **SignInPresenter** that your Activity requires to perform the necessary actions, e.g. signIn(String email). Once the sign in action finishes you should call getMvpView().showSignInSuccessful().
6. Create a **SignInPresenterTest** and write unit tests for signIn(email). Remember to mock the SignInMvpView and also the DataManager
7. Make your **ActivitySignIn** implement **SignInMvpView** and implement the required methods like showSignInSuccessful()
8. In your activity, inject a new instance of **SignInPresenter** and call presenter.attachView(this) from onCreate and presenter.detachView() from onDestroy(). Also, set up a click listener in your button that calls presenter.signIn(email)







