/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package interactivespaces.util.data.json;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Maps;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * Test the {@link JsonMapper}.
 *
 * @author Keith M. Hughes
 */
public class JsonMapperTest {

  private static final String TEST_JSON_I18N = "{\"foo\":\"\\u0164\\u0117\\u015F\\u0167\"}";

  /**
   * A Unicode string to make sure we encode correctly.
   */
  public static final String TEST_VALUE_I18N = "\u0164\u0117\u015F\u0167";

  private JsonMapper mapper;

  @Before
  public void setup() {
    mapper = new JsonMapper();
  }

  /**
   * Ensure that non 7 bit ASCII characters are properly escaped.
   */
  @Test
  public void testI18nToString() {
    String key = "foo";
    String i18n = TEST_VALUE_I18N;
    Map<String, Object> object = Maps.newHashMap();
    object.put(key, i18n);

    String json = mapper.toString(object);

    assertEquals(TEST_JSON_I18N, json);
  }

  /**
   * Ensure that non 7 bit ASCII characters are properly parsed.
   */
  @Test
  public void testI18nToObject() {
    String key = "foo";
    String i18n = TEST_VALUE_I18N;
    Map<String, Object> object = mapper.parseObject(TEST_JSON_I18N);

    assertEquals(i18n, object.get(key));
  }
}
