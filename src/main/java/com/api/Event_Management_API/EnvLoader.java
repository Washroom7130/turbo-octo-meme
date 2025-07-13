package com.api.Event_Management_API;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    public static void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("GMAIL_USERNAME", dotenv.get("GMAIL_USERNAME"));
        System.setProperty("GMAIL_PASSWORD", dotenv.get("GMAIL_PASSWORD"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("PAYOS_CLIENT_ID", dotenv.get("PAYOS_CLIENT_ID"));
        System.setProperty("PAYOS_API_KEY", dotenv.get("PAYOS_API_KEY"));
        System.setProperty("PAYOS_CHECKSUM_KEY", dotenv.get("PAYOS_CHECKSUM_KEY"));
    }
}
