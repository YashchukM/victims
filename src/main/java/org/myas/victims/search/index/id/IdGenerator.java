package org.myas.victims.search.index.id;

/**
 * Created by Mykhailo Yashchuk on 11.04.2017.
 */
public interface IdGenerator<E> {
    String generate(E element);
}
