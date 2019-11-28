package com.xceptance.loadtest.api.events;

public abstract class EventLogger
{
    public static Event DEFAULT = Event.init("default");

    public static Event BROWSE = Event.init("browse");
    public static Event ADD_TO_CART = Event.init("add2cart");
    public static Event CRAWLER = Event.init("crawler");
    public static Event PRICE = Event.init("price");
    public static Event CHECKOUT = Event.init("checkout");
}
