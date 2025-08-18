package com.aula.aion.model;

public class CalendarDay {
    private String dayText;
    private boolean isCurrentMonth;
    private boolean isGreenOutline;
    private boolean isRedOutline;
    private boolean isPurpleFill;
    private int dayOfMonth;

    public CalendarDay(String dayText, boolean isCurrentMonth, int dayOfMonth) {
        this.dayText = dayText;
        this.isCurrentMonth = isCurrentMonth;
        this.dayOfMonth = dayOfMonth;
        this.isGreenOutline = false;
        this.isRedOutline = false;
        this.isPurpleFill = false;
    }

    // Getters e Setters
    public String getDayText() { return dayText; }
    public boolean isCurrentMonth() { return isCurrentMonth; }
    public boolean isGreenOutline() { return isGreenOutline; }
    public void setGreenOutline(boolean greenOutline) { isGreenOutline = greenOutline; }
    public boolean isRedOutline() { return isRedOutline; }
    public void setRedOutline(boolean redOutline) { isRedOutline = redOutline; }
    public boolean isPurpleFill() { return isPurpleFill; }
    public void setPurpleFill(boolean purpleFill) { isPurpleFill = purpleFill; }
    public int getDayOfMonth() { return dayOfMonth; }

}
