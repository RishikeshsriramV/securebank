package com.securebank.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.securebank.exception.BankException;

/** Small helpers for reading and validating request parameters. */
public final class InputUtil {

    private InputUtil() { }

    public static String clean(String s) {
        return s == null ? "" : s.trim();
    }

    public static long parseLong(String s, String label) throws BankException {
        try {
            return Long.parseLong(clean(s));
        } catch (NumberFormatException e) {
            throw new BankException("Please enter a valid " + label);
        }
    }

    public static double parseAmount(String s) throws BankException {
        try {
            double v = Double.parseDouble(clean(s));
            if (Double.isNaN(v) || Double.isInfinite(v)) {
                throw new NumberFormatException();
            }
            return Math.round(v * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            throw new BankException("Please enter a valid amount");
        }
    }

    /** Post-Redirect-Get helper: moves the one-time message from session to request. */
    public static void moveFlashToRequest(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("flash") != null) {
            req.setAttribute("flash", s.getAttribute("flash"));
            s.removeAttribute("flash");
        }
    }
}
