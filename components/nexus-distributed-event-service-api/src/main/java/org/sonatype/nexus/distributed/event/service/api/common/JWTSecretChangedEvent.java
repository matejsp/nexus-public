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
package org.sonatype.nexus.distributed.event.service.api.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.distributed.event.service.api.EventType.UPDATED;

/**
 * Indicates that a 'JWT secret' has been changed.
 *
 * @since 3.next
 */
public class JWTSecretChangedEvent
    extends DistributedEventSupport
{
  public static final String NAME = "JWTSecretChangedEvent";

  private final String nodeId;

  @JsonCreator
  public JWTSecretChangedEvent(
      @JsonProperty("nodeId") final String nodeId)
  {
    super(UPDATED);
    this.nodeId = checkNotNull(nodeId);
  }

  public String getNodeId() {
    return nodeId;
  }

  @Override
  public String toString() {
    return "JWTSecretChangedEvent{" +
        "nodeId='" + nodeId + '\'' +
        '}';
  }
}
