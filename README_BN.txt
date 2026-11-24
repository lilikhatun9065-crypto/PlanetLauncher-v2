
PLANET LAUNCHER — GitHub দিয়ে APK তৈরি

এটি Android-এর একটি Home Launcher সোর্স প্রজেক্ট। অ্যাপের আইকনগুলো গোলকের পৃষ্ঠে সাজানো থাকে। আঙুল দিয়ে টেনে গোলক ঘোরানো যায় এবং আইকনে চাপ দিলে অ্যাপ চালু হয়। সার্চ বক্স দিয়ে অ্যাপ খোঁজা যায়।

GitHub দিয়ে APK বানানোর নিয়ম:
১. GitHub-এ নতুন একটি Repository তৈরি করো।
২. এই ZIP-এর ভেতরের PlanetLauncher ফোল্ডারের সব ফাইল-ফোল্ডার Repository-এর মূল জায়গায় Upload করো। অর্থাৎ app, .github, build.gradle, settings.gradle সরাসরি Repository-তে থাকবে।
৩. GitHub-এর Actions ট্যাবে যাও।
৪. Build Planet Launcher APK workflow চালাও। main/master branch-এ push করলেও workflow চলবে।
৫. Workflow সফল হলে নিচের Artifacts থেকে Planet-Launcher-debug-apk ডাউনলোড করো।
৬. Artifact ZIP Extract করে app-debug.apk ফোনে ইনস্টল করো।
৭. Home বাটন চাপ দিয়ে Planet Launcher-কে Default Home App হিসেবে নির্বাচন করো।

গুরুত্বপূর্ণ:
- এটি Debug APK; Play Store-এর জন্য release-signed APK নয়।
- Android 6.0 বা পরের সংস্করণ লক্ষ্য করে তৈরি।
- সব ফোনে পরীক্ষা করা হয়নি।
- গোলকটি Android Canvas-এ আঁকা 3D-ধাঁচের ইন্টার‌্যাকটিভ গোলক; এটি পূর্ণ 3D mesh নয়।
- অ্যাপ তালিকা দেখাতে Android package visibility permission ব্যবহার করা হয়েছে।

লোকালি build করতে JDK 17 ও Gradle 8.7 লাগবে। প্রজেক্ট ফোল্ডারে `gradle assembleDebug` চালাও। APK পাওয়া যাবে:
app/build/outputs/apk/debug/app-debug.apk
