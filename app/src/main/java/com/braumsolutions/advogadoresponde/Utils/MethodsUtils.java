package com.braumsolutions.advogadoresponde.Utils;

import android.util.Log;

import com.github.thunder413.datetimeutils.DateTimeUnits;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodsUtils {

    public static boolean validEmail(String email) {
        boolean isEmailIdValid = false;
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailIdValid = true;
            }
        }
        return isEmailIdValid;
    }

    public static String addMask(final String textoAFormatar, final String mask) {
        String formatado = "";
        int i = 0;
        // vamos iterar a mascara, para descobrir quais caracteres vamos adicionar e quando...
        for (char m : mask.toCharArray()) {
            if (m != '#') { // se não for um #, vamos colocar o caracter informado na máscara
                formatado += m;
                continue;
            }
            // Senão colocamos o valor que será formatado
            try {
                formatado += textoAFormatar.charAt(i);
            } catch (Exception e) {
                break;
            }
            i++;
        }
        return formatado;
    }

    public static String getDateTime() {
        DateTimeUtils.setTimeZone("GMT-2");
        return DateTimeUtils.formatDate(new Date());
    }

    public static String getDateTimeMessage(String date) {
        //TODO: FAZER PARA MAIS DE 30 DIAS
        if (DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.DAYS) >= 1 && DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.DAYS) <= 30) {
            String day = String.valueOf(DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.DAYS));
            if (DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.DAYS) < 10) {
                day = "0" + day;
                return String.format("%s dia", day);
            } else {
                return String.format("%s dias", day);
            }
        } else if (DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.HOURS) >= 1 && DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.HOURS) <= 24) {
            String hour = String.valueOf(DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.HOURS));
            if (DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.HOURS) < 10) {
                hour = "0" + hour;
                return String.format("%s hora", hour);
            } else {
                return String.format("%s horas", hour);
            }
        } else if (DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.MINUTES) >= 1 && DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.MINUTES) <= 60) {
            String min = String.valueOf(DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.MINUTES));
            if (DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.MINUTES) < 10) {
                min = "0" + min;
            }
            return String.format("%s min", min);
        } else if (DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.SECONDS) >= 0 && DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.SECONDS) <= 60) {
            String seg = String.valueOf(DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.SECONDS));
            if (DateTimeUtils.getDateDiff(getDateTime(), date, DateTimeUnits.SECONDS) < 10) {
                seg = "0" + seg;
            }
            return String.format("%s seg", seg);
        } else {
            return "Agora";
        }

    }



}
