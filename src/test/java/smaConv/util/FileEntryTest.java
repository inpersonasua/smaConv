package smaConv.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import smaConv.util.FileEntry;

public class FileEntryTest {
  private FileEntry uncompressed;
  private FileEntry compressed;

  @Before
  public void setUp() throws Exception {
    uncompressed = new FileEntry();
    uncompressed.setCompression((short) 0);

    compressed = new FileEntry();
    compressed.setCompression((short) 1);
  }

  @Test
  public void testIsCompressed() {
    assertTrue(compressed.isCompressed());
    assertFalse(uncompressed.isCompressed());
  }

}
