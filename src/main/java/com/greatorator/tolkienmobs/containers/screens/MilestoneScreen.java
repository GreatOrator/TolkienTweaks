package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.containers.MilestoneContainer;
import com.greatorator.tolkienmobs.containers.handlers.ToggleButtonFactory;
import com.greatorator.tolkienmobs.containers.widget.ButtonTexture;
import com.greatorator.tolkienmobs.containers.widget.NumberButton;
import com.greatorator.tolkienmobs.containers.widget.ToggleButton;
import com.greatorator.tolkienmobs.containers.widget.TolkienButton;
import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.greatorator.tolkienmobs.network.manager.MilestoneSettingsUpdateManager;
import com.greatorator.tolkienmobs.util.ColorUtility;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.block.MilestoneSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.block.custom.MilestoneBlock.LIT;

public class MilestoneScreen extends AbstractContainerScreen<MilestoneContainer> {
    private final Identifier GUI = Identifier.fromNamespaceAndPath(MODID, "textures/gui/milestone/milestone_gui.png");
    protected final MilestoneContainer container;
    protected final MilestoneBlockEntity tileEntity;
    protected MilestoneSettings milestoneSettings;
    private EditBox milestoneName;
    protected List<AbstractWidget> widgetsToRemove = new ArrayList<>();
    protected List<AbstractWidget> widgetsToAdd = new ArrayList<>();
    private final List<AbstractWidget> locationList = new ArrayList<>();
    protected boolean renderablesChanged = false;
    public static final int maxItemsPerPage = 7;
    public static int currentPage = 0;

    public MilestoneScreen(MilestoneContainer container, Inventory inv, Component title) {
        // The height is fixed by the texture and set before anything else reads it; 26.1 makes the
        // two sizes final, so they go through the constructor instead of being assigned after.
        super(container, inv, title, 176, 196);
        this.container = container;
        this.tileEntity = container.tile;
        this.milestoneSettings = tileEntity.getMilestoneSettings();

    }

    @Override
    public void init() {
        super.init();
        if (getMinecraft().player.isCreative()) {
            var saveCodeButton = this.addRenderableWidget(TolkienButton
                .builder(Component.translatable("buttons.tolkienmobs.milestone.saveinfo"), button -> {
                    sendMessageToServer();
                    this.minecraft.setScreen(null);
                }, ButtonTexture.LARGE_BUTTON)
                .size(30, 16)
                .build());
            var layout = new LinearLayout((leftPos - 20) + 151, topPos + 45,
                LinearLayout.Orientation.HORIZONTAL);
            layout.spacing(4);
            layout.addChild(saveCodeButton);
            layout.arrangeElements();

            addNameField();
            addMilestoneDistanceButton();
            addMilestoneMultiplierButton();
            addPaymentMethod();
        }

        getItemsForPage();

        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        Button prevButton = Button.builder(Component.literal("<"), button -> {
            // Decrement page and rebuild the screen
            if (currentPage > 0) {
                currentPage--;
                this.clearWidgets(); // Clear old widgets
                this.init(); // Reinitialize with new page
            }
        })
                .pos(relX + 7, relY + 173)
                .size(8, 16)
                .build();
        prevButton.active = currentPage > 0;
        this.addRenderableWidget(prevButton);

        Button nextButton = Button.builder(Component.literal(">"), button -> {
            // Increment page and rebuild the screen
            if (currentPage <= getPageCount() - 1) {
                currentPage++;
                this.clearWidgets();
                this.init();
            }
        })
                .pos(relX + 161, relY + 173)
                .size(8, 16)
                .build();
        nextButton.active = currentPage < getPageCount();
        this.addRenderableWidget(nextButton);
        tileEntity.updateClientState();
    }

    public void addMilestoneDistanceButton() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(ToggleButtonFactory.DISTANCE_BUTTON(offsetX + 16, offsetY + 48, milestoneSettings.distance, b -> {
            milestoneSettings.distance = ((NumberButton) b).getValue();
            ClientPacketDistributor.sendToServer(new MilestoneSettingsUpdateManager(milestoneSettings.distance, milestoneSettings.dimension, milestoneSettings.name, milestoneSettings.uuid));
        }));
    }

    private void addMilestoneMultiplierButton() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(ToggleButtonFactory.DIMENSION_BUTTON(offsetX + 45, offsetY + 48, milestoneSettings.dimension, b -> {
            milestoneSettings.dimension = ((NumberButton) b).getValue();
            ClientPacketDistributor.sendToServer(new MilestoneSettingsUpdateManager(milestoneSettings.distance, milestoneSettings.dimension, milestoneSettings.name, milestoneSettings.uuid));
        }));
    }

    public void addPaymentMethod() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;

        if (TolkienMobsConfig.PAYMENT_TYPE.get()) {
            addRenderableWidget(ToggleButtonFactory.ITEM_PAYMENT_METHOD(offsetX + 74, offsetY + 48, 0, b -> {
            }));
        } else {
            addRenderableWidget(ToggleButtonFactory.EXPERIENCE_PAYMENT_METHOD(offsetX + 74, offsetY + 48, 0, b -> {
            }));
        }
    }

    public void addNameField() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(this.milestoneName = new EditBox(this.font, offsetX + 16, offsetY + 16, imageWidth - 31, this.font.lineHeight + 7, Component.literal(tileEntity.getMilestoneSettings().name)));

        this.milestoneName.setValue(tileEntity.getMilestoneSettings().name);
        this.milestoneName.setMaxLength(50);
        this.milestoneName.setVisible(true);
    }

    public void updateRenderables() {
        if (!widgetsToRemove.isEmpty()) {
            for (AbstractWidget abstractWidget : widgetsToRemove) {
                removeWidget(abstractWidget);
            }
            widgetsToRemove.clear();
        }
        if (!widgetsToAdd.isEmpty()) {
            for (AbstractWidget abstractWidget : widgetsToAdd) {
                addRenderableWidget(abstractWidget);
            }
            widgetsToAdd.clear();
        }
        renderablesChanged = false;
    }

    /**
     * Shift and control step further through the number buttons, which is the one place the old
     * screen read the keyboard modifiers directly. 26.1 keeps them on the input event that caused
     * the click, so the event is what this needs.
     */
    public int adjustNumberButton(int value, int change, int min, int max, MouseButtonEvent event) {
        if (event.hasShiftDown()) change *= 10;
        if (event.hasControlDown()) change *= 64;
        if (change < 0) {
            value = (Math.max(value + change, min));
        } else {
            value = (Math.min(value + change, max));
        }
        return value;
    }

    /** The index of the last page, which is what the next button and the page label count against. */
    public int getPageCount(){
        int others = 0;
        for (MilestoneHandler.MilestoneData data : MilestoneHandler.getKnownByPlayer(Objects.requireNonNull(getMinecraft().player))) {
            if (data.getUuid().equals(tileEntity.getUUID())) continue;
            others++;
        }
        // Ceiling division: the old count was one page short whenever the total was just past a
        // multiple of seven, which hid the last page of a long list.
        return Math.max((others + maxItemsPerPage - 1) / maxItemsPerPage - 1, 0);
    }

    private List<AbstractWidget> getItemsForPage() {
        int yOffset = 0;
        int startIndex = currentPage * maxItemsPerPage;
        List<MilestoneHandler.MilestoneData> knownByPlayer = MilestoneHandler.getKnownByPlayer(Objects.requireNonNull(getMinecraft().player));
        int endIndex = Math.min(startIndex + maxItemsPerPage, knownByPlayer.size());

        for (int i = 0; i < knownByPlayer.size(); i++) {
            // The page holds [startIndex, endIndex); the bounds used to skip the item at
            // startIndex itself, so the first known milestone never appeared on any page.
            if (i < startIndex || i >= endIndex) continue;
            MilestoneHandler.MilestoneData data = knownByPlayer.get(i);
            if (data.getUuid().equals(tileEntity.getUUID())) continue;

            int relX = (this.width - this.imageWidth) / 2;
            int relY = (this.height - this.imageHeight) / 2;
            int y = 66 + (yOffset * 15);
            locationList.add(
                    this.addRenderableWidget(Button.builder(Component.literal(data.getName()),
                                    button -> {
                                        this.tileEntity.sendPacketToServer(output -> output.writeUUID(data.getUuid()), 3);
                                    })
                            .pos(relX + 5, relY + y)
                            .size(150, 15)
                            .build()
                    )
            );
            if (TolkienMobsConfig.PAYMENT_TYPE.get()) {
                addRenderableWidget(ToggleButtonFactory.ITEM_PAYMENT_METHOD_CLIENT(relX + 155, relY + y, this.tileEntity.getItemTravelCost(data),
                        (button) -> {

                        }));
            } else {
                addRenderableWidget(ToggleButtonFactory.EXPERIENCE_PAYMENT_METHOD_CLIENT(relX + 155, relY + y, this.tileEntity.getExperienceTravelCost(data),
                        (button) -> {

                        }));
            }
            yOffset +=1; // Adjust for next button
        }
        return locationList;
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int pX, int pY) {
        super.extractTooltip(graphics, pX, pY);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof TolkienButton button && !button.getLocalization(pX, pY).equals(Component.empty()))
                graphics.setTooltipForNextFrame(font, button.getLocalization(pX, pY), pX, pY);
        }
        Component tooltip = Component.literal("");
        tooltip = Component.translatable("screen.tolkienmobs.milestone.instructions");
        if (getMinecraft().player.isCreative()) {
            this.milestoneName.setTooltip(Tooltip.create(tooltip));
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.centeredText(font, title, 83, 5, 8552833);
        if (container.tile.getBlockState().getValue(LIT)) {
            graphics.centeredText(font, " ", 83, 14, ColorUtility.DARKSLATEBLUE.getColor());
        }
        if (getMinecraft().player.isCreative()) {
            if (!milestoneName.isFocused() && milestoneName.getValue().isEmpty()) {
                graphics.text(font, tileEntity.getMilestoneSettings().name, milestoneName.getX() - leftPos + 4, (milestoneName.getY() + 2) - topPos, -10197916);
            }
        } else {
            graphics.centeredText(font, tileEntity.getMilestoneSettings().name, 83, 54, ColorUtility.CORNFLOWERBLUE.getColor());
        }

        // The page counter used to be drawn after the whole screen, in absolute coordinates; the
        // label pass already has the GUI origin applied, so it only needs the offset inside it.
        String pagesLabel = GeneralUtility.withSuffix(currentPage) + " / " + GeneralUtility.withSuffix(getPageCount());
        graphics.centeredText(font, pagesLabel, 100 - font.width(pagesLabel), 179, ColorUtility.DARKGRAY.getColor());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        if (TolkienMobsConfig.PAYMENT_TYPE.get()) {
            if (getMinecraft().player.isCreative()) {
                graphics.fakeItem(BuiltInRegistries.ITEM.getValue(Identifier.parse(TolkienMobsConfig.PAYMENT_ITEM.get())).getDefaultInstance(), relX + 74, relY + 48);
            }
        } else {
            if (getMinecraft().player.isCreative()) {
                graphics.fakeItem(BuiltInRegistries.ITEM.getValue(Identifier.parse("tolkienmobs:experience_orb")).getDefaultInstance(), relX + 74, relY + 48);
            }
        }

        // The name field is a widget, so it is drawn with the rest of them; it used to be rendered
        // here as well, which drew it over itself.
        if (renderablesChanged)
            updateRenderables();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean ret = super.mouseClicked(event, doubleClick);
        if (getMinecraft().player.isCreative()) {
            if (this.milestoneName.mouseClicked(event, doubleClick)) {
                this.milestoneName.setFocused(true);
                ret = true;
            } else if (this.milestoneName.isFocused()) {
                this.milestoneName.setFocused(false);
                ret = true;
            }
            for (Renderable renderable : this.renderables) {
                if (renderable instanceof NumberButton numberButton && GeneralUtility.inBounds(numberButton.getX(), numberButton.getY(), numberButton.getWidth(), numberButton.getHeight(), event.x(), event.y())) {
                    if (event.button() == 0)
                        numberButton.setValue(adjustNumberButton(numberButton.getValue(), 1, numberButton.min, numberButton.max, event));
                    else if (event.button() == 1)
                        numberButton.setValue(adjustNumberButton(numberButton.getValue(), -1, numberButton.min, numberButton.max, event));
                    numberButton.onPress(event);
                    numberButton.playDownSound(Minecraft.getInstance().getSoundManager());
                    return true;
                }
                if (renderable instanceof ToggleButton toggleButton && GeneralUtility.inBounds(toggleButton.getX(), toggleButton.getY(), toggleButton.getWidth(), toggleButton.getHeight(), event.x(), event.y())) {
                    if (event.button() == 1) {
                        toggleButton.onClick(event);
                        toggleButton.playDownSound(Minecraft.getInstance().getSoundManager());
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        // The page buttons rebuild the screen after moving a page; scrolling only changed the
        // counter, so the label moved and the list stayed where it was.
        if (scrollDeltaY > 0) {
            if (this.currentPage > 0) {
                this.currentPage--;
                this.clearWidgets();
                this.init();
            }
        } else {
            if (this.currentPage < getPageCount()) {
                this.currentPage++;
                this.clearWidgets();
                this.init();
            }
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (getMinecraft().player.isCreative()) {
            if (this.milestoneName.isFocused() && !event.isEscape())
                if (this.milestoneName.keyPressed(event) || this.milestoneName.canConsumeInput())
                    return true;
        }
        return super.keyPressed(event);
    }

    private void sendMessageToServer() {
        milestoneSettings.name = this.milestoneName.getValue();

        ClientPacketDistributor.sendToServer(new MilestoneSettingsUpdateManager(milestoneSettings.distance, milestoneSettings.dimension, milestoneSettings.name, milestoneSettings.uuid));
    }
}
