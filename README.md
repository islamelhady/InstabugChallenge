# InstabugChallenge

### Tools & APIs:
- Kotlin
- View Binding   
- View Model
- View Model Factory
- Repository
- Live Data
- [Executers] (https://developer.android.com/reference/java/util/concurrent/Executors)
- [HttpURLConnection] (https://developer.android.com/reference/java/net/HttpURLConnection)
- I didn't use Dagger hilt for DI as it's considered as 3rd parties.



### Code Architecture:
MVI


### Requriments:
We need you to create an application that will help in testing backed APIs.
This application must allow us to do the following:
1. Enter URL to connect to
2. Add request headers (They are dynamic, the user can add as many headers as he can)
3. The app must support GET/POST request types:
   - For the POST request type, the App must provide the ability to switch between 2 request body types:
     - JSON String
     - multipart/ file upload.
4. After request completion, the app needs to display the following:
   - Request URL
   - Response code
   - Error if any
   - Request/Response headers
   - Request body or query parameters depending on request type
5. If the device is offline then app must show an error message and do not make the call
6. Requests alongside their responses should be cached
7. The app must allow viewing of cached requests and responses
8. The app must allow sorting cached requests/responses by their execution time.
9. The app must allow filtering cached requests/responses by request type
10. The app must allow filtering cached requests/responses by response status.
Success/Failure
11. Your app must handle configuration changes like screen rotation

### Notes:
Your app must not use any third party libraries. We consider (Retrofit,
volley, coroutines, room….etc) as 3rd parties

