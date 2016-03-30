package com.skeletorsue;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class B2 {
    private JSONObject UploadAuth, Authorization;
    private Bucket Bucket;

    public B2(Bucket Bucket) throws IOException {
        this.Bucket = Bucket;
    }

    public void Upload(File file, Bucket Bucket) throws IOException, NoSuchAlgorithmException {
        String File = file.getPath().substring(Bucket.Directory.length() + 1);

        byte fileData[] = Files.readAllBytes(Paths.get(file.getPath()));
        HttpURLConnection connection = null;
        try {
            URL url = new URL(GetUploadAuth().getString("uploadUrl"));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", GetUploadAuth().getString("authorizationToken"));
            connection.setRequestProperty("Content-Type", "b2/x-auto");
            connection.setRequestProperty("X-Bz-File-Name", URLEncoder.encode(File, "UTF-8"));
            connection.setRequestProperty("X-Bz-Content-Sha1", Hash.File("SHA1", file));
            connection.setDoOutput(true);
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.write(fileData);

            InputStream in = new BufferedInputStream(connection.getInputStream());
            String RawResponse = IOUtils.toString(in);
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

    private JSONObject GetUploadAuth() throws IOException {

        if (UploadAuth == null) {
            System.out.println("Getting another thing");
            HttpURLConnection connection = null;
            String postParams = "{\"bucketId\":\"" + Bucket.Name + "\"}";
            byte postData[] = postParams.getBytes(StandardCharsets.UTF_8);

            try {
                URL url = new URL(GetAuthorization().getString("apiUrl") + "/b2api/v1/b2_get_upload_url");

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", GetAuthorization().getString("authorizationToken"));
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

    private JSONObject GetAuthorization() throws IOException {
        if (Authorization == null) {
            HttpURLConnection connection = null;
            String headerForAuthorizeAccount = "Basic " + Base64.encodeBase64String((Sync.Config.Credentials.get("account-id") + ":" + Sync.Config.Credentials.get("application-key")).getBytes());
            try {
                URL url = new URL("https://api.backblaze.com/b2api/v1/b2_authorize_account");

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", headerForAuthorizeAccount);

                InputStream in = new BufferedInputStream(connection.getInputStream());
                String RawResponse = IOUtils.toString(in);
                JSONObject Response = new JSONObject(RawResponse);

                Authorization = Response;
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
        }

        return Authorization;
    }
}
