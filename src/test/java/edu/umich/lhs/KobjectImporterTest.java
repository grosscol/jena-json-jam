package edu.umich.lhs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by grosscol on 2017-08-31.
 */
public class KobjectImporterTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setup() {


  }

  @Test
  public void modelFromXML() throws Exception {
    InputStream stream = TestUtils.streamFixture("kobject-sample.xml");
    Model model = KobjectImporter.xmlToModel(stream);

    System.out.print(KobjectImporter.summarize(model));
  }

  @Test
  public void modelFromJSON() throws Exception {
    InputStream stream = TestUtils.streamFixture("kobject-sample.json");
    Model model = KobjectImporter.jsonToModel(stream);

    System.out.print(KobjectImporter.summarize(model));
  }

  @Test
  public void stringSummary() throws Exception {
    InputStream stream = TestUtils.streamFixture("kobject-sample.xml");
    Model model = KobjectImporter.xmlToModel(stream);

    String s = KobjectImporter.summarize(model);
    System.out.println(s);
  }

  @Test
  public void exquivalence() throws Exception{
    InputStream stream;

    stream = TestUtils.streamFixture("kobject-sample.xml");
    Model xModel = KobjectImporter.xmlToModel(stream);
    stream.close();

    stream = TestUtils.streamFixture("kobject-sample.json");
    Model jModel = KobjectImporter.jsonToModel(stream);
    stream.close();

    MessageDigest xDig = MessageDigest.getInstance("MD5");
    MessageDigest jDig = MessageDigest.getInstance("MD5");

    String xSummary = KobjectImporter.summarize(xModel);
    String jSummary = KobjectImporter.summarize(jModel);

    xDig.update(xSummary.getBytes());
    jDig.update(jSummary.getBytes());

    boolean res;
    res = MessageDigest.isEqual(jDig.digest(), xDig.digest());
    System.out.println("Equivalence Result: "+res);
  }

  @Test
  public void roundTrip() throws Exception {
    InputStream stream = TestUtils.streamFixture("kobject-sample.xml");
    Model modelXml = KobjectImporter.xmlToModel(stream);

    ByteArrayOutputStream outs = new ByteArrayOutputStream();
    RDFDataMgr.write(outs, modelXml, RDFFormat.JSONLD_PRETTY);

    InputStream ins = new ByteArrayInputStream(outs.toByteArray());
    Model modelJson = KobjectImporter.jsonToModel(ins);

    MessageDigest digX = MessageDigest.getInstance("MD5");
    MessageDigest digJ = MessageDigest.getInstance("MD5");

    outs.close();
    outs = new ByteArrayOutputStream();
    modelXml.write(outs);
    digX.update(outs.toByteArray());

    outs.close();
    outs = new ByteArrayOutputStream();
    modelJson.write(outs);
    digJ.update(outs.toByteArray());

    modelXml.write(System.out);
    modelJson.write(System.out);

    boolean res;
    res = MessageDigest.isEqual(digJ.digest(), digX.digest());
    System.out.println("Round Trip Result: "+res);
  }
}
