/*
 * Copyright 2010-2013 Ning, Inc.
 * Copyright 2014-2017 Groupon, Inc
 * Copyright 2014-2017 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.util.tag.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.callcontext.InternalCallContext;
import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.util.api.TagApiException;
import org.killbill.billing.util.entity.Pagination;
import org.killbill.billing.util.entity.dao.MockEntityDaoBase;
import org.killbill.billing.util.tag.Tag;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

public class MockTagDao extends MockEntityDaoBase<TagModelDao, Tag, TagApiException> implements TagDao {

    private final Map<UUID, List<TagModelDao>> tagStore = new HashMap<UUID, List<TagModelDao>>();

    @Override
    public void create(final TagModelDao tag, final InternalCallContext context) throws TagApiException {
        if (tagStore.get(tag.getObjectId()) == null) {
            tagStore.put(tag.getObjectId(), new ArrayList<TagModelDao>());
        }
        tagStore.get(tag.getObjectId()).add(tag);
    }

    @Override
    public void create(final TagModelDao entity, final boolean sendEvent, final boolean ignoreDuplicate, final InternalCallContext context) throws TagApiException {
        create(entity, context);
    }

    @Override
    public void deleteTag(final UUID objectId, final ObjectType objectType,
                          final UUID tagDefinitionId, final boolean sendEvent, final InternalCallContext context) {
        final List<TagModelDao> tags = tagStore.get(objectId);
        if (tags != null) {
            final Iterator<TagModelDao> tagIterator = tags.iterator();
            while (tagIterator.hasNext()) {
                final TagModelDao tag = tagIterator.next();
                if (tag.getTagDefinitionId().equals(tagDefinitionId)) {
                    tagIterator.remove();
                }
            }
        }
    }

    @Override
    public Pagination<TagModelDao> searchTags(final String searchKey, final Long offset, final Long limit, final InternalTenantContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TagModelDao getById(final UUID tagId, final InternalTenantContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TagModelDao> getTagsForObject(final UUID objectId, final ObjectType objectType, final boolean includedDeleted, final InternalTenantContext internalTenantContext) {
        if (tagStore.get(objectId) == null) {
            return ImmutableList.<TagModelDao>of();
        }

        return ImmutableList.<TagModelDao>copyOf(Collections2.filter(tagStore.get(objectId), new Predicate<TagModelDao>() {
            @Override
            public boolean apply(final TagModelDao input) {
                return objectType.equals(input.getObjectType());
            }
        }));
    }

    @Override
    public List<TagModelDao> getTagsForAccountType(final ObjectType objectType, final boolean includedDeleted, final InternalTenantContext internalTenantContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TagModelDao> getTagsForAccount(final boolean includedDeleted, final InternalTenantContext internalTenantContext) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        tagStore.clear();
    }
}
