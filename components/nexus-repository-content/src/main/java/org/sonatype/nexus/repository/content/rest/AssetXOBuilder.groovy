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
package org.sonatype.nexus.repository.content.rest

import javax.annotation.Nullable

import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.content.Asset
import org.sonatype.nexus.repository.content.AssetBlob
import org.sonatype.nexus.repository.rest.api.AssetXO
import org.sonatype.nexus.repository.rest.api.AssetXODescriptor
import org.sonatype.nexus.repository.rest.api.RepositoryItemIDXO

import static org.sonatype.nexus.repository.content.store.InternalIds.internalAssetId
import static org.sonatype.nexus.repository.content.store.InternalIds.toExternalId

/**
 * Builds asset transfer objects for REST APIs.
 *
 * @since 3.26
 */
class AssetXOBuilder
{
  static AssetXO fromAsset(final Asset asset, final Repository repository,
                           final Map<String, AssetXODescriptor> assetDescriptors)
  {
    String externalId = toExternalId(internalAssetId(asset)).value

    Optional<AssetBlob> assetBlob = asset.blob()

    Map checksum = assetBlob.map({ blob -> blob.checksums() }).orElse([:])

    String contentType = assetBlob.map({ blob -> blob.contentType() }).orElse(null)
    String format = repository.format.value

    String uploader = assetBlob.flatMap( {blob -> blob.createdBy()}).orElse(null)
    String uploaderIp = assetBlob.flatMap( {blob -> blob.createdByIp()}).orElse(null)
    long fileSize = assetBlob.map({ blob -> blob.blobSize()}).orElse(0)

    return AssetXO.builder()
        .path(asset.path())
        .downloadUrl(repository.url + asset.path())
        .id(new RepositoryItemIDXO(repository.name, externalId).value)
        .repository(repository.name)
        .checksum(checksum)
        .format(format)
        .contentType(contentType)
        .lastModified(calculateLastModified(asset))
        .attributes(getExpandedAttributes(asset, format, assetDescriptors))
        .uploader(uploader)
        .uploaderIp(uploaderIp)
        .fileSize(fileSize)
        .build()
  }

  private static Date calculateLastModified(final Asset asset) {
    Date lastModified = null
    if (asset.lastUpdated()) {
      lastModified = Date.from(asset.lastUpdated().toInstant())
    }
    else if (asset.created()) {
      lastModified = Date.from(asset.created().toInstant())
    }

    return lastModified
  }

  private static Map getExpandedAttributes(final Asset asset, final String format,
                                           @Nullable final Map<String, AssetXODescriptor> assetDescriptors)
  {
    Set<String> exposedAttributeKeys = assetDescriptors?.get(format)?.listExposedAttributeKeys()
    Map expanded = [:]
    if (exposedAttributeKeys) {
      Map exposedAttributes = (asset.attributes(format)?.backing() as Map<String, Object>)?.subMap(exposedAttributeKeys)
      if (exposedAttributes) {
        expanded.put(format, exposedAttributes)
      }
    }

    return expanded
  }
}
