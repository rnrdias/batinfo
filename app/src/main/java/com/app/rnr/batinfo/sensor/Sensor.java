package com.app.rnr.batinfo.sensor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by RNR on 06/01/2018.
 */

public class Sensor {
    final String TAG = this.getClass().getName();

    private SensorRunnable sr;
    private String file;
    private String unidade;
    private int casasDecimal = -1;

    public Sensor(String file, String unidade, int casasDecimal) {
        this.file = file;
        this.unidade = unidade;
        this.casasDecimal = casasDecimal;
    }

    public Sensor(SensorRunnable sr, String unidade, int casasDecimal) {
        this.sr = sr;
        this.unidade = unidade;
        this.casasDecimal = casasDecimal;
    }

    String getValue() {
        String valor = "";
        String v = "";
        if (sr != null) {
            v = sr.getValue();
        } else if (file != null) {
            v = executeCommand("cat " + file);
        }
  /*
        if (digitos >= 0) {
            int dif = digitos - v.length();
            if (dif != 0) {
                while (dif-- != 0) {
                    v = '0' + v;
                }
            }
        }
*/
        if (v.length() == 0) {
            valor = "Undefined";
            return valor;
        }

        if (casasDecimal >= 0) {
            int dif = v.length() - casasDecimal - 1;
            if (dif < 0) {
                while (dif++ != 0) {
                    v = '0' + v;
                }
            }
        }

        for (int i = 0; i < v.length(); i++) {
            valor += v.charAt(i);
            if (v.length() - i == casasDecimal + 1) {
                valor += ".";
            }
        }

        valor += unidade;

        /*if (v.length() != 0) {
            valor += unidade;
        } else {
            valor = "Undefined";
        }*/
        Log.d(TAG, ": " + valor);

        return valor;
    }

    private String executeCommand(String command) {
        Process p;
        StringBuilder output = new StringBuilder();
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            p.waitFor();
            String line;
            if ((line = reader.readLine()) != null) {
                output.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
