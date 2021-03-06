package com.artemis.fsm;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import java.lang.reflect.ParameterizedType;

/**
 * Provider of components for {@link EntityState states}. ComponentProviders are used to
 * store componentValues when components are removed from the world and restore when the
 * component is re-added to the world, as well as differentiating between components shared
 * by states and components individual to each state.
 *
 * onAdd(),onRemove() and onProviderInit() may be used in any was the user sees fit, but
 * the most common use-case for most will be storing and restoring component values.
 *
 * 1. For each individual component that may exist on an entity, there should be one provider.
 * 2. If a provider instance is shared by two states, components will not be added or
 * removed from the entity on stateChange between these two state.
 * 3. If two states have providers of same component type, but different provider instances,
 * the component will be swapped, triggering an entity change.
 *
 * @author Vemund Kvam on 15/06/14.
 */
public abstract class ComponentProvider<T extends Component> implements Pool.Poolable {
    protected T lastComponentProduced;
    protected Class<T> componentClass;
    protected int classIndex=-1;
    protected int instanceIndex=-1;
    protected boolean addedStateMachine = false;
    protected Entity entity;

    public ComponentProvider() {
        ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
        componentClass = (Class<T>) superclass.getActualTypeArguments()[0];
    }

    /**
     * Called before the component is removed from associated {@link com.artemis.Entity Entity}.
     *
     * Example usage: Save the component values until next time a component is created.
     * @param component the removed component.
     */
    protected abstract void onRemove(T component);

    /**
     * Called before the component is added to the entity.
     *
     * Example usage: Restore component values before adding them to the entity.
     * @param component the added component
     */
    protected abstract void onAdd(T component);

    /**
     * Called when the componentProvider is added to EntityStateMachine through EntityState
     * for the first time.
     *
     * Example usage: Set all default values to be used by component.
     */
    protected abstract void onProviderInit();


    protected void setEntity(Entity entity){
        this.entity = entity;
    }

    protected void setIndices(int classIndex, int instanceIndex){
        this.classIndex=classIndex;
        this.instanceIndex=instanceIndex;
        addedStateMachine = true;
    }

    protected T createComponent(){
        T component = Pools.obtain(componentClass);
        lastComponentProduced = component;
        onAdd(component);
        return component;
    }
    protected void removedFromEntity(){
        T component = entity.getComponent(componentClass);
        if(component==lastComponentProduced){
            onRemove(component);
        }
    }

    @Override
    public void reset() {
        lastComponentProduced = null;
        classIndex = -1;
        instanceIndex = -1;
        addedStateMachine =false;
        entity=null;
    }
}
