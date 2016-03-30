package com.skeletorsue;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.xml.ws.Response;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class B2 {
    private static String AuthorizationToken, ApiUrl;
    private static JSONObject UploadAuth;

    public static void Authorize() throws IOException {
        GetToken();
    }

    public static void Upload(File file, Bucket Bucket) throws IOException, NoSuchAlgorithmException {
        JSONObject UploadAuth = GetUploadURL(Bucket.Name);
        String File = file.getPath().substring(Bucket.Directory.length() + 1);

        byte fileData[] = Files.readAllBytes(Paths.get(file.getPath()));
        HttpURLConnection connection = null;
        try {
            URL url = new URL(UploadAuth.getString("uploadUrl"));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", UploadAuth.getString("authorizationToken"));
            connection.setRequestProperty("Content-Type", "b2/x-auto");
            connection.setRequestProperty("X-Bz-File-Name", URLEncoder.encode(File, "UTF-8"));
            connection.setRequestProperty("X-Bz-Content-Sha1", Hash.File("SHA1", file));
            connection.setDoOutput(true);
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.write(fileData);

            InputStream in = new BufferedInputStream(connection.getInputStream());
            String RawResponse = IOUtils.toString(in);
            JSONObject Response = new JSONObject(RawResponse);
            System.out.println("We've uploaded " + URLEncoder.encode(File, "UTF-8"));

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage() + ": " + URLEncoder.encode(File, "UTF-8"));
            throw ioe;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static JSONObject GetUploadURL(String Bucket) throws IOException {

        if (UploadAuth == null) {
            HttpURLConnection connection = null;
            String postParams = "{\"bucketId\":\"" + Bucket + "\"}";
            byte postData[] = postParams.getBytes(StandardCharsets.UTF_8);

            try {
                URL url = new URL(ApiUrl + "/b2api/v1/b2_get_upload_url");

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", AuthorizationToken);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
                connection.setDoOutput(true);

                DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                writer.write(postData);

                InputStream in = new BufferedInputStream(connection.getInputStream());
                String RawResponse = IOUtils.toString(in);
                UploadAuth = new JSONObject(RawResponse);


            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        return UploadAuth;
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
