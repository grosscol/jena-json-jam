package edu.umich.lhs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by grosscol on 2017-08-31.
 */
public class TestUtils {
  // Convenience method to avoid typing .json at end of resource names
  public static String jsonFixture(String fixtureName) throws IOException {
    return loadFixture(fixtureName + ".json");
  }

  // Helper function to retrieve string fixtures from test package resources
  public static String loadFixture(String fixtureName) throws IOException {
    String fixture = new Scanner(
        TestUtils.class.getResourceAsStream("/fixtures/" + fixtureName), "UTF-8")
        .useDelimiter("\\A").next();
    return fixture;
  }

  // Helper function to stream fixtures from test package resources
  public static InputStream streamFixture(String fixtureName) throws IOException {
    InputStream ins = TestUtils.class.getResourceAsStream("/fixtures/" + fixtureName);
    return ins;
  }

}
