package by.tade.taxi.entity.repository;

import javassist.NotFoundException;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Base Repository for {@code Identifiable<?>} entities.
 *
 * @param <E> entity class
 */
@NoRepositoryBean
public interface IdentifiableRepository<E, I> extends BaseRepository<E, I> {

}