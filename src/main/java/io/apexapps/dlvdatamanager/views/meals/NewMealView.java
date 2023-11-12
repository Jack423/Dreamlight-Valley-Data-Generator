package io.apexapps.dlvdatamanager.views.meals;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import io.apexapps.dlvdatamanager.components.IngredientPicker;
import io.apexapps.dlvdatamanager.data.entity.Meal;
import io.apexapps.dlvdatamanager.data.service.MealService;
import io.apexapps.dlvdatamanager.data.service.RecipeIngredientService;
import io.apexapps.dlvdatamanager.utils.NotificationUtil;
import io.apexapps.dlvdatamanager.views.MainLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@PageTitle("New Meal")
@Route(value = "meals/new", layout = MainLayout.class)
public class NewMealView extends Composite<VerticalLayout> {
    private static final String MEAL_ICON_PATH_FORMAT = "/assets/images/recipes/%s/%s.png";
    private final FormLayout formLayout = new FormLayout();
    private final TextField name = new TextField("Name");
    private final TextField icon = new TextField("Icon");
    private final TextArea description = new TextArea("Description");
    private final ComboBox<Meal.RecipeType> recipeType = new ComboBox<>("Recipe Type");
    private final TextField stars = new TextField("Stars");
    private final TextField energy = new TextField("Energy");
    private final TextField sellPrice = new TextField("Sell Price");
    private final Binder<Meal> mealBinder = new Binder<>(Meal.class);
    private final Meal meal = new Meal();
    private final IngredientPicker ingredientPicker;
    private final MealService mealService;

    public NewMealView(MealService mealService, RecipeIngredientService recipeIngredientService) {
        this.mealService = mealService;
        this.ingredientPicker = new IngredientPicker(recipeIngredientService.load());
        addClassNames("new-meal-view");

        getContent().add(new Button("Back", new Icon(VaadinIcon.ARROW_LEFT), event -> UI
                .getCurrent()
                .navigate(MealsView.class)
        ));

        newIngredientView();
        buildSaveButton();
    }

    private void newIngredientView() {
        getContent().setHeightFull();
        icon.setReadOnly(true);

        recipeType.setItems(Meal.RecipeType.values());
        recipeType.setItemLabelGenerator(type -> type.name);
        name.addValueChangeListener(event -> {
            if (recipeType.getValue() != null || meal.getRecipeType() != null) {
                final String value = String.format(
                        MEAL_ICON_PATH_FORMAT,
                        getIconPathString(Optional.ofNullable(recipeType.getValue()).orElse(meal.getRecipeType())),
                        Optional.ofNullable(event.getValue()).orElse(meal.getName()).replaceAll(" ", "_")
                );

                icon.setValue(value);
                meal.setIcon(value);
            }
        });
        recipeType.addValueChangeListener(event -> {
            if (meal.getName() != null || name.getValue() != null) {
                final String value = String.format(
                        MEAL_ICON_PATH_FORMAT,
                        getIconPathString(Optional.ofNullable(event.getValue()).orElse(meal.getRecipeType())),
                        Optional.ofNullable(name.getValue()).orElse(meal.getName()).replaceAll(" ", "_")
                );

                icon.setValue(value);
                meal.setIcon(value);
            }
        });

        formLayout.add(name, icon, description, recipeType, stars, energy, sellPrice);

        getContent().add(formLayout);

        mealBinder.bindInstanceFields(this);
        mealBinder.readBean(meal);

        try {
            ingredientPicker.setIngredients(meal.getIngredients());
            getContent().add(ingredientPicker);
        } catch (Exception e) {
            log.error("Unable to load the full list of ingredients", e);
            NotificationUtil.showError("Unable to load the recipe ingredients");
        }
    }

    private void buildSaveButton() {
        Button saveButton = new Button("Save", event -> {
            try {
                if (mealBinder.isValid()) {
                    meal.setId(mealService.count() + 1);
                    meal.setIngredients(ingredientPicker.getIngredients());
                    mealBinder.writeBean(meal);
                    mealService.update(meal);
                    NotificationUtil.showSuccess("Meal saved successfully");
                }
            } catch (Exception e) {
                log.error("Unable to save the meal", e);
                NotificationUtil.showError("Unable to save the meal");
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        getContent().add(saveButton);
    }

    private String getIconPathString(Meal.RecipeType type) {
        return switch (type) {
            case APPETIZERS -> "appetizers";
            case ENTREES -> "entrees";
            case DESSERTS -> "desserts";
        };
    }
}
