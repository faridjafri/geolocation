# geolocation
Application to retrieve geolocation of an IP address

To run the application, the system needs to have MongoDB running on default port, and access to the internet for fetching IP details.

Run mvn clean install. Then run command java -jar geolocation-0.0.1-SNAPSHOT.jar. Hit the URL http://localhost:8080/app/greeting in the browser.

There are two tabs - Find Geolocation and History. In order to get data in History tab, find some geolocations in the Find Geolocation tab.

This application is incomplete for the given requirements. The UI does not implement get last N locations or find history between date range, but these functionalities are implemented on the backend.

In order to test the backend, the postman collection is added in src/main/resources/postman/ folder.

Errors yet to be resolved:
The Find Geolocation tab does not refresh data with newly entered IP on the UI, but stored the history in the backend.
Hitting enter refreshes the page.
Exceptions yet to be handled on the backend to throw proper UI errors.
Error handling for top N locations yet to be handled.

Overall, it looks like a lousy application because it took a lot of time integrating backend with UI and I missed small details along the way. I intend to finish this after evaluation.
