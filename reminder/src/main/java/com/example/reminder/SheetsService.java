//初始化 Google Sheet API，透過service account

package com.example.reminder;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.InputStream;
import java.util.Collections;

public class SheetsService {
    private static final String APPLICATION_NAME = "ReminderApp";

    public static Sheets createService() throws Exception {
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var jsonFactory = GsonFactory.getDefaultInstance();

        InputStream in = SheetsService.class.getResourceAsStream("/credentials.json");
        if (in == null) {
            throw new IllegalStateException("請把 credentials.json 放在 src/main/resources/");
        }

        var credentials = ServiceAccountCredentials.fromStream(in)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));

        return new Sheets.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
