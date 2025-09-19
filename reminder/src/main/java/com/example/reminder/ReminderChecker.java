//每分鐘讀取試算表資料
//比對「時間欄」和現在時間（±2 分鐘誤差）
//若狀態為「未提醒」→ 呼叫 ReminderPopup.showReminder() → 更新狀態
package com.example.reminder;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReminderChecker {
    private final Sheets sheets;
    private final String spreadsheetId;
    private final String range;

    public ReminderChecker(Sheets sheets, String spreadsheetId, String range) {
        this.sheets = sheets;
        this.spreadsheetId = spreadsheetId;
        this.range = range;
    }

    public void checkAndNotify() throws Exception {
        ValueRange response = sheets.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) return;


        LocalDateTime now = LocalDateTime.now();
        LocalTime nowOnlyTime = LocalTime.now();

        for (int i = 0; i < values.size(); i++) {
            List<Object> row = values.get(i);
            if (row.size() < 2) continue;

            String timeStr = row.get(0).toString().trim();
            String task = row.get(1).toString();
            String status = (row.size() > 2) ? row.get(2).toString() : "";

            try {
                // 1️⃣ 完整日期時間
                if (timeStr.contains("/")) { // 判斷是不是 yyyy/MM/dd HH:mm
                    LocalDateTime remindTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                    long diff = Duration.between(remindTime, now).toMinutes();
                    if (Math.abs(diff) <= 1 && !"已提醒".equals(status)) { // 誤差 ±2 分鐘
                        ReminderPopup.show("📌 " + task);
                        sheets.spreadsheets().values().update(
                                spreadsheetId,
                                "工作表1!C" + (i + 2),
                                new ValueRange().setValues(List.of(List.of("已提醒")))
                        ).setValueInputOption("RAW").execute();
                    }
                }
                // 2️⃣ 只有時間 (每日重複)
                else {
                    LocalTime remindTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
                    long diff = Duration.between(remindTime, nowOnlyTime).toMinutes();
                    if (Math.abs(diff) <= 1) { // 誤差 ±2 分鐘
                        ReminderPopup.show("📌 (每日) " + task);
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ 無法解析時間格式: " + timeStr);
            }
        }
    }
}
