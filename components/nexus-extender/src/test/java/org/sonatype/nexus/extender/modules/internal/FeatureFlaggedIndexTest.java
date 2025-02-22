/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.extender.modules.internal;

import org.sonatype.goodies.testsupport.TestSupport;
import org.sonatype.nexus.common.app.FeatureFlag;
import org.sonatype.nexus.extender.modules.FeatureFlaggedIndex;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.osgi.framework.Bundle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

public class FeatureFlaggedIndexTest
    extends TestSupport
{
  private static final String FLAG_1 = "FeatureFlaggedIndexTest_1";

  private static final String FLAG_2 = "FeatureFlaggedIndexTest_2";

  @Mock
  Bundle mockBundle;

  @FeatureFlag(name = FLAG_1)
  @FeatureFlag(name = FLAG_2)
  @SuppressWarnings("InnerClassMayBeStatic")
  private final class TestClass
  {
  }

  @Before
  public void setup() throws ClassNotFoundException {
    doReturn(TestClass.class).when(mockBundle).loadClass(anyString());
    System.clearProperty(FLAG_1);
    System.clearProperty(FLAG_2);
    assertThat(System.getProperty(FLAG_1), is((String) null));
    assertThat(System.getProperty(FLAG_2), is((String) null));
  }

  @After
  public void teardown() {
    System.clearProperty(FLAG_1);
    System.clearProperty(FLAG_2);
  }

  @Test
  public void testNoFlagsEnabled() {
    assertThat(FeatureFlaggedIndex.isFeatureFlagDisabled(mockBundle, anyString()), is(true));
  }

  @Test
  public void testPartialFlagsEnabled() {
    System.setProperty(FLAG_1, Boolean.toString(true));
    assertThat(FeatureFlaggedIndex.isFeatureFlagDisabled(mockBundle, anyString()), is(true));

    System.clearProperty(FLAG_1);
    System.setProperty(FLAG_2, Boolean.toString(true));
    assertThat(FeatureFlaggedIndex.isFeatureFlagDisabled(mockBundle, anyString()), is(true));
  }

  @Test
  public void testAllFlagsEnabled() {
    System.setProperty(FLAG_1, Boolean.toString(true));
    System.setProperty(FLAG_2, Boolean.toString(true));
    assertThat(FeatureFlaggedIndex.isFeatureFlagDisabled(mockBundle, anyString()), is(false));
  }
}
