package io.apexapps.dlvdatamanager.views.characters;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import io.apexapps.dlvdatamanager.components.avataritem.AvatarItem;
import io.apexapps.dlvdatamanager.views.MainLayout;

@PageTitle("Characters")
@Route(value = "characters/:characterID?/:action?(edit)", layout = MainLayout.class)
public class CharactersView extends Composite<VerticalLayout> {

    private Button buttonSecondary = new Button();

    private VerticalLayout layoutColumn2 = new VerticalLayout();

    private AvatarItem avatarItem = new AvatarItem();

    private AvatarItem avatarItem2 = new AvatarItem();

    public CharactersView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
        buttonSecondary.setText("Create New Character");
        getContent().setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidthFull();
        avatarItem.setWidthFull();
        setAvatarItemSampleData(avatarItem);
        avatarItem2.setWidthFull();
        setAvatarItemSampleData(avatarItem2);
        getContent().add(buttonSecondary);
        getContent().add(layoutColumn2);
        layoutColumn2.add(avatarItem);
        layoutColumn2.add(avatarItem2);
    }

    private void setAvatarItemSampleData(AvatarItem avatarItem) {
        avatarItem.setHeading("Aria Bailey");
        avatarItem.setDescription("Endocrinologist");
        avatarItem.setAvatar(new Avatar("Aria Bailey"));
    }
}
