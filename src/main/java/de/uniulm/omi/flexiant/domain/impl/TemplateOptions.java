package de.uniulm.omi.flexiant.domain.impl;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by daniel on 13.01.15.
 */
public class TemplateOptions {

    private final Set<String> networks;

    public TemplateOptions() {
        networks = new HashSet<String>();
    }

    public Set<String> getNetworks() {
        return networks;
    }
}
