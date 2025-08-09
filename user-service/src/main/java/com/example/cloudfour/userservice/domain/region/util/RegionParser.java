package com.example.cloudfour.userservice.domain.region.util;

import java.util.List;

public final class RegionParser {

    private static final List<String> SI_SUFFIX = List.of("시", "도");
    private static final List<String> GU_SUFFIX = List.of("구", "군");
    private static final List<String> DONG_SUFFIX = List.of("동", "읍", "면", "리");

    private RegionParser() {}

    public record RegionParts(String si, String gu, String dong) {}

    public static RegionParts parse(String fullAddress) {
        if (fullAddress == null) {
            return null;
        }
        String normalized = fullAddress.trim().replaceAll("\\s+", " ");
        String[] tokens = normalized.split(" ");
        if (tokens.length < 3) {
            // Fallback: cannot reliably parse
            return null;
        }

        String si = null, gu = null, dong = null;
        for (String token : tokens) {
            if (si == null && endsWithAny(token, SI_SUFFIX)) { si = token; continue; }
            if (gu == null && endsWithAny(token, GU_SUFFIX)) { gu = token; continue; }
            if (dong == null && endsWithAny(token, DONG_SUFFIX)) { dong = token; continue; }
        }

        if (si == null) si = tokens[0];
        if (gu == null) gu = tokens.length > 1 ? tokens[1] : null;
        if (dong == null) dong = tokens.length > 2 ? tokens[2] : null;

        if (si == null || gu == null || dong == null) {
            return null;
        }
        return new RegionParts(si, gu, dong);
    }

    private static boolean endsWithAny(String token, List<String> suffixes) {
        for (String s : suffixes) {
            if (token.endsWith(s)) return true;
        }
        return false;
    }
}

