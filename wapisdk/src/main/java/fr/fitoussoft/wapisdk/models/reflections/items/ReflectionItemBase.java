package fr.fitoussoft.wapisdk.models.reflections.items;

import fr.fitoussoft.wapisdk.models.Model;

/**
 * Created by emmanuel.fitoussi on 03/12/2014.
 * TODO: ReflectionItemAnalogic,  ReflectionItemDigital,  ReflectionItemText,  ReflectionItemList,
 */
public class ReflectionItemBase extends Model {
    private int reflectionID;
    private String direction;

    public int getReflectionID() {
        return reflectionID;
    }

    public String getDirection() {
        return direction;
    }
}
