package fr.fitoussoft.wapisdk.models.reflections;

import com.fasterxml.jackson.annotation.JsonTypeName;

import fr.fitoussoft.wapisdk.models.reflections.items.ReflectionItemBase;

/**
 * Created by emmanuel.fitoussi on 15/10/2014.
 */
@JsonTypeName("ReflectionPlanning")
public class ReflectionPlanning extends Reflection {
    private ReflectionItemBase planning;
    private ReflectionItemBase index;

    public ReflectionItemBase getPlanning() {
        return planning;
    }

    public ReflectionItemBase getIndex() {
        return index;
    }
}
