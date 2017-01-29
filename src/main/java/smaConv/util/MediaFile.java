package smaConv.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MediaFile {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ObjectNode objectNode = objectMapper.createObjectNode();

  /**
   * Insert {key:value} field in Json format to file "media".
   *
   * @param key
   *          - unique integer number as String equals to new name of added media file
   * @param value
   *          - original media file name
   */
  public void addMediaFile(String key, String value) {
    objectNode.put(key, value);
  }

  /**
   * Get content of "media" file.
   *
   * @return content of "media" file as byte array
   */
  public byte[] getMediaFile() {
    try {
      return objectMapper.writeValueAsBytes(objectNode);
    } catch (JsonProcessingException exception) {
      throw new RuntimeException("Error at getting media file.");
    }
  }
}
