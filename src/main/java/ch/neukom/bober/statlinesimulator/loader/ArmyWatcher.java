package ch.neukom.bober.statlinesimulator.loader;

import ch.neukom.bober.statlinesimulator.data.Army;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ArmyWatcher {
    private final Map<String, Army> armies = new HashMap<>();
    private final Set<Consumer<Map<String, Army>>> onUpdateConsumers = new HashSet<>();

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private final AtomicInteger pausedCounter = new AtomicInteger(0);

    public ArmyWatcher(WatchKey watchKey,
                       Supplier<Map<String, Army>> armyLoader) {
        executor.scheduleWithFixedDelay(() -> {
            List<WatchEvent<?>> events = watchKey.pollEvents();
            if (pausedCounter.get() == 0 && (armies.isEmpty() || !events.isEmpty())) {
                updateArmies(armyLoader.get());
            }
            watchKey.reset();
        }, 0, 1, TimeUnit.SECONDS);
    }

    public Map<String, Army> getArmies() {
        return armies;
    }

    public void updateArmies(Map<String, Army> armies) {
        this.armies.clear();
        this.armies.putAll(armies);
        runOnUpdate();
    }

    public void addArmy(Army army) {
        this.armies.put(army.armyId(), army);
        runOnUpdate();
    }

    public void registerOnUpdate(Consumer<Map<String, Army>> consumer) {
        this.onUpdateConsumers.add(consumer);
        if (!armies.isEmpty()) {
            runOnUpdate();
        }
    }

    public void withoutSynchronization(Runnable runnable) {
        pausedCounter.incrementAndGet();
        runnable.run();
        pausedCounter.decrementAndGet();
    }

    private void runOnUpdate() {
        onUpdateConsumers.forEach(consumer -> consumer.accept(armies));
    }
}
