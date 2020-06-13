package austeretony.oxygen_store.client.gui.store.gifts;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import austeretony.alternateui.screen.browsing.GUIScroller;
import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.core.GUISimpleElement;

public class OxygenSubCategoriesList extends GUISimpleElement<OxygenSubCategoriesList> {

    private final Multimap<String, String> elements = HashMultimap.<String, String>create();

    private int elementsOffset, elementWidth, elementHeight, subElementHeight;

    public final List<GUIButton> visibleButtons, buttonsBuffer;

    public OxygenSubCategoriesList(int xPosition, int yPosition, int visibleElementsAmount, int elementsOffset, int elementWidth, int elementHeight, int subElementHeight) {
        this.setPosition(xPosition, yPosition);
        this.setSize(elementWidth, elementHeight * visibleElementsAmount + elementsOffset * (visibleElementsAmount - 1));

        this.elementsOffset = elementsOffset;
        this.elementWidth = elementWidth;
        this.elementHeight = elementHeight;
        this.subElementHeight = subElementHeight;

        GUIScroller scroller = new GUIScroller(visibleElementsAmount, visibleElementsAmount);
        this.initScroller(scroller);

        this.visibleButtons = new ArrayList<>(visibleElementsAmount);
        this.buttonsBuffer = new ArrayList<>(visibleElementsAmount);

        this.enableFull();
    }

    public void addCategory(String category) {
        this.elements.put(category, "EMPTY");
    }

    public void addCategory(String category, Iterable<String> subCategories) {
        this.elements.putAll(category, subCategories);
    }

    public void addCategory(String category, String[] subCategories) {
        for (String subCategory : subCategories)
            this.elements.put(category, subCategory);
    }
    
    public void load() {
        //TODO
    }
}
