package com.cse5236.buckeyeschedule;

import org.junit.Test;

import static org.junit.Assert.*;


import android.util.Patterns;

import com.google.common.base.Charsets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    private boolean isValidEmailId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    @Test
    public void isValidEmailTest() {
        String email = "lin.3236@osu.edu";
        boolean result = isValidEmailId(email);
        assertTrue(result);
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return Pattern.compile("^[+]?[0-9]{10,13}$").matcher(phoneNumber).matches();
    }

    @Test
    public void isValidPhoneNumberTest() {
        String phoneNumber = "6145377582";
        boolean result = isValidPhoneNumber(phoneNumber);
        assertTrue(result);
    }

    @Test
    public void isPasswordEncryptedBase64Test() throws Exception {
        String password = "12346742623747";
        byte[] bytes = password.getBytes("UTF-8");
        String encryptedPassword = com.cse5236.buckeyeschedule.Base64.encodeToString(bytes, 1);
        byte[] decoded_bytes = com.cse5236.buckeyeschedule.Base64.decode(encryptedPassword, 1);
        String decodedPassword = new String(decoded_bytes, Charsets.UTF_8);
        assertEquals(password, decodedPassword);
    }

    @Test
    // This one fail for some reason
    public void isPasswordEncryptedAESCryptTest() throws Exception {
        String password = "12346742623747";
        AESCrypt aesCrypt = new AESCrypt();
        String encodedPassword = aesCrypt.encrypt(password);
        String decodedPassword = aesCrypt.decrypt(encodedPassword);
        assertEquals(password, decodedPassword);
    }

    public static boolean isValidPassword(String password) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}");
        return password.length() != 0 && PASSWORD_PATTERN.matcher(password).matches();
    }

    @Test
    public void isValidPasswordTest() {
        String password = "6145377583";
        boolean result = isValidPassword(password);
        assertTrue(result);
    }

    public static boolean isValidUrl(String url) {
        String formatter = "^((ftp|http|https):\\/\\/)?(www.)?(?!.*(ftp|http|https|www.))[a-zA-Z0-9_-]+(\\.[a-zA-Z]+)+((\\/)[\\w#]+)*(\\/\\w+\\?[a-zA-Z0-9_]+=\\w+(&[a-zA-Z0-9_]+=\\w+)*)?$";
        return Pattern.compile(formatter).matcher(url).matches();
    }

    @Test
    public void isValidUrlTest() {
        String url = "https://hongdalin.me";
        boolean result = isValidUrl(url);
        assertTrue(result);
    }

    public static boolean isValidDateTime(String dateTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        try {
            format.parse(dateTime);
            return true;
        }catch (ParseException e){
            return false;
        }
    }

    @Test
    public void isValidUrlDateTime() {
        String dateTime = "2022-11-25 08-02";
        boolean result = isValidDateTime(dateTime);
        assertTrue(result);
    }
}
