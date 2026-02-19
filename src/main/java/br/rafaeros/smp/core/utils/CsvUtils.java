package br.rafaeros.smp.core.utils;

import java.io.StringWriter;
import java.util.Locale;

public class CsvUtils {

    private CsvUtils() {}

    public static String formatDouble(Double value) {
        if (value == null) return "0,00";
        return String.format(Locale.of("pt", "BR"), "%.2f", value);
    }
    public static String escapeCsv(String value) {
        if (value == null) return "\"\"";
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    public static byte[] generateCsvBytes(StringWriter sw) {
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] csvBytes = sw.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] finalBytes = new byte[bom.length + csvBytes.length];
        
        System.arraycopy(bom, 0, finalBytes, 0, bom.length);
        System.arraycopy(csvBytes, 0, finalBytes, bom.length, csvBytes.length);
        
        return finalBytes;
    }
}