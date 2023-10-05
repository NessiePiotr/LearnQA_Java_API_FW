package lib;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class DataGenerate {

    public static String getRandomEmail(){
        String timeStamp = new SimpleDateFormat("yyyyMMddmmss").format(new java.util.Date());
        return "learnqa" + timeStamp + "@example.com";
    }

    public static Map<String, String> getRegistrationData(){
        Map<String, String> data = new HashMap<>();

        data.put("email", DataGenerate.getRandomEmail());
        data.put("password","123");
        data.put("username","learnqa");
        data.put("firstName","learnqa");
        data.put("lastName","learnqa");

        return data;
    }

    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues){
        Map<String, String> defaultValues = DataGenerate.getRegistrationData();

        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        for (String key : keys) {
            if (nonDefaultValues.containsKey(key)) {
                userData.put(key, nonDefaultValues.get(key));
            } else {
                userData.put(key, defaultValues.get(key));
            }
        }
        return userData;
    }

    public static String getRandomStringWithGivenSize(int size) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

    public static Map<String, String> getRegistrationDataWithoutOneField(String notExpectedFiledName){
        Map<String, String> defaultValues = DataGenerate.getRegistrationData();

        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        for (String key : keys) {
            if (!Objects.equals(notExpectedFiledName, key)) {
                userData.put(key, defaultValues.get(key));
            }
        }
        return userData;
    }
}
