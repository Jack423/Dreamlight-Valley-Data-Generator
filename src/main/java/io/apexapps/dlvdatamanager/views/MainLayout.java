package io.apexapps.dlvdatamanager.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import io.apexapps.dlvdatamanager.views.allothercraftingitems.AllOtherCraftingItemsView;
import io.apexapps.dlvdatamanager.views.characters.CharactersView;
import io.apexapps.dlvdatamanager.views.critters.CrittersView;
import io.apexapps.dlvdatamanager.views.fish.FishView;
import io.apexapps.dlvdatamanager.views.foraging.ForagingView;
import io.apexapps.dlvdatamanager.views.gems.GemsView;
import io.apexapps.dlvdatamanager.views.home.HomeView;
import io.apexapps.dlvdatamanager.views.ingredients.IngredientsView;
import io.apexapps.dlvdatamanager.views.locations.LocationsView;
import io.apexapps.dlvdatamanager.views.meals.MealsView;
import io.apexapps.dlvdatamanager.views.refinedmaterials.RefinedMaterialsView;
import io.apexapps.dlvdatamanager.views.seeds.SeedsView;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("DLV Data Manager");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Home", HomeView.class, LineAwesomeIcon.HOME_SOLID.create()));
        nav.addItem(new SideNavItem("Characters", CharactersView.class, LineAwesomeIcon.MALE_SOLID.create()));
        nav.addItem(new SideNavItem("Critters", CrittersView.class, LineAwesomeIcon.CAT_SOLID.create()));
        nav.addItem(new SideNavItem("Fish", FishView.class, LineAwesomeIcon.FISH_SOLID.create()));
        nav.addItem(new SideNavItem("Foraging", ForagingView.class, LineAwesomeIcon.SHOPPING_BASKET_SOLID.create()));
        nav.addItem(new SideNavItem("Ingredients", IngredientsView.class, LineAwesomeIcon.APPLE_ALT_SOLID.create()));
        nav.addItem(new SideNavItem("Meals", MealsView.class, LineAwesomeIcon.BREAD_SLICE_SOLID.create()));
        nav.addItem(new SideNavItem("Refined Materials", RefinedMaterialsView.class,
                LineAwesomeIcon.HAMMER_SOLID.create()));
        nav.addItem(new SideNavItem("All Other Crafting Items", AllOtherCraftingItemsView.class,
                LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new SideNavItem("Gems", GemsView.class, LineAwesomeIcon.GEM_SOLID.create()));
        nav.addItem(new SideNavItem("Locations", LocationsView.class, LineAwesomeIcon.MAP_MARKER_SOLID.create()));
        nav.addItem(new SideNavItem("Seeds", SeedsView.class, LineAwesomeIcon.SEEDLING_SOLID.create()));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
