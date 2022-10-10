package com.cloud.cloudclient.view;

import com.cloud.cloudclient.view.enums.TypeOfLoad;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.ProgressBar;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class Indicators {
    private static ObservableMap<String, Optional<Indicator>> indicators = FXCollections.observableHashMap();

    public static ObservableMap<String, Optional<Indicator>> getIndicators() {
        return indicators;
    }

    public static void downloading(String fileName, long fullSize, int sizeOfBatch, TypeOfLoad type) {
        try {
            getProgressBar(fileName).orElseThrow().cumulative();
        } catch (Exception e) {
            log.debug("add indicator");
            addIndicator(fileName, fullSize, sizeOfBatch, type);
        }
    }

    private static void addIndicator(String fileName, long fullSize, int sizeOfBatch, TypeOfLoad type) {
        indicators.put(fileName, Optional.of(new Indicator(new ProgressBar(),
                1.0 / ((double) fullSize / (double) sizeOfBatch), type)));
    }

    public static Optional<Indicator> getProgressBar(String fileName) {
        return indicators.get(fileName);
    }

    public static void removeIndicator(String fileName) {
        indicators.remove(fileName);
    }

    public static int getSize() {
        return indicators.size();
    }

    static {
        indicators.addListener((MapChangeListener<? super String, ? super Optional<Indicator>>) change -> {
            if (change.wasAdded()) {
                change.getValueAdded().get().getProgressBar().setProgress(0);
                change.getValueAdded().get().getProgressBar().setVisible(true);
                change.getValueAdded().get().getProgressBar().getStyleClass().add("indicatorDownloads");
            } else if (change.wasRemoved()) {
                change.getValueRemoved().get().getProgressBar().setVisible(false);
            }
        });
    }
}
