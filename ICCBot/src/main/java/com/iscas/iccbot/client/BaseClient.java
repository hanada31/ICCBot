package com.iscas.iccbot.client;

import com.iscas.iccbot.client.statistic.model.StatisticResult;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;

import java.io.IOException;

/**
 * BaseClient
 *
 * @author hanada
 * @version 2.0
 */
@Slf4j
public abstract class BaseClient {
    protected StatisticResult result;

    public void start() {
        long startMS = System.currentTimeMillis();
        try {
            clientAnalyze();
            clientOutput();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
        log.info(String.format("%s took %.2f seconds", this.getClass().getName(),
                (System.currentTimeMillis() - startMS) / 1000.0));
    }

    protected abstract void clientAnalyze();

    public abstract void clientOutput() throws IOException, DocumentException;

    public StatisticResult getResult() {
        return result;
    }
}