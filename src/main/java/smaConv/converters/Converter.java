package smaConv.converters;

import java.util.ArrayList;
import java.util.List;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.XmlParser;

public abstract class Converter {
  private final XmlParser xmlParser = new XmlParser();

  public abstract Deck<AnkiCard> makeDeck(Parser smpakParser);

  protected List<String> filesToConvert(byte[] courseXml, String expression) {
    List<String> xmlFileList = new ArrayList<>();
    for (String item : xmlParser.getValues(courseXml, expression)) {
      String fileName = "item" + ("00000" + item).substring(item.length()) + ".xml";
      xmlFileList.add(fileName);
    }
    return xmlFileList;
  }

  /**
   * Converts "element id" (from course.xml) to sound file belongings to particular xml exercise
   * file.
   *
   * @param xmlFileName
   *          - name of converter xml file
   * @param soundLetter
   *          - (usually "a" or "q") - to which part, question or answer, sound file belongs to. For
   *          example letter "a": <br>
   *          {@code<part name="example" type="sfx_id">a</part>}
   * @return sound file name
   */
  protected String soundField(String xmlFileName, String soundLetter) {
    String soundFile = xmlFileName.substring(4, 9) + soundLetter + ".mp3";
    return "[sound:" + soundFile + "]";
  }
}
