package com.skeletorsue;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class B2 {
    private static String AuthorizationToken, ApiUrl, UploadURL;

    public static void Authorize() throws IOException {
        GetToken();
    }

    public static void Upload(File file, String Bucket) {

    }

    private static String GetUploadURL(String Bucket) {
        if (UploadURL == null) {
            // do stuff
        }

        return UploadURL;
    }

    private static String GetToken() throws IOException {
        if (AuthorizationToken == null) {
            HttpURLConnection connection = null;
            String headerForAuthorizeAccount = "Basic " + Base64.encode((Sync.Config.Credentials.get("account-id") + ":" + Sync.Config.Credentials.get("application-key")).getBytes());
            try {
                AuthorizationToken = "XXX";
                URL url = new URL("https://api.backblaze.com/b2api/v1/b2_authorize_account");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", headerForAuthorizeAccount);
                InputStream in = new BufferedInputStream(connection.getInputStream());

                String RawResponse = IOUtils.toString(in);
                JSONObject Response = new JSONObject(RawResponse);

                ApiUrl = Response.get("apiUrl").toString();
                AuthorizationToken = Response.get("authorizationToken").toString();
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
        }

        return AuthorizationToken;
    }
}
