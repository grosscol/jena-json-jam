package edu.umich.lhs;

import java.io.InputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

/**
 * Created by grosscol on 2017-08-24.
 */
public class KobjectImporter {

  public static final String KGRID_NS = "http://lhs.umich.edu/kgrid#";
  public static final String KNOWLEDDGE_OBJECT_URI = KGRID_NS+"knowledgeObject";
  public static final String HAS_INPUT_MESSAGE_URI = KGRID_NS+"hasInputMessage";
  public static final String HAS_OUTPUT_MESSAGE_URI = KGRID_NS+"hasOutputMessage";
  public static final String HAS_PAYLOAD_URI = KGRID_NS+"hasPayload";

  public static final String NOOFPARAMS_URI = KGRID_NS+"noofparams";
  public static final String HASPARAMS_URI = KGRID_NS+"hasParams";
  public static final String PARAMNAME_URI = KGRID_NS+"paramname";
  public static final String DATATYPE_URI = KGRID_NS+"datatype";

  public static final String RETURNTYPE_URI = KGRID_NS+"returnType";

  public static final String FUNCTION_NAME_URI = KGRID_NS+"functionName";
  public static final String CONTENT_URI = KGRID_NS+"content";
  public static final String ENGINE_TYPE_URI = KGRID_NS+"engineType";

  public static final String IDENTIFIER_URI = "http://schema.org/identifier";

  static Model xmlToModel( InputStream ins){
    Model model = ModelFactory.createDefaultModel();
    model.read(ins, null);

    return model;
  }

  static Model jsonToModel( InputStream ins){
    Model model = ModelFactory.createDefaultModel();
    model.read(ins, "http://example.com", "JSON-LD");

    return model;
  }


  static String summarize(Model model){
    String result = "";

    Resource kobjectRDFClass = model.createResource(KNOWLEDDGE_OBJECT_URI);
    Property inpMsgProp = ResourceFactory.createProperty(HAS_INPUT_MESSAGE_URI);
    Property outMsgProp = ResourceFactory.createProperty(HAS_OUTPUT_MESSAGE_URI);
    Property payloadProp = ResourceFactory.createProperty(HAS_PAYLOAD_URI);
    Property identifierProp = ResourceFactory.createProperty(IDENTIFIER_URI);

    ResIterator itt = model.listResourcesWithProperty(RDF.type, kobjectRDFClass);

    if(itt.hasNext()){
      Resource kobject = itt.nextResource();
      result += "Knowledge Object: " + kobject.getURI() + "\n";
      result += "Identifier: " + kobject.getProperty(identifierProp).getString() + "\n";

      Resource inpMsg = kobject.getPropertyResourceValue(inpMsgProp);
      result += summarizeInputMessage(inpMsg);

      Resource outMsg = kobject.getPropertyResourceValue(outMsgProp);
      result += summarizeOutputMessage(outMsg);

      Resource payload = kobject.getPropertyResourceValue(payloadProp);
      result += summarizePayload(payload);
    }
    return result;
  }

  static String summarizePayload(Resource res){
    String result = "\n--Payload--\n";

    Property funcNameProp = ResourceFactory.createProperty(FUNCTION_NAME_URI);
    Property engineTypeProp = ResourceFactory.createProperty(ENGINE_TYPE_URI);
    Property contentProp = ResourceFactory.createProperty(CONTENT_URI);

    String funcName = res.getProperty(funcNameProp).getString();
    String engineType = res.getProperty(engineTypeProp).getString();
    String content = res.getProperty(contentProp).getString();

    result += "Function Name: "+ funcName + "\n";
    result += "Engine Type: "+ engineType + "\n";
    result += "Content: \n" + content +"\n";

    return result;
  }

  static String summarizeOutputMessage(Resource res){
    String result = "\n--OutputMessage--\n";

    Property retTypeProp = ResourceFactory.createProperty(RETURNTYPE_URI);

    String retType = res.getProperty(retTypeProp).getString();
    result += "Return Type: "+ retType + "\n";

    return result;
  }

  static String summarizeInputMessage(Resource res){
    String result = "\n--InputMessage--\n";

    Property noOfParamsProp = ResourceFactory.createProperty(NOOFPARAMS_URI);
    Property hasParams = ResourceFactory.createProperty(HASPARAMS_URI);

    Integer numParams = res.getProperty(noOfParamsProp).getInt();
    result += "Number of params: "+ numParams.toString() + "\n";

    NodeIterator itt = res.getProperty(hasParams).getSeq().iterator();
    while(itt.hasNext()){
      RDFNode node = itt.next();
      if(node.isResource()){ result += summarizeParameter(node.asResource());}
    }

    return result;
  }

  static String summarizeParameter(Resource res){
    String result = "";
    Property paramnameProp = ResourceFactory.createProperty(PARAMNAME_URI);
    Property datatypeProp = ResourceFactory.createProperty(DATATYPE_URI);

    result += res.getProperty(paramnameProp).getString() + " : ";
    result += res.getProperty(datatypeProp).getString() + "\n";

    return result;
  }


}
