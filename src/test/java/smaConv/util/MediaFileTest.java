package smaConv.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import smaConv.util.MediaFile;

public class MediaFileTest {

  private MediaFile mediaFile;
  private final String jsonString = "{\"k\":\"v\"}";

  @Before
  public void setUp() {
    mediaFile = new MediaFile();
  }

  @Test
  public void shouldReturnEmptyByteArrayWhenNothingWasAdded() {
    assertThat(mediaFile.getMediaFile()).isEqualTo("{}".getBytes());
  }

  @Test
  public void stringsShouldBeEquals() {
    mediaFile.addMediaFile("k", "v");
    assertTrue("json strings are not equal",
        jsonString.equals(new String(mediaFile.getMediaFile())));
  }

}
