package com.evoting.securevoting.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private Map<String, String> otpStorage = new HashMap<>();

    // Generate OTP
    public String generateOtp(String email) {

        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);

        String otpString = String.valueOf(otp);

        otpStorage.put(email, otpString);

        return otpString;
    }

    // Verify OTP
    public boolean verifyOtp(String email, String otp) {

        if (!otpStorage.containsKey(email)) {
            return false;
        }

        String storedOtp = otpStorage.get(email);

        return storedOtp.equals(otp);
    }

    // Remove OTP after successful verification
    public void clearOtp(String email) {
        otpStorage.remove(email);
    }
}
