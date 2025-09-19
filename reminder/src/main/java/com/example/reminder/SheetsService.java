//初始化 Google Sheet API，透過service account

package com.example.reminder;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class SheetsService {

    private static final String APPLICATION_NAME = "ReminderApp";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static Sheets createService() throws IOException, GeneralSecurityException {
        // 嘗試從環境變數讀取
        String saPath = System.getenv("REMINDER_SA_PATH");
        if (saPath == null || saPath.isBlank()) {
            throw new IllegalStateException("❌ 環境變數 REMINDER_SA_PATH 未設定，無法找到 credentials.json");
        }

        try (InputStream in = new FileInputStream(saPath)) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

            return new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
    }
}
