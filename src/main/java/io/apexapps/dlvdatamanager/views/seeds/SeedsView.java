package io.apexapps.dlvdatamanager.views.seeds;

import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationBinder;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import io.apexapps.dlvdatamanager.data.entity.Seed;
import io.apexapps.dlvdatamanager.data.service.SeedService;
import io.apexapps.dlvdatamanager.views.MainLayout;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Seeds")
@Route(value = "seeds/:seedID?/:action?(edit)", layout = MainLayout.class)
public class SeedsView extends Div implements BeforeEnterObserver {

    private final String SEED_ID = "seedID";
    private final String SEED_EDIT_ROUTE_TEMPLATE = "seeds/%s/edit";

    private final Grid<Seed> grid = new Grid<>(Seed.class, false);

    CollaborationAvatarGroup avatarGroup;

    private TextField growTime;
    private TextField icon;
    private TextField ingredientType;
    private TextField name;
    private TextField nativeBiome;
    private TextField seedPrice;
    private TextField sellPrice;
    private TextField waterings;
    private TextField yield;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final CollaborationBinder<Seed> binder;

    private Seed seed;

    private final SeedService seedService;

    public SeedsView(SeedService seedService) {
        this.seedService = seedService;
        addClassNames("seeds-view");

        // UserInfo is used by Collaboration Engine and is used to share details
        // of users to each other to able collaboration. Replace this with
        // information about the actual user that is logged, providing a user
        // identifier, and the user's real name. You can also provide the users
        // avatar by passing an url to the image as a third parameter, or by
        // configuring an `ImageProvider` to `avatarGroup`.
        UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        avatarGroup.getStyle().set("visibility", "hidden");

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("growTime").setAutoWidth(true);
        grid.addColumn("icon").setAutoWidth(true);
        grid.addColumn("ingredientType").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("nativeBiome").setAutoWidth(true);
        grid.addColumn("seedPrice").setAutoWidth(true);
        grid.addColumn("sellPrice").setAutoWidth(true);
        grid.addColumn("waterings").setAutoWidth(true);
        grid.addColumn("yield").setAutoWidth(true);
        grid.setItems(query -> seedService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SEED_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(SeedsView.class);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(Seed.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(seedPrice, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("seedPrice");
        binder.forField(sellPrice, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("sellPrice");
        binder.forField(waterings, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("waterings");
        binder.forField(yield, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("yield");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.seed == null) {
                    this.seed = new Seed();
                }
                binder.writeBean(this.seed);
                seedService.update(this.seed);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(SeedsView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> seedId = event.getRouteParameters().get(SEED_ID).map(Long::parseLong);
        if (seedId.isPresent()) {
            Optional<Seed> seedFromBackend = seedService.get(seedId.get());
            if (seedFromBackend.isPresent()) {
                populateForm(seedFromBackend.get());
            } else {
                Notification.show(String.format("The requested seed was not found, ID = %d", seedId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(SeedsView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        growTime = new TextField("Grow Time");
        icon = new TextField("Icon");
        ingredientType = new TextField("Ingredient Type");
        name = new TextField("Name");
        nativeBiome = new TextField("Native Biome");
        seedPrice = new TextField("Seed Price");
        sellPrice = new TextField("Sell Price");
        waterings = new TextField("Waterings");
        yield = new TextField("Yield");
        formLayout.add(growTime, icon, ingredientType, name, nativeBiome, seedPrice, sellPrice, waterings, yield);

        editorDiv.add(avatarGroup, formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Seed value) {
        this.seed = value;
        String topic = null;
        if (this.seed != null && this.seed.getId() != null) {
            topic = "seed/" + this.seed.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.seed);
        avatarGroup.setTopic(topic);

    }
}
