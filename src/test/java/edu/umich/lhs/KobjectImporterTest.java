package edu.umich.lhs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
  public void smoke() throws Exception {
    InputStream stream = TestUtils.streamFixture("kobject-sample.xml");
    Model model = KobjectImporter.streamToModel(stream);

    model.write(System.out);
    //RDFDataMgr.write(System.out, model, RDFFormat.JSONLD_PRETTY);
  }

  @Test
  public void jsonOutput() throws Exception {
    InputStream stream = TestUtils.streamFixture("kobject-sample.xml");
    Model model = KobjectImporter.streamToModel(stream);

    RDFDataMgr.write(System.out, model, RDFFormat.JSONLD_PRETTY);
  }

  @Test
  public void stringSummary() throws Exception {
    InputStream stream = TestUtils.streamFixture("kobject-sample.xml");
    Model model = KobjectImporter.streamToModel(stream);

    String s = KobjectImporter.summarize(model);
    System.out.println(s);
  }

  @Test
  public void roundTrip() throws Exception {
    InputStream stream = TestUtils.streamFixture("kobject-sample.xml");
    Model modelXml = KobjectImporter.streamToModel(stream);

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
    System.out.println("Result: "+res);
  }
}
