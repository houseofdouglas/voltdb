/* This file is part of VoltDB.
 * Copyright (C) 2008-2014 VoltDB Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with VoltDB.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.voltdb;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.zookeeper_voltpatches.KeeperException;
import org.voltdb.iv2.UniqueIdGenerator;


// Interface through which the outside world can interact with the replica-side
// of DR. Currently, there's not much to do here, since the subsystem is
// largely self-contained
public interface ReplicaDRGateway extends Promotable {

    public abstract void updateCatalog(CatalogContext catalog);

    public abstract boolean isActive();

    public abstract void initialize(boolean resumeReplication);

    public abstract void shutdown(boolean blocking) throws InterruptedException;

    public abstract void promotePartition(int partitionId, long maxUniqueId);

    public abstract void notifyOfLastAppliedSpUniqueId(int dataCenter, long spUniqueId);

    public abstract Map<Integer, Map<Integer, Long>> getLastReceivedUniqueIds();

    public static class DummyReplicaDRGateway implements ReplicaDRGateway {
        Map<Integer, Map<Integer, Long>> ids = new HashMap<Integer, Map<Integer, Long>>();

        @Override
        public void initialize(boolean resumeReplication) {}

        @Override
        public void acceptPromotion() throws InterruptedException,
                ExecutionException, KeeperException {}

        @Override
        public void updateCatalog(CatalogContext catalog) {}

        @Override
        public boolean isActive() { return false; }

        @Override
        public void shutdown(boolean blocking) {}

        @Override
        public void promotePartition(int partitionId, long maxUniqueId) {}

        @Override
        public void notifyOfLastAppliedSpUniqueId(int dataCenter, long spUniqueId) {
            if (!ids.containsKey(dataCenter)) {
                ids.put(dataCenter, new HashMap<Integer, Long>());
            }
            ids.get(dataCenter).put(UniqueIdGenerator.getPartitionIdFromUniqueId(spUniqueId), spUniqueId);
        };

        @Override
        public Map<Integer, Map<Integer, Long>> getLastReceivedUniqueIds() { return ids; }

    }
}
