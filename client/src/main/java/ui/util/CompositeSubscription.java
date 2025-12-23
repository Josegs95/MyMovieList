package ui.util;

import java.util.ArrayList;
import java.util.List;

public class CompositeSubscription implements Subscription {

    private final List<Subscription> subscriptions = new ArrayList<>();

    public void add(Subscription subscription) {
        subscriptions.add(subscription);
    }

    @Override
    public void unsubscribe() {
        subscriptions.forEach(Subscription::unsubscribe);
        subscriptions.clear();
    }
}
