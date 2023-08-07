package io.apexapps.dlvdatamanager.views.fish;

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
import io.apexapps.dlvdatamanager.data.entity.Fish;
import io.apexapps.dlvdatamanager.data.service.FishService;
import io.apexapps.dlvdatamanager.views.MainLayout;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Fish")
@Route(value = "fish/:fishID?/:action?(edit)", layout = MainLayout.class)
public class FishView extends Div implements BeforeEnterObserver {

    private final String FISH_ID = "fishID";
    private final String FISH_EDIT_ROUTE_TEMPLATE = "fish/%s/edit";

    private final Grid<Fish> grid = new Grid<>(Fish.class, false);

    CollaborationAvatarGroup avatarGroup;

    private TextField description;
    private TextField energy;
    private TextField icon;
    private TextField location;
    private TextField name;
    private TextField rippleColor;
    private TextField sellPrice;
    private TextField weatherCondition;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final CollaborationBinder<Fish> binder;

    private Fish fish;

    private final FishService fishService;

    public FishView(FishService fishService) {
        this.fishService = fishService;
        addClassNames("fish-view");

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
        grid.addColumn("description").setAutoWidth(true);
        grid.addColumn("energy").setAutoWidth(true);
        grid.addColumn("icon").setAutoWidth(true);
        grid.addColumn("location").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("rippleColor").setAutoWidth(true);
        grid.addColumn("sellPrice").setAutoWidth(true);
        grid.addColumn("weatherCondition").setAutoWidth(true);
        grid.setItems(query -> fishService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(FISH_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(FishView.class);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(Fish.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(energy, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("energy");
        binder.forField(sellPrice, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("sellPrice");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.fish == null) {
                    this.fish = new Fish();
                }
                binder.writeBean(this.fish);
                fishService.update(this.fish);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(FishView.class);
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
        Optional<Long> fishId = event.getRouteParameters().get(FISH_ID).map(Long::parseLong);
        if (fishId.isPresent()) {
            Optional<Fish> fishFromBackend = fishService.get(fishId.get());
            if (fishFromBackend.isPresent()) {
                populateForm(fishFromBackend.get());
            } else {
                Notification.show(String.format("The requested fish was not found, ID = %d", fishId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(FishView.class);
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
        description = new TextField("Description");
        energy = new TextField("Energy");
        icon = new TextField("Icon");
        location = new TextField("Location");
        name = new TextField("Name");
        rippleColor = new TextField("Ripple Color");
        sellPrice = new TextField("Sell Price");
        weatherCondition = new TextField("Weather Condition");
        formLayout.add(description, energy, icon, location, name, rippleColor, sellPrice, weatherCondition);

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

    private void populateForm(Fish value) {
        this.fish = value;
        String topic = null;
        if (this.fish != null && this.fish.getId() != null) {
            topic = "fish/" + this.fish.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.fish);
        avatarGroup.setTopic(topic);

    }
}
