package pl.softwaremill.common.dbtest;

/**
 * @author Maciej Bilas
 * @since 15/12/11 12:45
 */
public interface EntityDAO {
    Entity persist(Entity entity);

    Entity reload(Entity entity);
}
