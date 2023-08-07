package io.apexapps.dlvdatamanager.views.ingredients;

import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationBinder;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import io.apexapps.dlvdatamanager.data.entity.Ingredient;
import io.apexapps.dlvdatamanager.data.service.IngredientService;
import io.apexapps.dlvdatamanager.views.MainLayout;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Ingredients")
@Route(value = "ingredients/:ingredientID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
public class IngredientsView extends Div implements BeforeEnterObserver {

    private final String INGREDIENT_ID = "ingredientID";
    private final String INGREDIENT_EDIT_ROUTE_TEMPLATE = "ingredients/%s/edit";

    private final Grid<Ingredient> grid = new Grid<>(Ingredient.class, false);

    CollaborationAvatarGroup avatarGroup;

    private TextField buyPrice;
    private TextField description;
    private TextField energy;
    private TextField growTime;
    private Checkbox hidden;
    private TextField icon;
    private TextField ingredientType;
    private TextField name;
    private TextField sellPrice;
    private TextField water;
    private TextField yield;
    private TextField location;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final CollaborationBinder<Ingredient> binder;

    private Ingredient ingredient;

    private final IngredientService ingredientService;

    public IngredientsView(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
        addClassNames("ingredients-view");

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
        grid.addColumn("description").setAutoWidth(true);
        grid.addColumn("energy").setAutoWidth(true);
        grid.addColumn("growTime").setAutoWidth(true);
        LitRenderer<Ingredient> hiddenRenderer = LitRenderer.<Ingredient>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", hidden -> hidden.isHidden() ? "check" : "minus").withProperty("color",
                        hidden -> hidden.isHidden()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(hiddenRenderer).setHeader("Hidden").setAutoWidth(true);

        grid.addColumn("icon").setAutoWidth(true);
        grid.addColumn("ingredientType").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("sellPrice").setAutoWidth(true);
        grid.addColumn("water").setAutoWidth(true);
        grid.addColumn("yield").setAutoWidth(true);
        grid.addColumn("location").setAutoWidth(true);
        grid.setItems(query -> ingredientService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(INGREDIENT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(IngredientsView.class);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(Ingredient.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(buyPrice, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("buyPrice");
        binder.forField(energy, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("energy");
        binder.forField(sellPrice, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("sellPrice");
        binder.forField(water, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("water");
        binder.forField(yield, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("yield");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.ingredient == null) {
                    this.ingredient = new Ingredient();
                }
                binder.writeBean(this.ingredient);
                ingredientService.update(this.ingredient);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(IngredientsView.class);
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
        Optional<Long> ingredientId = event.getRouteParameters().get(INGREDIENT_ID).map(Long::parseLong);
        if (ingredientId.isPresent()) {
            Optional<Ingredient> ingredientFromBackend = ingredientService.get(ingredientId.get());
            if (ingredientFromBackend.isPresent()) {
                populateForm(ingredientFromBackend.get());
            } else {
                Notification.show(String.format("The requested ingredient was not found, ID = %d", ingredientId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(IngredientsView.class);
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
        description = new TextField("Description");
        energy = new TextField("Energy");
        growTime = new TextField("Grow Time");
        hidden = new Checkbox("Hidden");
        icon = new TextField("Icon");
        ingredientType = new TextField("Ingredient Type");
        name = new TextField("Name");
        sellPrice = new TextField("Sell Price");
        water = new TextField("Water");
        yield = new TextField("Yield");
        location = new TextField("Location");
        formLayout.add(buyPrice, description, energy, growTime, hidden, icon, ingredientType, name, sellPrice, water,
                yield, location);

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

    private void populateForm(Ingredient value) {
        this.ingredient = value;
        String topic = null;
        if (this.ingredient != null && this.ingredient.getId() != null) {
            topic = "ingredient/" + this.ingredient.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.ingredient);
        avatarGroup.setTopic(topic);

    }
}
