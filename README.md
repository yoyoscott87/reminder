# reminder

##  設定環境變數



setx REMINDER_SA_PATH "C:\Users\User\AppData\Roaming\ReminderApp\credentials.json"



記得重新下載 json，並放在電腦安全的資料夾裡

下載 jdk 24.0.2 網址 : https://www.oracle.com/tw/java/technologies/javase/jdk24-archive-downloads.html
<img width="1649" height="83" alt="{ACF3E5B6-9BC9-41D6-A040-C990F3A255DE}" src="https://github.com/user-attachments/assets/67a903ae-8e6d-417b-a637-8f9c970a74df" />


並放入環境變數中


## 執行jar檔

java -jar target\reminder-app-1.0-SNAPSHOT.jar  


## 下載exe打包器


https://github.com/wixtoolset/wix3/releases/tag/wix3141rtm


## 執行打包


jpackage --input target --name ReminderApp --main-jar reminder-app-1.0-SNAPSHOT.jar --main-class com.example.reminder.MainApp --type msi --win-menu --win-shortcut --icon C:\intern\reminder\icon.ico
