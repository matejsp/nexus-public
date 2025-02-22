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
package org.sonatype.nexus.blobstore.metrics;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.sonatype.nexus.common.guice.AbstractInterceptorModule;

import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;

/**
 * Registers the {@link MonitoringBlobStoreMetrics} behaviour.
 *
 * @since 3.next
 */
public class BlobStoreModule
    extends AbstractInterceptorModule
{
  @Override
  protected void configure() {
    bindInterceptor(Matchers.any(), new TransactionalMatcher(), new BlobStoreAnalyticsInterceptor());
  }

  private static final class TransactionalMatcher
      extends AbstractMatcher<Method>
  {
    @Override
    public boolean matches(final Method method) {
      if (method.isAnnotationPresent(MonitoringBlobStoreMetrics.class)) {
        return true;
      }
      // look for stereotypes; annotations marked with @MonitoringBlobStoreMetrics
      for (Annotation annotation : method.getDeclaredAnnotations()) {
        if (annotation.annotationType().isAnnotationPresent(MonitoringBlobStoreMetrics.class)) {
          return true;
        }
      }
      return false;
    }
  }
}
