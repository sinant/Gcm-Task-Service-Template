# GcmTaskService Example
MyTaskService handles one time and repetitive tasks in the background thread.
## Starting Task Service
Call startTaskService() in onCreate and start service in onActivityResult. This way TaskService will only start if a task is scheduled.
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState != null)
    startTaskService();
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
        case REQUEST_GOOGLE_PLAY_SERVICES:
        if (resultCode == Activity.RESULT_OK) {
            Intent i = new Intent(this, TaskService.class);
            startService(i); // OK, init GCM
        }
        break;
        default:
        super.onActivityResult(requestCode, resultCode, data);
    }
}

private void startTaskService() {
    GoogleApiAvailability api = GoogleApiAvailability.getInstance();
    int isAvailable = api.isGooglePlayServicesAvailable(this);
    if (isAvailable == ConnectionResult.SUCCESS) {
        onActivityResult(REQUEST_GOOGLE_PLAY_SERVICES, Activity.RESULT_OK, null);
    } else if (api.isUserResolvableError(isAvailable) &&
    api.showErrorDialogFragment(this, isAvailable, REQUEST_GOOGLE_PLAY_SERVICES)) {
        // wait for onActivityResult call
        } else {
        Toast.makeText(this, api.getErrorString(isAvailable), Toast.LENGTH_LONG).show();
    }
}
```
## One off task
### Create
```java
TaskService.scheduleOneOff(context);
```
One off task will start between 0 to 10 seconds.
### Cancel
```java
TaskService.cancelOneOff(context);
```
## Repeating Task
### Create
```java
TaskService.scheduleRepeat(context);
```
Repeating task will fire every 60 seconds.
### Cancel
```java
TaskService.cancelRepeat(context);
```
### Canceling All Active Tasks
```java
TaskService.cancelRepeat(context);
```
