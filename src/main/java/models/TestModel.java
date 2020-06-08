package models;

import freemarker.template.*;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class TestModel {
    public static String makeItLower(String data) {
        return data.toLowerCase();
    }
    public static String getQueryString() throws Exception {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        configuration.setDirectoryForTemplateLoading(new File("."));
        configuration.setDefaultEncoding("UTF-8");
        Template template = configuration.getTemplate("src/main/java/models/testModelTemplate.sql");
        StringWriter stringWriter = new StringWriter();

        // Make the array of users and each user.
        ArrayList<User> users = new ArrayList<User>();
        // Note: These must be a "Bean" (getters and setters and constructors).
        User user1 = new User("user1", "pass1");
        users.add(user1);
        User user2 = new User("user2", "pass2");
        users.add(user2);

        // Add them to the root context (must be a String => Object HashMap).
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("users", users);

        // Process the template.
        template.process(context, stringWriter);

        return stringWriter.toString();
    }
}
