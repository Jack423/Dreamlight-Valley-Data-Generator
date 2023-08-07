package io.apexapps.dlvdatamanager.views.foraging;

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
import io.apexapps.dlvdatamanager.data.entity.Foraging;
import io.apexapps.dlvdatamanager.data.service.ForagingService;
import io.apexapps.dlvdatamanager.views.MainLayout;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Foraging")
@Route(value = "foraging/:foragingID?/:action?(edit)", layout = MainLayout.class)
public class ForagingView extends Div implements BeforeEnterObserver {

    private final String FORAGING_ID = "foragingID";
    private final String FORAGING_EDIT_ROUTE_TEMPLATE = "foraging/%s/edit";

    private final Grid<Foraging> grid = new Grid<>(Foraging.class, false);

    CollaborationAvatarGroup avatarGroup;

    private TextField acquisitionMethods;
    private TextField description;
    private TextField image;
    private TextField locations;
    private TextField name;
    private TextField sellPrice;
    private TextField type;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final CollaborationBinder<Foraging> binder;

    private Foraging foraging;

    private final ForagingService foragingService;

    public ForagingView(ForagingService foragingService) {
        this.foragingService = foragingService;
        addClassNames("foraging-view");

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
        grid.addColumn("acquisitionMethods").setAutoWidth(true);
        grid.addColumn("description").setAutoWidth(true);
        grid.addColumn("image").setAutoWidth(true);
        grid.addColumn("locations").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("sellPrice").setAutoWidth(true);
        grid.addColumn("type").setAutoWidth(true);
        grid.setItems(query -> foragingService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(FORAGING_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ForagingView.class);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(Foraging.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(sellPrice, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("sellPrice");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.foraging == null) {
                    this.foraging = new Foraging();
                }
                binder.writeBean(this.foraging);
                foragingService.update(this.foraging);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(ForagingView.class);
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
        Optional<Long> foragingId = event.getRouteParameters().get(FORAGING_ID).map(Long::parseLong);
        if (foragingId.isPresent()) {
            Optional<Foraging> foragingFromBackend = foragingService.get(foragingId.get());
            if (foragingFromBackend.isPresent()) {
                populateForm(foragingFromBackend.get());
            } else {
                Notification.show(String.format("The requested foraging was not found, ID = %d", foragingId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ForagingView.class);
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
        acquisitionMethods = new TextField("Acquisition Methods");
        description = new TextField("Description");
        image = new TextField("Image");
        locations = new TextField("Locations");
        name = new TextField("Name");
        sellPrice = new TextField("Sell Price");
        type = new TextField("Type");
        formLayout.add(acquisitionMethods, description, image, locations, name, sellPrice, type);

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

    private void populateForm(Foraging value) {
        this.foraging = value;
        String topic = null;
        if (this.foraging != null && this.foraging.getId() != null) {
            topic = "foraging/" + this.foraging.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.foraging);
        avatarGroup.setTopic(topic);

    }
}
