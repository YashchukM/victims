package org.myas.victims.search.index.id;

import org.elasticsearch.common.UUIDs;
import org.myas.victims.core.domain.Victim;

/**
 * Created by Mykhailo Yashchuk on 11.04.2017.
 */
public class VictimIdGenerator implements IdGenerator<Victim> {
    @Override
    public String generate(Victim element) {
        return UUIDs.base64UUID();
    }
}
