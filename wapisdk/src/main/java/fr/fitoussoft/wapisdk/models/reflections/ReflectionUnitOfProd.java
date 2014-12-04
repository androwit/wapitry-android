package fr.fitoussoft.wapisdk.models.reflections;

import com.fasterxml.jackson.annotation.JsonTypeName;

import fr.fitoussoft.wapisdk.models.reflections.items.ReflectionItemBase;

/**
 * Created by emmanuel.fitoussi on 15/10/2014.
 */
@JsonTypeName("ReflectionUnitOfProd")
public class ReflectionUnitOfProd extends Reflection {

    private ReflectionItemBase sequence;

    private ReflectionItemBase capacityPower;

    private ReflectionItemBase runTimeMin;

    private ReflectionItemBase preheatTime;

    private ReflectionItemBase instantPower;

    private ReflectionItemBase coupling;

    public ReflectionItemBase getSequence() {
        return sequence;
    }

    public ReflectionItemBase getCapacityPower() {
        return capacityPower;
    }

    public ReflectionItemBase getRunTimeMin() {
        return runTimeMin;
    }

    public ReflectionItemBase getPreheatTime() {
        return preheatTime;
    }

    public ReflectionItemBase getInstantPower() {
        return instantPower;
    }

    public ReflectionItemBase getCoupling() {
        return coupling;
    }
}
