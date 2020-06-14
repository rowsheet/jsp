package dashboard;

public class Sidebar {
    public Sidebar() {
        this.addSeparator();
        this.addPrimaryLink("Dashboard", "tachometer-alt", "/dashboard");
        this.addSeparator();
        this.addPrimaryLabel("interface");
        this.addDropdown("Components", "cog");
        this.addSecondaryLabel("Components", "custom components");
        this.addSecondaryLink("Components", "Buttons", "/buttons");
        this.addSecondaryLink("Components", "Cards", "/cards");
        this.addDropdown("Utilities", "wrench");
        this.addSecondaryLabel("Utilities", "custom utilities");
        this.addSecondaryLink("Utilities", "Colors", "/colors");
        this.addSecondaryLink("Utilities", "Borders", "/borders");
        this.addSecondaryLink("Utilities", "Animations", "/animations");
        this.addSecondaryLink("Utilities", "Other", "/other");
        this.addSeparator();
    }
    public void addSeparator() {

    }
    public void addPrimaryLink(String name, String icon, String href) {
        addPrimaryLink(name, icon, href, false);
    }
    public void addPrimaryLink(String name, String icon, String href, boolean active) {

    }
    public void addDropdown(String name, String icon) {

    }
    public void addPrimaryLabel(String name) {

    }
    public void addSecondaryLabel(String dropdownName, String name) {

    }
    public void addSecondaryLink(String dropdownName, String name, String href) {
        addSecondaryLink(dropdownName, name, href, false);
    }
    public void addSecondaryLink(String dropdownName, String name, String href, boolean active) {

    }
}
