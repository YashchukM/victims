package org.myas.victims.search.index.id;

import org.myas.victims.core.domain.UnrecognizedRecord;

/**
 * Created by Mykhailo Yashchuk on 11.04.2017.
 */
public class UnrecognizedIdGenerator implements IdGenerator<UnrecognizedRecord> {
    @Override
    public String generate(UnrecognizedRecord element) {
        return null;
    }
}
