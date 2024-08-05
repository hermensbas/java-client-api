/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.example.cookbook;

import com.marklogic.client.example.cookbook.RawCombinedSearch;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class RawCombinedSearchTest {
  @Test
  public void testMain() {
    boolean succeeded = false;
    try {
      RawCombinedSearch.main(new String[0]);
      succeeded = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertTrue( succeeded);
  }
}
