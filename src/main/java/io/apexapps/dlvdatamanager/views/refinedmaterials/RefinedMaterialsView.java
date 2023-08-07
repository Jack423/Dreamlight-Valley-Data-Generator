package io.apexapps.dlvdatamanager.views.refinedmaterials;

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
import io.apexapps.dlvdatamanager.data.entity.RefinedMaterial;
import io.apexapps.dlvdatamanager.data.service.RefinedMaterialService;
import io.apexapps.dlvdatamanager.views.MainLayout;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Refined Materials")
@Route(value = "refined-materials/:refinedMaterialID?/:action?(edit)", layout = MainLayout.class)
public class RefinedMaterialsView extends Div implements BeforeEnterObserver {

    private final String REFINEDMATERIAL_ID = "refinedMaterialID";
    private final String REFINEDMATERIAL_EDIT_ROUTE_TEMPLATE = "refined-materials/%s/edit";

    private final Grid<RefinedMaterial> grid = new Grid<>(RefinedMaterial.class, false);

    CollaborationAvatarGroup avatarGroup;

    private TextField buyPrice;
    private TextField craftingRecipe;
    private TextField description;
    private TextField icon;
    private TextField name;
    private TextField sellPrice;
    private TextField soldAt;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final CollaborationBinder<RefinedMaterial> binder;

    private RefinedMaterial refinedMaterial;

    private final RefinedMaterialService refinedMaterialService;

    public RefinedMaterialsView(RefinedMaterialService refinedMaterialService) {
        this.refinedMaterialService = refinedMaterialService;
        addClassNames("refined-materials-view");

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
        grid.addColumn("buyPrice").setAutoWidth(true);
        grid.addColumn("craftingRecipe").setAutoWidth(true);
        grid.addColumn("description").setAutoWidth(true);
        grid.addColumn("icon").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("sellPrice").setAutoWidth(true);
        grid.addColumn("soldAt").setAutoWidth(true);
        grid.setItems(query -> refinedMaterialService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(REFINEDMATERIAL_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(RefinedMaterialsView.class);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(RefinedMaterial.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(buyPrice, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("buyPrice");
        binder.forField(sellPrice, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("sellPrice");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.refinedMaterial == null) {
                    this.refinedMaterial = new RefinedMaterial();
                }
                binder.writeBean(this.refinedMaterial);
                refinedMaterialService.update(this.refinedMaterial);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(RefinedMaterialsView.class);
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
        Optional<Long> refinedMaterialId = event.getRouteParameters().get(REFINEDMATERIAL_ID).map(Long::parseLong);
        if (refinedMaterialId.isPresent()) {
            Optional<RefinedMaterial> refinedMaterialFromBackend = refinedMaterialService.get(refinedMaterialId.get());
            if (refinedMaterialFromBackend.isPresent()) {
                populateForm(refinedMaterialFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested refinedMaterial was not found, ID = %d", refinedMaterialId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(RefinedMaterialsView.class);
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
        buyPrice = new TextField("Buy Price");
        craftingRecipe = new TextField("Crafting Recipe");
        description = new TextField("Description");
        icon = new TextField("Icon");
        name = new TextField("Name");
        sellPrice = new TextField("Sell Price");
        soldAt = new TextField("Sold At");
        formLayout.add(buyPrice, craftingRecipe, description, icon, name, sellPrice, soldAt);

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

    private void populateForm(RefinedMaterial value) {
        this.refinedMaterial = value;
        String topic = null;
        if (this.refinedMaterial != null && this.refinedMaterial.getId() != null) {
            topic = "refinedMaterial/" + this.refinedMaterial.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.refinedMaterial);
        avatarGroup.setTopic(topic);

    }
}
