package fr.fitoussoft.wapisdk.models.reflections;

import com.fasterxml.jackson.annotation.JsonTypeName;

import fr.fitoussoft.wapisdk.models.reflections.items.ReflectionItemBase;

/**
 * Created by emmanuel.fitoussi on 15/10/2014.
 */
@JsonTypeName("ReflectionSystem")
public class ReflectionSystem extends Reflection {
    private ReflectionItemBase freeFlashSize;
    private ReflectionItemBase freeRAMSize;
    private ReflectionItemBase connectedExt;

    public ReflectionItemBase getFreeFlashSize() {
        return freeFlashSize;
    }

    public ReflectionItemBase getFreeRAMSize() {
        return freeRAMSize;
    }

    public ReflectionItemBase getConnectedExt() {
        return connectedExt;
    }
}
