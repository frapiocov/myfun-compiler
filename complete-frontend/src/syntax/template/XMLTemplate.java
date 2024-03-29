package syntax.template;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.Optional;

public class XMLTemplate implements Template<Document> {

  @Override
  public void write(String path, Document model) {
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      DOMSource source = new DOMSource(model);
      StreamResult result = new StreamResult(path);
      transformer.transform(source, result);
    } catch (TransformerConfigurationException ex) {
      System.err.println("Check configuration for generate XML");
    } catch (TransformerException ex) {
      System.err.println("Error during transforming");
    }
  }

  @Override
  public Optional<Document> create() {
    Document doc = null;
    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      doc = docBuilder.newDocument();
    } catch (ParserConfigurationException e) {
      System.err.println("Error creating XML document" + e);
    }
    return Optional.ofNullable(doc);
  }
}
