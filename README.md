
# Planet Launcher

An Android home-screen launcher with a touch-rotatable 3D-style app sphere. App icons are arranged across a spherical surface; swipe to rotate, tap an icon to launch, and use the search field to filter installed apps.

## Project details

- Android application written in Java
- Minimum Android: 6.0 (API 23)
- Compile/target SDK: 35
- Application ID: `com.nice.planetlauncher`
- No third-party runtime libraries
- Android HOME intent is included so it can be selected as the default launcher

## Build the APK on GitHub

1. Create a new GitHub repository.
2. Upload the contents of this folder (so `app/`, `build.gradle`, `settings.gradle`, and `.github/` are at the repository root).
3. Open the repository's Actions tab.
4. Run Build Planet Launcher APK (or push to main/master to trigger it).
5. Open the completed workflow run. Under Artifacts, download `Planet-Launcher-debug-apk`.
6. Extract the downloaded artifact and install `app-debug.apk` on your Android phone.

The workflow builds a debug APK for testing. It is not a Play Store release or a signed production release.

## Build locally

Install JDK 17 and Gradle 8.7, then run from this folder:

`gradle assembleDebug`

APK output:

`app/build/outputs/apk/debug/app-debug.apk`

## Use as the home screen

After installation, press the phone's Home button and select Planet Launcher. If Android does not show the chooser, open Settings and search for Default apps or Home app, then select Planet Launcher. To switch back, choose the phone's original launcher in the same settings screen.

## Notes

- Android package visibility is requested so the launcher can list launchable apps. Some system or restricted apps may not appear.
- Search filters the app sphere; clearing the search restores the full list.
- This is a 2D Canvas-rendered 3D-style sphere, not a hardware 3D model. Rotation and icon placement are interactive.
- The project has not been tested on every Android device. Build and test on the target phone before relying on it as the default home screen.
