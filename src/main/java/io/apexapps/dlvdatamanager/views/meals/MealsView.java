package io.apexapps.dlvdatamanager.views.meals;

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
import io.apexapps.dlvdatamanager.data.entity.Meal;
import io.apexapps.dlvdatamanager.data.service.MealService;
import io.apexapps.dlvdatamanager.views.MainLayout;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Meals")
@Route(value = "meals/:mealID?/:action?(edit)", layout = MainLayout.class)
public class MealsView extends Div implements BeforeEnterObserver {

    private final String MEAL_ID = "mealID";
    private final String MEAL_EDIT_ROUTE_TEMPLATE = "meals/%s/edit";

    private final Grid<Meal> grid = new Grid<>(Meal.class, false);

    CollaborationAvatarGroup avatarGroup;

    private TextField description;
    private TextField energy;
    private TextField icon;
    private TextField ingredients;
    private TextField name;
    private TextField recipeType;
    private TextField sellPrice;
    private TextField stars;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final CollaborationBinder<Meal> binder;

    private Meal meal;

    private final MealService mealService;

    public MealsView(MealService mealService) {
        this.mealService = mealService;
        addClassNames("meals-view");

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
        grid.addColumn("ingredients").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("recipeType").setAutoWidth(true);
        grid.addColumn("sellPrice").setAutoWidth(true);
        grid.addColumn("stars").setAutoWidth(true);
        grid.setItems(query -> mealService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(MEAL_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MealsView.class);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(Meal.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(energy, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("energy");
        binder.forField(sellPrice, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("sellPrice");
        binder.forField(stars, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("stars");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.meal == null) {
                    this.meal = new Meal();
                }
                binder.writeBean(this.meal);
                mealService.update(this.meal);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(MealsView.class);
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
        Optional<Long> mealId = event.getRouteParameters().get(MEAL_ID).map(Long::parseLong);
        if (mealId.isPresent()) {
            Optional<Meal> mealFromBackend = mealService.get(mealId.get());
            if (mealFromBackend.isPresent()) {
                populateForm(mealFromBackend.get());
            } else {
                Notification.show(String.format("The requested meal was not found, ID = %d", mealId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(MealsView.class);
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
        ingredients = new TextField("Ingredients");
        name = new TextField("Name");
        recipeType = new TextField("Recipe Type");
        sellPrice = new TextField("Sell Price");
        stars = new TextField("Stars");
        formLayout.add(description, energy, icon, ingredients, name, recipeType, sellPrice, stars);

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

    private void populateForm(Meal value) {
        this.meal = value;
        String topic = null;
        if (this.meal != null && this.meal.getId() != null) {
            topic = "meal/" + this.meal.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.meal);
        avatarGroup.setTopic(topic);

    }
}
