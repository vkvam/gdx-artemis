package com.artemis.managers;

import org.junit.Assert;
import org.junit.Test;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.World;
import com.artemis.systems.EntityProcessingSystem;

public class ComponentManagerTest {

    static class ComponentA implements Component {
        @Override
        public void reset() {
        }
    }

    static class ComponentB implements Component {
        @Override
        public void reset() {
        }
    }

    static class ComponentC implements Component {
        @Override
        public void reset() {
        }
    }

    static class SystemA extends EntityProcessingSystem {
        ComponentMapper<ComponentA> aMapper;

        @SuppressWarnings("unchecked")
        public SystemA() {
            super(Filter.allComponents(ComponentA.class));
        }

        @Override
        public void initialize() {
            aMapper = world.getMapper(ComponentA.class);
        }

        @Override
        protected void process(Entity e) {
            Assert.assertNotNull(aMapper.get(e));
            e.removeComponent(ComponentA.class);
        }
    }

    static class SystemB extends EntityProcessingSystem {
        ComponentMapper<ComponentA> aMapper;

        @SuppressWarnings("unchecked")
        public SystemB() {
            super(Filter.allComponents(ComponentA.class));
        }

        @Override
        public void initialize() {
            aMapper = world.getMapper(ComponentA.class);
        }

        @Override
        protected void process(Entity e) {
            Assert.assertNotNull(aMapper.get(e));
        }
    }


    @Test
    public void testRemoveComponent() {
        World world = new World();
        world.setSystem(new SystemA());
        world.setSystem(new SystemB());
        world.initialize();

        Entity e = world.createEntity();
        ComponentA c = world.createComponent(ComponentA.class);
        e.addComponent(c);
        world.addEntity(e);

        world.process();
        world.process();
    }

    @Test
    public void testGetComponents() {
        World world = new World();
        world.initialize();

        Entity e = world.createEntity();
        ComponentA a = world.createComponent(ComponentA.class);
        e.addComponent(a);
        ComponentB b = world.createComponent(ComponentB.class);
        e.addComponent(b);
        world.addEntity(e);

        ComponentC c = world.createComponent(ComponentC.class);

        world.process();

        ComponentManager manager = world.getManager(ComponentManager.class);
        Assert.assertEquals(2, manager.getComponents(e).size);
        Assert.assertTrue(manager.getComponents(e).contains(a, true));
        Assert.assertTrue(manager.getComponents(e).contains(b, true));
        Assert.assertFalse(manager.getComponents(e).contains(c, true));
    }
}
