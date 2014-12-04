package fr.fitoussoft.wapisdk.models.containers;

import java.util.List;

import fr.fitoussoft.wapisdk.models.Model;
import fr.fitoussoft.wapisdk.models.reflections.Reflection;

/**
 * Created by emmanuel.fitoussi on 03/12/2014.
 * [OK]
 */
public class ReflectionContainer extends Model {
    private Number count;

    private List<Reflection> reflections;

    public Number getCount() {
        return count;
    }

    public List<Reflection> getReflections() {
        return reflections;
    }
}