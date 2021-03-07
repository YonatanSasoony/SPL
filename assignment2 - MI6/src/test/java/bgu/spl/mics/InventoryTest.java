package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {
    private Inventory inventory;

    @BeforeEach
    public void setUp(){
        inventory = Inventory.getInstance();
    }

    @Test
      public void testGetItem(){
      String[] gadgets = {"Shoe","Tavor","Broom"};
      inventory.load(gadgets);
      assertTrue(inventory.getItem("Broom"));
      assertTrue(inventory.getItem("Shoe"));
      assertFalse(inventory.getItem("M16"));
      assertFalse(inventory.getItem("Shoe"));
      assertFalse(inventory.getItem(null));
      fail("testGetItem fails");
    }

    @Test
    public void testLoad(){
        String[] gadgets = {"iPad","Tavor","Claw","Sword","Laser Glove","JetPack","M-16 Long","Knife","Smoke Grenade","Exploding Gum"};
        String[] test1 = {"Crocs","Snack","Iron"};

        inventory.load(gadgets);

        for (String gadget: gadgets)
            assertTrue(inventory.getItem(gadget));

        for (String gadget: gadgets)
            assertFalse(inventory.getItem(gadget));

        for (String test: test1)
            assertFalse(inventory.getItem(test));
        fail("testLoad fails");
    }
}
