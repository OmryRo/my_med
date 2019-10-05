# My Medicine

<img src="https://github.com/OmryRo/my_med/blob/master/graphics/market_logo.png" width="200" align="left" />

My Medicine is an "school project" as part of course #67625 at The Hebrew University in Jerusalem.
 
My Medicine is an App for Android (7+) that help tracking medicine times and appointments to the doctor.

**Note:** keep in mind that this App is part of a school project and won't be developed farther then the current last version. 


## Features

* Keep track of your regular medicines, doctors, when the prescriptions will expire and appointments.
* Instead of writing the name of the medicines, you can use the camera of the phone to scan the barcode (works only with Israeli packages and for a small selection of medicines).
* Notifications to remind you when to take your medicine, the time of appointments and when hte prescriptions will expire.
* Drip counter that sometimes works on some devices.

## Screenshots

<img src="https://github.com/OmryRo/my_med/blob/master/graphics/screenshot_1.png" width="300" />
<img src="https://github.com/OmryRo/my_med/blob/master/graphics/screenshot_2.png" width="300" />

## Technical Notes

* Uses Firebase to scan bar-codes and keep a list of medicines with their barcode for the search feature.
* Uses OpenCV for the drip counter. Which also the reason for the fat APK size.
* We use Room from AndroidX to save the data locally. We don't save any of the user data on any external database.

## License

* The icon of the application and in some places in the app using Icons of [Font Awesome](https://fontawesome.com/license/free). Icons from font awsome are marked in the vector file.
* The icons that aren't marked with anything, such as Meterial Design Fonts are under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html).
* The code under the package *il.ac.huji.cs.postpc.mymeds.calender_view* are *'extension'* for the library [mCalendarView](https://github.com/SpongeBobSun/mCalendarView) which under [Apache 2.0](https://github.com/SpongeBobSun/mCalendarView/blob/master/LICENSE)
* Anything else in the repository is under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html).