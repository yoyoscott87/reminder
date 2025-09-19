# reminder

##  設定環境變數



setx REMINDER_SA_PATH "C:\Users\%USERNAME%\AppData\Roaming\ReminderApp\credentials.json"



記得重新下載 json，並放在電腦安全的資料夾裡


將google服務帳戶用同一個 credentials處理，將google sheet新增工作表放提醒事項


## 執行jar檔

java -jar target\reminder-app-1.0-SNAPSHOT.jar  


## 下載exe打包器


https://github.com/wixtoolset/wix3/releases/tag/wix3141rtm


## 執行打包


jpackage --input target --name ReminderApp --main-jar reminder-app-1.0-SNAPSHOT.jar --main-class com.example.reminder.MainApp --type msi --win-menu --win-shortcut --icon C:\intern\reminder\icon.ico
