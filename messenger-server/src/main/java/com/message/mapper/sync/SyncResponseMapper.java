package com.message.mapper.sync;

import java.util.List;

public interface SyncResponseMapper<T> {
    String toSyncResponse(List<T> list);
}
