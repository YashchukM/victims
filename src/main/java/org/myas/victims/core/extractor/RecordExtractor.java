package org.myas.victims.core.extractor;

import java.io.IOException;

/**
 * Created by Mykhailo Yashchuk on 08.03.2017.
 */
public interface RecordExtractor {
    void extract(int startPage, int endPage) throws IOException;
}
