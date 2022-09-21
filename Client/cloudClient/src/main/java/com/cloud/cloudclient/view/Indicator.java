package com.cloud.cloudclient.view;

import javafx.scene.control.ProgressBar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Indicator {
    private ProgressBar progressBar;
    private double cumulative;
    private TypeOfLoad type;

    public void cumulative() {
        progressBar.setProgress(progressBar.getProgress() + cumulative);
    }

}
