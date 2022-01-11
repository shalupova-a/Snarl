package Game.View;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;

public class ViewPanelFactory {

    private static final int GAMEPANEL_WIDTH = 50;
    private static final int GAMEPANEL_HEIGHT = 25;

    private static final int STORYPANEL_WIDTH = 70;
    private static final int STORYPANEL_HEIGHT = 14;

    private static final int PLAYERINFO_WIDTH = 20;
    private static final int PLAYERINFO_HEIGHT = 25;

    private static final AsciiFont TILESET = AsciiFont.TALRYTH_15_15;

    public AsciiPanel getAsciiPanel(String panelType) {
        switch (panelType) {
            case "GAME":
                return new AsciiPanel(GAMEPANEL_WIDTH, GAMEPANEL_HEIGHT, TILESET);
            case "STORY":
                return new AsciiPanel(STORYPANEL_WIDTH, STORYPANEL_HEIGHT, TILESET);
            case "INVENTORY":
                return new AsciiPanel(PLAYERINFO_WIDTH, PLAYERINFO_HEIGHT, TILESET);
            default:
                return null;
        }
    }
}
