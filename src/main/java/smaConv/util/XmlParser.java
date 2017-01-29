package smaConv.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class for parsing and getting valuef from xml file.
 */
public class XmlParser {
  /**
   * Return String value from xml file.
   *
   * @param supermemoXmlFile
   *          - content of xml file to parse.
   * @param expression
   *          - XPath expression for value to get.
   * @return Value as String from xml file or empty String if no value can be find for given XPath
   *         expression.
   */
  public String getValue(byte[] supermemoXmlFile, String expression) {
    ArrayList<String> list = getNodeList(supermemoXmlFile, expression);
    if (list.isEmpty()) {
      return "";
    } else {
      return list.get(0);
    }
  }

  /**
   * Obtains values as ArrayList&lt;String&gt; from xml file.
   *
   * @param supermemoXmlFile
   *          - content of file to parse.
   * @param expression
   *          - XPath expression for values to obtain.
   * @return Values as ArrayList&lt;String&gt; or empty ArrayList&lt;String&gt; if no values can be
   *         find for given XPath expression.
   */
  public ArrayList<String> getValues(byte[] supermemoXmlFile, String expression) {
    return getNodeList(supermemoXmlFile, expression);
  }

  private ArrayList<String> getNodeList(byte[] supermemoXmlFile, String expression) {
    try (ByteArrayInputStream bis = new ByteArrayInputStream(supermemoXmlFile)) {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(bis);
      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();
      XPathExpression expr = xpath.compile(expression);
      NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
      ArrayList<String> values = new ArrayList<>();
      for (int i = 0; i < nodeList.getLength(); i++) {
        values.add(nodeList.item(i).getNodeValue());
      }
      return values;
    } catch (SAXException | IOException | ParserConfigurationException | XPathException exception) {
      exception.printStackTrace();
      return new ArrayList<>();
    }
  }
}
