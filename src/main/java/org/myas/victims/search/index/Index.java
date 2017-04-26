package org.myas.victims.search.index;

import java.util.List;

/**
 * Created by Mykhailo Yashchuk on 10.04.2017.
 */
public interface Index<E> {
    void index(E element);

    void index(List<? extends E> elements);

    void delete(E element);

    void delete(List<? extends E> elements);
}