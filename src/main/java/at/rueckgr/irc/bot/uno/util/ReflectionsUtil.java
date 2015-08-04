package at.rueckgr.irc.bot.uno.util;

import at.rueckgr.irc.bot.uno.events.Event;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ReflectionsUtil {
    private ReflectionsUtil() {
        // don't instantiate this class
    }

    public static Map<String, ? extends Event> getEvents() {
        Map<String, Event> events = new HashMap<>();

        for (Event eventObject : getClasses(Event.class)) {
            events.put(eventObject.getCommand(), eventObject);
        }

        return events;
    }


    private static <T> List<T> getClasses(Class<T> clazz) {
        return new Reflections(clazz.getPackage().getName()).getSubTypesOf(clazz).stream().map(ReflectionsUtil::newInstance).collect(Collectors.toList());
    }

    private static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
