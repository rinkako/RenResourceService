/*
 * Project Ren @ 2018
 * Rinkako, Ariana, Gordan. SYSU SDCS.
 */
package org.sysu.renResourcing.filter;

import org.sysu.renResourcing.context.ParticipantContext;
import org.sysu.renResourcing.context.WorkitemContext;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Author: Rinkako
 * Date  : 2018/2/9
 * Usage : Filters a distribution set by doing nothing.
 */
public class EmptyFilter extends RFilter {

    /**
     * Filter description.
     */
    public static final String Descriptor = "The Empty filter do " +
            "nothing to a candidate set, return it as nothing happened.";

    /**
     * Create a new filter.
     *
     * @param id          unique id for selector fetching
     * @param type        type name string
     * @param description selector description text
     * @param args        parameter dictionary in HashMap
     */
    public EmptyFilter(String id, String type, String description, HashMap<String, String> args) {
        super(id, type, EmptyFilter.Descriptor, args);
    }

    /**
     * Perform filter on the candidate set.
     *
     * @param candidateSet candidate participant set
     * @param context      workitem context
     * @return filtered participant set
     */
    @Override
    public HashSet<ParticipantContext> PerformFilter(HashSet<ParticipantContext> candidateSet, WorkitemContext context) {
        return new HashSet<>(candidateSet);
    }
}
