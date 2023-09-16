package com.lrc.missionO2.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrc.missionO2.exceptions.APIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Service
public class SMSService {
    @Value("${sms-api}")
    private String api;
    public void sendSms(String otp, String number) {
        try {
            // Construct data
            String apiKey = "apikey=" + api;
            String message = "&message=" + "Hi there, thank you for sending your first test message from Textlocal. Get 20% off today with our code: "+otp + ".";
            String sender = "&sender=" + "600010";
            String numbers = "&numbers=" + "91" + number;

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
            String data = apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();
            if (stringBuffer.toString().contains("failure")) {
                System.out.println(stringBuffer.toString());
                throw new APIException("SMS not sent", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            throw new APIException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    public String checkBalance() {
        try {
            // Construct data
            String apiKey = "apikey=" + URLEncoder.encode(api, StandardCharsets.UTF_8);

            // Send data
            String data = "https://api.textlocal.in/balance/?" + apiKey;
            URL url = new URL(data);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder sResult = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                // Process line...
                sResult.append(line);
            }
            rd.close();

            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseNode = objectMapper.readTree(sResult.toString());

            return responseNode.path("balance").path("sms").toString();
        } catch (Exception e) {
            // Handle exception
            throw new APIException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
