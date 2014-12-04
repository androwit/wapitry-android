package fr.fitoussoft.wapisdk.models.reflections;

import com.fasterxml.jackson.annotation.JsonTypeName;

import fr.fitoussoft.wapisdk.models.reflections.items.ReflectionItemBase;

/**
 * Created by emmanuel.fitoussi on 15/10/2014.
 */
@JsonTypeName("ReflectionDigital")
public class ReflectionDigital extends Reflection {
    private ReflectionItemBase value;

    public ReflectionItemBase getValue() {
        return value;
    }
}
